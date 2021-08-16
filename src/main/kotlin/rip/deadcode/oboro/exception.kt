package rip.deadcode.oboro


class InvalidCommand(message: String) : RuntimeException(message)

class ProfileNotFound(message: String) : RuntimeException(message)

class EnvironmentVariableAlreadySet(message: String) : RuntimeException(message)
