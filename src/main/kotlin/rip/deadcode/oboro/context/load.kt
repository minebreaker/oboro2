package rip.deadcode.oboro.context

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import rip.deadcode.oboro.LoadContext
import rip.deadcode.oboro.fileSystem
import rip.deadcode.oboro.model.Conflict
import rip.deadcode.oboro.model.Conflict.Overwrite
import rip.deadcode.oboro.model.Profile
import rip.deadcode.oboro.model.Value
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Path


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
            System.err.println("Error: ")
        }
    )
}

fun loadProfileFile(profile: String): Result<Profile> {

    val path = getOboroHome().resolve(profile)
    return parseProfile(path)
}

fun getOboroHome(): Path {
    val userHome = System.getProperty("user.home")!!  // User's home directory; Guaranteed to have the value
    return fileSystem.getPath(userHome, ".oboro").toAbsolutePath().normalize()
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
        Result.success(profile)
    } catch (e: Exception) {
        Result.failure(e)
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
        else -> throw IllegalStateException()
    }
}

val pathSeparator: String = System.getProperty("path.separator")
