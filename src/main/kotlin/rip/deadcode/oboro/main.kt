package rip.deadcode.oboro

import rip.deadcode.oboro.context.init
import rip.deadcode.oboro.context.load


fun main(args: Array<String>) {

    when (val context = parseArgs(args)) {
        is InitContext -> init(context)
        is LoadContext -> load(context)
        HelpContext    -> TODO()
    }
}
