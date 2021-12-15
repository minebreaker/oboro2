package rip.deadcode.oboro

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText


fun redirectingStdout(block: () -> Unit): String {

    val currentOut = System.out

    val baos = ByteArrayOutputStream()
    val ps = PrintStream(baos)
    System.setOut(ps)

    block()

    System.setOut(currentOut)

    return baos.toString(StandardCharsets.UTF_8)
}

fun dependencies(os: TestTarget, environments: Map<String, String> = mapOf()): Dependencies {
    return when (os) {
        TestTarget.Unix    -> {
            Dependencies(
                fileSystem = Jimfs.newFileSystem(Configuration.unix()),
                environments = environments,
                home = "/home/username",
                pathSeparator = ":",
                os = Os.Linux
            )
        }
        TestTarget.Windows -> Dependencies(
            fileSystem = Jimfs.newFileSystem(Configuration.windows()),
            environments = environments,
            home = "C:\\User\\username",
            pathSeparator = ";",
            os = Os.Windows
        )
    }
}

enum class TestTarget {
    Unix, Windows
}

private val testGson = Gson()

fun saveJsonHome(dependencies: Dependencies, filename: String, model: Map<String, Any>) {
    saveJson(dependencies.fileSystem.getPath(dependencies.home).resolve(".oboro").resolve(filename), model)
}

fun saveJson(path: Path, model: Map<String, Any>) {
    path.parent.createDirectories()
    path.writeText(testGson.toJson(model))
}

fun assertOutput(result: String, expected: String) {
    // Adds line break to the end of the expected
    // and replace line break to the default of the os running the test
    assertThat(result).isEqualTo((expected.trimIndent() + "\n").replace("\n", System.getProperty("line.separator")))
}
