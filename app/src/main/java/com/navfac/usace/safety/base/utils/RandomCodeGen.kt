package com.navfac.usace.safety.base.utils

import java.util.*

object RandomCodeGen {
    fun generate(): String {
        val rnd = Random()
        val number = rnd.nextInt(999999)
        return String.format("%06d", number)
    }
}