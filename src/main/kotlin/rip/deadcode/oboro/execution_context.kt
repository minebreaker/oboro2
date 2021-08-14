package rip.deadcode.oboro


sealed interface ExecutionContext

data class InitContext(val shell: Shell) : ExecutionContext {
    companion object {
        const val command: String = "init"
    }
}

data class LoadContext(val profile: String) : ExecutionContext {
    companion object {
        const val command: String = "load"
    }
}

object HelpContext : ExecutionContext

enum class Shell {
    Bash {
        override val command: String = "bash"
    },
    PowerShell {
        override val command: String = "powershell"
    }
    ;

    abstract val command: String
}
