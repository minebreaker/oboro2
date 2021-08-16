package rip.deadcode.oboro.context

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import rip.deadcode.oboro.LoadContext
import rip.deadcode.oboro.ProfileNotFound
import rip.deadcode.oboro.getCurrent
import rip.deadcode.oboro.getOboroHome
import rip.deadcode.oboro.model.Conflict
import rip.deadcode.oboro.model.Conflict.Append
import rip.deadcode.oboro.model.Conflict.Error
import rip.deadcode.oboro.model.Conflict.Overwrite
import rip.deadcode.oboro.model.Conflict.Skip
import rip.deadcode.oboro.model.Profile
import rip.deadcode.oboro.model.Value
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Path
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.system.exitProcess


fun load(context: LoadContext) {

    val profile = loadProfileFile(context.profile)
    profile.fold(
        onSuccess = {
            it.variable.forEach { valueObj ->

                val env = System.getenv(valueObj.key)
                val valueStr = valueObj.value.joinToString(pathSeparator)

                when (valueObj.conflict) {
                    Overwrite ->
                        println("${valueObj.key}=${valueStr}")
                    Append    -> {
                        val valueWithEnv = if (env == null) {
                            valueStr
                        } else {
                            env + pathSeparator + valueStr
                        }
                        println("${valueObj.key}=${valueWithEnv}")
                    }
                    Error     -> {
                        System.err.println("Error: environment variable already set: current=\"${env}\"")
                        exitProcess(1)
                    }
                    Skip      -> Unit  // Do nothing
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

fun loadProfileFile(profile: String): Result<Profile> {

    val current = getCurrent()
    val currentJson = current.resolve("${profile}.json")
    val home = getOboroHome()
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
                v.isJsonObject -> {
                    val w = v.asJsonObject
                    val value = w["value"].asString
                    val conflict = parseConflict(w["conflict"]?.asString ?: Overwrite.command)
                    Value(k, listOf(value), conflict)
                }
                v.isJsonArray ->
                    Value(k, v.asJsonArray.map { it.asString }, Overwrite)
                v.isJsonPrimitive ->
                    Value(k, listOf(v.asString), Overwrite)
                else -> throw IllegalStateException()
            }
        }.toList()

        return Profile(variables)
    }
}

fun parseConflict(s: String): Conflict {
    return when (s) {
        Overwrite.command -> Overwrite
        Append.command    -> Append
        Skip.command      -> Skip
        Error.command     -> Error
        else              -> throw IllegalStateException("Unsupported conflict strategy: \"${s}\"")
    }
}

private val pathSeparator: String = System.getProperty("path.separator")
