package com.navfac.usace.safety.base.utils

import java.util.*

object RandomNumberGenerator {
    fun generate(): Int {
        val rnd = Random()
        val number = rnd.nextInt(999999)
        return number
    }
}