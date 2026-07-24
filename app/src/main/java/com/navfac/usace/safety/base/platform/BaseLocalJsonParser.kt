package com.navfac.usace.safety.base.platform

import android.content.Context
import java.io.IOException
import java.io.InputStream
import kotlin.text.Charsets.UTF_8


object BaseLocalJsonParser {
    /*
    fun parseJSONData(context: Context, jsonFileName: String): String? {
        val JSONString: String?
        JSONString = try {
            val inputStream: InputStream = context.assets.open(jsonFileName)
            val sizeOfJSONFile: Int = inputStream.available()
            val bytes = ByteArray(sizeOfJSONFile)
            inputStream.read(bytes)
            inputStream.close()
            String(bytes, UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return JSONString
    }
    */
}