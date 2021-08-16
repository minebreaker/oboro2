package rip.deadcode.oboro

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.*
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

enum class Os {
    Unix, Windows
}

fun setUpFileSystem(os: Os, block: () -> Unit) {
    val currentFs = fileSystem
    val currentProperty = System.getProperties()

    System.setProperties(Properties(System.getProperties()))
    when (os) {
        Os.Unix    -> {
            fileSystem = Jimfs.newFileSystem(Configuration.unix())
            System.setProperty("user.home", "/home/username")
            System.setProperty("path.separator", ":")
        }
        Os.Windows -> {
            fileSystem = Jimfs.newFileSystem(Configuration.windows())
            System.setProperty("user.home", "C:\\User\\username")
            System.setProperty("path.separator", ";")
        }
    }

    block()

    fileSystem = currentFs
    System.setProperties(currentProperty)
}

private val testGson = Gson()

fun saveJsonHome(filename: String, model: Map<String, Any>) {
    saveJson(fileSystem.getPath(System.getProperty("user.home")).resolve(".oboro").resolve(filename), model)
}

fun saveJson(path: Path, model: Map<String, Any>) {
    path.parent.createDirectories()
    path.writeText(testGson.toJson(model))
}

fun assertOutput(result: String, expected: String) {
    // Adds line break to the end of the expected
    // and replace line break to the default of the os running the test
    assertThat(result).isEqualTo((expected + "\n").replace("\n", System.getProperty("line.separator")))
}
