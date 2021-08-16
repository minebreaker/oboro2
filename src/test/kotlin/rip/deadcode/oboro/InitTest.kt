package rip.deadcode.oboro

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import rip.deadcode.oboro.context.bash
import rip.deadcode.oboro.context.powershell


class InitTest {

    @Test
    fun testInitBash() {

        val result = redirectingStdout {
            main(arrayOf("init", "bash"))
        }

        assertThat(result).isEqualTo(bash)
    }

    @Test
    fun testInitPowerShell() {

        val result = redirectingStdout {
            main(arrayOf("init", "powershell"))
        }

        assertThat(result).isEqualTo(powershell)
    }
}
