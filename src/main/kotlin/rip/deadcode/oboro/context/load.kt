package rip.deadcode.oboro.context

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import rip.deadcode.oboro.LoadContext
import rip.deadcode.oboro.getOboroHome
import rip.deadcode.oboro.model.Conflict
import rip.deadcode.oboro.model.Conflict.Overwrite
import rip.deadcode.oboro.model.Profile
import rip.deadcode.oboro.model.Value
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Path
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success


fun load(context: LoadContext) {

    val profile = loadProfileFile(context.profile)
    profile.fold(
        onSuccess = {
            it.variable.forEach { v ->
                val valueStr = if (v.value.size == 1) {
                    v.value[0]
                } else {
                    v.value.joinToString(pathSeparator)
                }
                println("${v.key}=${valueStr}")
            }
        },
        onFailure = {
            System.err.println("Error:profile \"${context.profile}\" not found.")
        }
    )
}

fun loadProfileFile(profile: String): Result<Profile> {

    val home = getOboroHome()
    val json = home.resolve("${profile}.json")
    val path = when {
        Files.exists(json) -> json
        else               -> TODO()
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

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Profile {
        val variableObj = json.asJsonObject.get("variable")
        // TODO proper error handling
        val variables = variableObj.asJsonObject.entrySet().map { (k, v) ->
            when {
                // TODO proper error handling
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
//        Append.command -> Append
//        Skip.command -> Skip
//        Error.command -> Error
        else              -> throw IllegalStateException()
    }
}

private val pathSeparator: String = System.getProperty("path.separator")
