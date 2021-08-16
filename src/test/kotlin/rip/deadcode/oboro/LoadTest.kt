package rip.deadcode.oboro

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import rip.deadcode.izvestia.Core.args
import rip.deadcode.izvestia.Core.test
import rip.deadcode.oboro.context.bash
import rip.deadcode.oboro.context.powershell

class LoadTest {

    @TestFactory
    fun testLoad() = test("load").parameterized(Os.Unix, Os.Windows).run { os ->
        setUpFileSystem(os) {
            saveJsonHome(
                "test.json",
                mapOf(
                    "variable" to mapOf(
                        "key1" to "value1",
                        "key2" to arrayOf("value2"),
                        "key3" to mapOf(
                            "value" to "value3",
                            "conflict" to "overwrite"
                        )
                    )
                )
            )

            val result = redirectingStdout {
                main(arrayOf("load", "test"))
            }

            assertOutput(
                result, """
                key1=value1
                key2=value2
                key3=value3
            """.trimIndent()
            )
        }
    }
}
