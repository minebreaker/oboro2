package rip.deadcode.oboro.context

import org.apache.commons.cli.HelpFormatter
import rip.deadcode.oboro.options

fun help() {
    println("Oboro - environment variable manager")
    println("https://github.com/minebreaker/oboro2")
    println()
    HelpFormatter().printHelp("oboro-runner", options)
}
