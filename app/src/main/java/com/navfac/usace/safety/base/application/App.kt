package com.navfac.usace.safety.base.application

import alirezat775.lib.kesho.Kesho
import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.navfac.usace.safety.features.bottomtab.search.model.SearchModelItem
import com.preference.PowerPreference
import dagger.hilt.android.HiltAndroidApp
import java.io.File


@HiltAndroidApp
class App : MultiDexApplication() {
    lateinit var kesho: Kesho
    val _searchItems = ArrayList<SearchModelItem>()

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        downloadDir = File(filesDir, "pdfs").apply { mkdirs() }
        kesho = Kesho(this, Kesho.SHARED_PREFERENCES_MANAGER)

        PowerPreference.init(this)
        MultiDex.install(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )


    }
    companion object{
       lateinit var context  : Context
       lateinit var downloadDir: File
       var urlPDFLast = ""
    }
}