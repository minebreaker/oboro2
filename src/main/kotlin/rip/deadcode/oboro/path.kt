package rip.deadcode.oboro

import java.nio.file.Path


fun getCurrent(): Path {
    val current = System.getProperty("user.home")
    return fileSystem.getPath(current).toAbsolutePath().normalize()
}

fun getOboroHome(): Path {
    val userHome = System.getProperty("user.home")  // User's home directory; Guaranteed to have the value
    return fileSystem.getPath(userHome, ".oboro").toAbsolutePath().normalize()
}
