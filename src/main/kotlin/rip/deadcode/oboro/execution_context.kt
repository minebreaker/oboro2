package rip.deadcode.oboro

import java.nio.file.FileSystem


sealed interface ExecutionContext

data class InitContext(val dependencies: Dependencies, val shell: Shell) : ExecutionContext {
    companion object {
        const val command: String = "init"
    }
}

data class LoadContext(val dependencies: Dependencies, val profile: String) : ExecutionContext {
    companion object {
        const val command: String = "load"
    }
}

object VersionContext : ExecutionContext {
    const val command: String = "version"
}

object HelpContext : ExecutionContext

data class Dependencies(
    val fileSystem: FileSystem,
    val environments: Map<String, String>,
    val home: String,
    val pathSeparator: String
)

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
