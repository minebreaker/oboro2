package rip.deadcode.oboro.model


data class Profile(
    val variable: List<Value>
)

data class Value(
    val key: String,
    val value: List<String>,
    val conflict: Conflict
)

enum class Conflict {
    Overwrite {
        override val command: String = "overwrite"
    },
    Append {
        override val command: String = "append"
    },
    Skip {
        override val command: String = "skip"
    },
    Error {
        override val command: String = "error"
    }
    ;

    abstract val command: String
}
