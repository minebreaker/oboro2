package rip.deadcode.oboro

import rip.deadcode.oboro.context.init
import rip.deadcode.oboro.context.load
import java.nio.file.FileSystems


fun main(args: Array<String>) {
    main(
        args,
        Dependencies(
            fileSystem = FileSystems.getDefault(),
            environments = System.getenv(),
            home = System.getProperty("user.home"),
            pathSeparator = System.getProperty("path.separator")
        )
    )
}

fun main(args: Array<String>, dependencies: Dependencies) {

    when (val context = parseArgs(args, dependencies)) {
        is InitContext -> init(context)
        is LoadContext -> load(context)
        HelpContext    -> TODO()
    }
}
