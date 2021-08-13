package rip.deadcode.oboro.context

import rip.deadcode.oboro.InitContext
import rip.deadcode.oboro.Shell


fun init(context: InitContext) {
    val script = when (context.shell) {
        Shell.Bash -> bash
    }
    print(script)
}


// TODO
val bash = """
    function oboro() {
        while read e; do
            export "${"$"}e"
        done < <(oboro-runner "${"$"}@")
    }
""".trimIndent()
