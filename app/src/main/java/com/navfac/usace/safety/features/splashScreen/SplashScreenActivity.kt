package com.navfac.usace.safety.features.splashScreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.navfac.usace.safety.BuildConfig
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.application.App
import com.navfac.usace.safety.databinding.ActivitySplashScreenBinding
import com.navfac.usace.safety.features.MainActivity
import com.navfac.usace.safety.features.bottomtab.settings.featuresintro.FeaturesIntroActivity
import com.navfac.usace.safety.features.bottomtab.settings.waiver.WaiverActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private var binding: ActivitySplashScreenBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)

        CoroutineScope(Dispatchers.Main).launch {
            delay(2500)
            if ((application as App).kesho.has("feature")) {
                if ((application as App).kesho.has("init")) {
                    val it = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(it)
                } else {
                    val it = Intent(this@SplashScreenActivity, WaiverActivity::class.java)
                    it.putExtra("item", "1")
                    startActivity(it)
                }
            } else {
                val it = Intent(this@SplashScreenActivity, FeaturesIntroActivity::class.java)
                startActivity(it)
            }
            finish()
        }

        /*
         * Show Version number as text
         * */
        val appVersion: String = BuildConfig.VERSION_NAME
        appVersion.let {
            binding!!.tvVersionnumber.text = "v$appVersion"
        }
    }
}