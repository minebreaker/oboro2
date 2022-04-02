package rip.deadcode.oboro.context

import rip.deadcode.oboro.ListContext
import rip.deadcode.oboro.getOboroHome
import java.nio.file.Files
import kotlin.io.path.name


fun list(context: ListContext) {
    val home = getOboroHome(context.dependencies)
    Files.list(home)
        .filter { it.toString().endsWith(".json") }
        .forEach {
            val name = it.fileName.name
            println(name.substring(0, name.length - 5))
        }
}
