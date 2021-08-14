package rip.deadcode.oboro

import rip.deadcode.oboro.context.init
import rip.deadcode.oboro.context.load


fun main(args: Array<String>) {

    val context = parseArgs(args)
    when (context) {
        is InitContext -> init(context)
        is LoadContext -> load(context)
        HelpContext    -> TODO()
    }
}
