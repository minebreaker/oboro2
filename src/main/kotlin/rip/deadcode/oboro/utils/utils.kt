package rip.deadcode.oboro.utils

import rip.deadcode.oboro.Os


fun guessOs(): Os {
    val name = System.getProperty("os.name").lowercase()  // We don't care locale
    return when {
        name.contains("win") -> Os.Windows
        name.contains("nux") -> Os.Linux
        name.contains("mac") -> Os.Mac
        else                 -> Os.Unknown
    }
}

fun <T> Map<String, T>.getIgnoreCase(key: String): T? {
    return this.entries.find { it.key.lowercase() == key.lowercase() }?.value
}
