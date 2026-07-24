package com.navfac.usace.safety.features.bottomtab.settings.waiver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.application.App
import com.navfac.usace.safety.base.extension.readFileAssetIntoString
import com.navfac.usace.safety.base.platform.BaseActivity
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.databinding.ActivityWaiverBinding
import com.navfac.usace.safety.features.MainActivity
import com.preference.provider.PreferenceProvider
import java.util.*

class WaiverActivity : BaseActivity<ActivityWaiverBinding>() {

    private lateinit var app: App

    override fun getApplicationContext(): Context {
        app = super.getApplicationContext() as App
        return super.getApplicationContext()
    }

    override val layoutRes: Int
        get() = R.layout.activity_waiver


    override fun getViewModel(): BaseViewModel? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreated(instance: Bundle?) {
        val item = intent.getStringExtra("item")

        if (!app.kesho.has("init")) binding.llFooter.visibility = View.VISIBLE
        binding.bAccept.setOnClickListener {
            when {
                app.kesho.has("init") -> onBackPressed()
                else -> {
                    app.kesho.push("init", false)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
        binding.bDecline.setOnClickListener {
            onDecline()
        }

        binding.webview.settings.javaScriptEnabled = true
        //binding.webview.settings.serifFontFamily

        binding.include7.ntoolbarbacku.setOnClickListener {
            onDecline()
        }

        when (item) {
            "1" -> {
                binding.include7.ntoolbartitle.text = "Waiver"
                //binding.webview.loadUrl("file:///android_asset/waiver/waiver.html")
                val body = readFileAssetIntoString(this, "waiver/waiver.html").toString()
                val colors = getColors()
                binding.webview.loadDataWithBaseURL("file:///android_asset/waiver/",
                       "<html> <style> img{ display: inline;height: auto;max-width: 100%; } " +
                            "body, h1, h2, p, div { background-color: ${colors.first}; color:" +
                            " ${colors.second ?: "black"}; font-size: 16px;}</style> <body>$body</body> </html>",
                    "text/html",
                    "UTF-8",
                    null)
            }
        }
    }

    private fun getColors(): Pair<String, String?> {
        var cv = ContextCompat.getColor(this, R.color.myColorForeground)
        val textColor: String = String.format(Locale.US, "#%02X%02X%02X%02X",
                    Color.red(cv), Color.green(cv), Color.blue(cv), Color.alpha(cv))
        cv = ContextCompat.getColor(this, R.color.myColorBackground)
        val backColor: String = String.format(Locale.US, "#%02X%02X%02X%02X",
                    Color.red(cv), Color.green(cv), Color.blue(cv), Color.alpha(cv))
        return Pair(backColor, textColor)
    }

    private fun onDecline() {
        when {
            app.kesho.has("init") -> onBackPressed()
            else -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Waiver")
                builder.setCancelable(false)
                builder.setMessage("To use this application, you must agree to this waiver, do you wish to close the app?")
                builder.setPositiveButton("Yes") { _, _ ->
                    finish()
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.show()
            }
        }
    }

    override fun processIntentData(data: Uri) {}


}