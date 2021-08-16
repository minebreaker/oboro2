package rip.deadcode.oboro

import java.nio.file.Path


fun getOboroHome(dependencies: Dependencies): Path {
    return dependencies.fileSystem.getPath(dependencies.home, ".oboro").toAbsolutePath().normalize()
}
