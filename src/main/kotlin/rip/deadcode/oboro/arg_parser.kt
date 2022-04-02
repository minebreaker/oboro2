package rip.deadcode.oboro

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options


val options = Options()
    .addOption("v", "version", false, "Shows version")
    .addOption("h", "help", false, "Shows help")

fun parseArgs(args: Array<String>, dependencies: Dependencies): ExecutionContext {
    val parser = DefaultParser()
    val commands = parser.parse(options, args)

    return when (commands.args.getOrNull(0)) {
        InitContext.command    -> parseInitCommand(dependencies, commands)
        LoadContext.command    -> parseLoadCommand(dependencies, commands)
        ListContext.command    -> ListContext(dependencies)
        VersionContext.command -> VersionContext
        else                   ->
            when {
                (commands.hasOption("version")) -> VersionContext
                else                            -> HelpContext
            }
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
        Shell.Bash.command       -> Result.success(Shell.Bash)
        Shell.PowerShell.command -> Result.success(Shell.PowerShell)
        else                     -> Result.failure(InvalidCommand("Unsupported shell name: \"${str}\""))
    }
}
