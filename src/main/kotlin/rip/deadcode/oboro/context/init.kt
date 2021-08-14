package rip.deadcode.oboro.context

import rip.deadcode.oboro.InitContext
import rip.deadcode.oboro.Shell
import rip.deadcode.oboro.getOboroHome
import java.io.IOException
import java.nio.file.Files


fun init(context: InitContext) {

    // Creates home directory if not exist
    try {
        val home = getOboroHome()
        Files.createDirectories(home)
    } catch (e: IOException) {
        // Print stacktrace and keep going
        e.printStackTrace()
    }

    // Output init scripts
    val script = when (context.shell) {
        Shell.Bash       -> bash
        Shell.PowerShell -> powershell
    }
    print(script)
}


private val bash = """
    # Oboro helpers for bash
    
    function oboro() {
        while read e; do
            export "${"$"}e"
        done < <(oboro-runner load "${"$"}@")
    }
    
    # Add following to your .bash_profile
    #
    # eval "${"$"}(oboro-runner init bash)"
""".trimIndent()

private val powershell = """
    # Oboro helpers for PowerShell
    
    function oboro {
        ${"$"}env = & oboro-runner.exe load ${"$"}args
        ${"$"}env = [string[]] ${"$"}env
    
        foreach (${"$"}e in ${"$"}env) {
            ${"$"}k, ${"$"}v = ${"$"}e -split "=", 2
            [Environment]::SetEnvironmentVariable(${"$"}k, ${"$"}v)
        }
    }
    
    # Add following to your ${"$"}home\Documents\WindowsPowerShell\Microsoft.PowerShell_profile.ps1
    #
    # Invoke-Expression (& oboro-runner.exe init powershell)
""".trimIndent()
