package rip.deadcode.oboro.context

import rip.deadcode.oboro.InitContext
import rip.deadcode.oboro.Shell


fun init(context: InitContext) {
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
        done < <(oboro-runner "${"$"}@")
    }
    
    # Add following to your .bash_profile
    #
    # eval "${"$"}(oboro-runner init bash)"
""".trimIndent()

private val powershell = """
    # Oboro helpers for PowerShell
    
    function oboro {
        ${"$"}env = & oboro-runner.exe ${"$"}args
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
