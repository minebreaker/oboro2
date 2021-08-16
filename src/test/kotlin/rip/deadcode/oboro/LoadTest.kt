package rip.deadcode.oboro

import org.junit.jupiter.api.TestFactory
import rip.deadcode.izvestia.Core.expect
import rip.deadcode.izvestia.Core.test
import java.lang.Exception

class LoadTest {

    @TestFactory
    fun testLoad() = test("load").parameterized(Os.Unix, Os.Windows).run { os ->

        val dependencies = dependencies(os)

        saveJsonHome(
            dependencies,
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
            main(arrayOf("load", "test"), dependencies)
        }

        assertOutput(
            result, """
                key1=value1
                key2=value2
                key3=value3
            """.trimIndent()
        )
    }

    @TestFactory
    fun testLoadCurrentDir() = test("load current directory").parameterized(Os.Unix, Os.Windows).run { os ->

        val dependencies = dependencies(os)

        saveJson(
            dependencies.fileSystem.getPath("./test.json"),
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
            main(arrayOf("load", "test"), dependencies)
        }

        assertOutput(
            result, """
                key1=value1
                key2=value2
                key3=value3
            """.trimIndent()
        )
    }

    @TestFactory
    fun testLoadConflicts() = test("load conflicts").parameterized(Os.Unix, Os.Windows).run { os ->

        val environments = mapOf(
            "key1" to "previous value1",
            "key2" to "previous value2",
            "key3" to "previous value3",
            "key4" to "previous value4"
        )
        val dependencies = dependencies(os, environments)

        saveJsonHome(
            dependencies,
            "test.json",
            mapOf(
                "variable" to mapOf(
                    "key1" to mapOf(
                        "value" to "overwrite",
                        "conflict" to "overwrite"
                    ),
                    "key2" to mapOf(
                        "value" to "append",
                        "conflict" to "append"
                    ),
                    "key3" to mapOf(
                        "value" to "skip",
                        "conflict" to "skip"
                    ),
                    "key4" to mapOf(
                        "value" to "error",
                        "conflict" to "error"
                    )
                )
            )
        )

        val result = redirectingStdout {
            expect {
                main(arrayOf("load", "test"), dependencies)
            }.throwsException(EnvironmentVariableAlreadySet::class.java)
        }

        assertOutput(
            result, """
                key1=overwrite
                key2=previous value2${dependencies.pathSeparator}append
            """.trimIndent()
        )
    }
}
