package rip.deadcode.oboro

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options


private val options = Options()

fun parseArgs(args: Array<String>): ExecutionContext {
    val parser = DefaultParser()
    val commands = parser.parse(options, args)

    val command = commands.args.getOrNull(0)
    return when (command) {
        InitContext.command -> parseInitCommand(commands)
        LoadContext.command -> parseLoadCommand(commands)
        else -> HelpContext
    }
}

private fun parseInitCommand(commands: CommandLine): ExecutionContext {
    val subcommand = commands.args.getOrNull(1)

    if (subcommand == null) {
        return HelpContext
    }

    return parseShell(subcommand).fold(
        onSuccess = {
            InitContext(it)
        },
        onFailure = {
            HelpContext
        }
    )
}

private fun parseLoadCommand(commands: CommandLine): ExecutionContext {
    val profile = commands.args.getOrNull(1)

    if (profile == null) {
        return HelpContext
    }

    return LoadContext(profile)
}

private fun parseShell(str: String): Result<Shell> {
    return when (str) {
        Shell.Bash.command -> Result.success(Shell.Bash)
        else -> Result.failure(InvalidCommand("Unsupported shell name: \"${str}\""))
    }
}
