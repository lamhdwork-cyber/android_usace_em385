package com.navfac.usace.safety.base.platform

import android.content.Context
import android.content.SharedPreferences
import android.util.Log


class BaseSharedPreference(context: Context) {

    var preferences: SharedPreferences =
            context.getSharedPreferences("com.navfac.usace.safety", Context.MODE_PRIVATE)

    fun putData(key: String, data: Any) {

        preferences.let { pref ->
            try {
                when (data) {
                    is String -> pref.edit().putString(key, data).apply()
                    is Int -> pref.edit().putInt(key, data).apply()
                    is Boolean -> pref.edit().putBoolean(key, data).apply()
                    is Float -> pref.edit().putFloat(key, data).apply()
                    else -> null
                }
            } catch (e: Exception) {
                Log.d("TAG", e.message!!)
            }
        }

    }

    fun getStringData(key: String): String? {
        return preferences.getString(key, "")
    }

    fun getIntData(key: String): Int? {
        return preferences.getInt(key, 0)
    }

    fun getBooleanData(key: String): Boolean? {
        return preferences.getBoolean(key, false)
    }

    fun getFloatData(key: String): Float? {
        return preferences.getFloat(key, 0f)
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    fun clearSingleValue(key: String) {
        preferences.edit().remove(key).apply()
    }
}