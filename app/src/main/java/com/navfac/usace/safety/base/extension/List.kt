package com.navfac.usace.safety.base.extension

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun <T> MutableList<T>.replace(newValue: T, block: (T) -> Boolean): MutableList<T> {
    return map {
        if (block(it)) newValue else it
    }.toMutableList()
}

inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)