package com.navfac.usace.safety.base.extension

import android.content.Context
import com.google.common.io.CharStreams
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

fun String.toJson(context: Context): String? {
    return try {
        val fis: InputStream = context.assets.open(this)
        val len: Int = fis.available()
        val bytes = ByteArray(len)
        fis.read(bytes)
        fis.close()
        String(bytes, Charsets.UTF_8)
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    }
}

fun String.isSubsection(): Boolean = Character.isLetter(this[2]) || Character.isLetter(this[1])

@Throws(IOException::class)
fun readFileAssetIntoString(context: Context, fileLocation: String?): String? { //assets: AssetManager
    val fis = context.assets.open(fileLocation!!)
    val isr = InputStreamReader(fis)
    val string: String = CharStreams.toString(isr)
    isr.close()
    fis.close()
    return string
}

fun String.extractSubsection(): String {
    var lastDigitIndex = 0
    for (c in this.toCharArray()) {
        if (c == '_') break else lastDigitIndex++
    }
    return this.substring(0, lastDigitIndex)
}

fun String.extractSubsectionOrdinal(/*filename: String*/): Char {
    var underscoreIndex = 0
    for (c in this.toCharArray()) {
        if (c == '_') break else underscoreIndex++
    }
    return this[underscoreIndex - 1].uppercaseChar() //toUpperCase()
}

fun String.extractSection(): Int {
    var lastDigitIndex = 0
    for (c in this.toCharArray()) {
        if (Character.isLetter(c) || c == '_') break else lastDigitIndex++
    }
    return this.substring(0, lastDigitIndex).toInt()
}

fun String.extractAppendicesFirstPage(): Boolean {
    if (this[2].toString() == "1") {
        return true
    }
    return false
}

fun String.checkSectionsFirstPage(): Boolean {
    var underscoreIndex = 0
    for (c in this.toCharArray()) {
        if (c == '_') break else underscoreIndex++
    }

    if (this[underscoreIndex + 1].toString() == "1") {
        return true
    }
    return false
}

fun String.extractSectionsFirstPage(): String {
    var underscoreIndex = 0
    for (c in this.toCharArray()) {
        if (c == '_') break else underscoreIndex++
    }
    return this.drop(underscoreIndex + 3).dropLast(5)
}

fun Int.formatNumber():String{
    return if (this < 10) "0$this" else "$this"
}


@Throws(IOException::class)
fun String.checkFileExistInAssets(context: Context, fileLocation: String): Boolean{ //assets: AssetManager

    return context.assets.list(fileLocation)?.contains(this) ?: false
}




