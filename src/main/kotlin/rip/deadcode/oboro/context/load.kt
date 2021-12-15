package rip.deadcode.oboro.context

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import rip.deadcode.oboro.EnvironmentVariableAlreadySet
import rip.deadcode.oboro.LoadContext
import rip.deadcode.oboro.Os
import rip.deadcode.oboro.ProfileNotFound
import rip.deadcode.oboro.getOboroHome
import rip.deadcode.oboro.model.Conflict
import rip.deadcode.oboro.model.Conflict.Append
import rip.deadcode.oboro.model.Conflict.Error
import rip.deadcode.oboro.model.Conflict.Insert
import rip.deadcode.oboro.model.Conflict.Overwrite
import rip.deadcode.oboro.model.Conflict.Skip
import rip.deadcode.oboro.model.Profile
import rip.deadcode.oboro.model.Value
import rip.deadcode.oboro.utils.getIgnoreCase
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Path
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success


fun load(context: LoadContext) {

    val pathSeparator = context.dependencies.pathSeparator
    val environments = context.dependencies.environments

    val profile = loadProfileFile(context)
    profile.fold(
        onSuccess = {
            it.variable.forEach { valueObj ->

                val env = environments.getEnv(valueObj.key, context)
                val valueStr = valueObj.value.joinToString()

                when (valueObj.conflict) {
                    Overwrite      ->
                        println("${valueObj.key}=${valueStr}")
                    Insert, Append -> {
                        val valueWithEnv = when {
                            env == null                 -> valueStr
                            valueObj.conflict == Append -> env + pathSeparator + valueStr
                            else  /* Insert */          -> valueStr + pathSeparator + env
                        }
                        println("${valueObj.key}=${valueWithEnv}")
                    }
                    Error          -> {
                        throw EnvironmentVariableAlreadySet("Error: environment variable already set: current=\"${env}\"")
                    }
                    Skip           -> Unit  // Do nothing
                }

            }
        },
        onFailure = {
            when (it) {
                is ProfileNotFound -> System.err.println(it.message)
                else               -> it.printStackTrace()
            }
        }
    )
}

fun loadProfileFile(context: LoadContext): Result<Profile> {

    val dependencies = context.dependencies
    val profile = context.profile

    val fs = dependencies.fileSystem
    val currentJson = fs.getPath("${profile}.json")
    val home = getOboroHome(dependencies)
    val homeJson = home.resolve("${profile}.json")
    val path = when {
        Files.exists(currentJson) -> currentJson
        Files.exists(homeJson)    -> homeJson
        else                      -> throw ProfileNotFound("Profile not found: \"${profile}\"")
    }

    return parseProfile(path)
}

fun parseProfile(path: Path): Result<Profile> {

    val gson = GsonBuilder()
        .registerTypeAdapter(Profile::class.java, ProfileDeserializer())
        .create()

    // Always use UTF-8
    return try {
        val profile = Files.newBufferedReader(path).use { reader ->
            gson.fromJson(reader, Profile::class.java)
        }
        success(profile)
    } catch (e: Exception) {
        failure(e)
    }
}

private class ProfileDeserializer : JsonDeserializer<Profile> {

    // TODO proper error handling
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Profile {
        val variableObj = json.asJsonObject.get("variable")
        val variables = variableObj.asJsonObject.entrySet().map { (k, v) ->
            when {
                v.isJsonObject    -> {
                    val w = v.asJsonObject
                    val value = w["value"].asString
                    val conflict = parseConflict(w["conflict"]?.asString ?: Overwrite.command)
                    Value(k, listOf(value), conflict)
                }
                v.isJsonArray     ->
                    Value(k, v.asJsonArray.map { it.asString }, Overwrite)
                v.isJsonPrimitive ->
                    Value(k, listOf(v.asString), Overwrite)
                else              -> throw IllegalStateException()
            }
        }.toList()

        return Profile(variables)
    }
}

fun parseConflict(s: String): Conflict {
    return when (s) {
        Overwrite.command -> Overwrite
        Append.command    -> Append
        Insert.command    -> Insert
        Skip.command      -> Skip
        Error.command     -> Error
        else              -> throw IllegalStateException("Unsupported conflict strategy: \"${s}\"")
    }
}

private fun Map<String, String>.getEnv(key: String, context: LoadContext): String? {
    // Windows treats env vars in case-insensitive manner.
    // TODO should be configurable
    return if (context.dependencies.os == Os.Windows) {
        this.getIgnoreCase(key)
    } else {
        this[key]
    }
}
