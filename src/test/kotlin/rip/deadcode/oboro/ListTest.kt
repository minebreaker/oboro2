package rip.deadcode.oboro

import org.junit.jupiter.api.Test

class ListTest {

    @Test
    fun testList() {

        val dependencies = dependencies(
            TestTarget.Unix,
            mapOf("key" to "value")
        )

        saveJsonHome(
            dependencies,
            "test1.json",
            mapOf("variable" to mapOf<String, Any>())
        )
        saveJsonHome(
            dependencies,
            "test2.json",
            mapOf("variable" to mapOf<String, Any>())
        )

        val result = redirectingStdout {
            main(arrayOf("list"), dependencies)
        }

        assertOutput(result, "test1\ntest2\n")
    }
}
