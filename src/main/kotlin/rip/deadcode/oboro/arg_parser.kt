package rip.deadcode.oboro

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options


private val options = Options()

fun parseArgs(args: Array<String>, dependencies: Dependencies): ExecutionContext {
    val parser = DefaultParser()
    val commands = parser.parse(options, args)

    val command = commands.args.getOrNull(0)
    return when (command) {
        InitContext.command -> parseInitCommand(dependencies, commands)
        LoadContext.command -> parseLoadCommand(dependencies, commands)
        else                -> HelpContext
    }
}

private fun parseInitCommand(dependencies: Dependencies, commands: CommandLine): ExecutionContext {
    val subcommand = commands.args.getOrNull(1) ?: return HelpContext

    return parseShell(subcommand).fold(
        onSuccess = {
            InitContext(dependencies, it)
        },
        onFailure = {
            HelpContext
        }
    )
}

private fun parseLoadCommand(dependencies: Dependencies, commands: CommandLine): ExecutionContext {
    val profile = commands.args.getOrNull(1) ?: return HelpContext

    return LoadContext(dependencies, profile)
}

private fun parseShell(str: String): Result<Shell> {
    return when (str) {
        Shell.Bash.command -> Result.success(Shell.Bash)
        Shell.PowerShell.command -> Result.success(Shell.PowerShell)
        else -> Result.failure(InvalidCommand("Unsupported shell name: \"${str}\""))
    }
}
