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

data class ListContext(val dependencies: Dependencies) : ExecutionContext {
    companion object {
        const val command: String = "list"
    }
}

object VersionContext : ExecutionContext {
    const val command: String = "version"
}

object HelpContext : ExecutionContext

data class Dependencies(
    val fileSystem: FileSystem,
    val environments: Map<String, String>,
    val os: Os,
    val home: String,
    val pathSeparator: String
)

enum class Os {
    Windows {
        override val command: String = "windows"
    },
    Linux {
        override val command: String = "linux"
    },
    Mac {
        override val command: String = "mac"
    },
    Unknown {
        override val command: String = "unknown"
    }
    ;

    abstract val command: String
}

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
