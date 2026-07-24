package com.navfac.usace.safety.base.extension

import java.io.File

fun File.isValidPdf(): Boolean {
    if (!exists() || length() < 5) return false
    return try {
        inputStream().use { input ->
            val header = ByteArray(5)
            input.read(header) == 5 && String(header).startsWith("%PDF-")
        }
    } catch (e: Exception) {
        false
    }
}