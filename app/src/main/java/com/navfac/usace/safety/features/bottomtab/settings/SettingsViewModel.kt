package com.navfac.usace.safety.features.bottomtab.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.features.bottomtab.settings.featuresintro.FeaturesIntroActivity
import com.navfac.usace.safety.features.bottomtab.settings.waiver.WaiverActivity

class SettingsViewModel : BaseViewModel() {

    /*
     * Other Settings Menu Operations
     * */
    @SuppressLint("QueryPermissionsNeeded")
    fun menuOtherItemOperation(item: Int, context: Context) {
        when (item) {
            1 -> {
                val browser = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.usace.army.mil/Missions/Locations/"))
                context.startActivity(browser)
            }
            2 -> {
                val subject = "Android App Feedback – NAVFAC Safety Manual EM385-1-1"
                val bodyText = "Android feedback"
                val emailaddress = "NAVFAC_SAFETY_APP@navy.mil"
                val email = Intent(Intent.ACTION_SEND)
                email.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailaddress))
                email.type = "message/rfc822"
                email.putExtra(Intent.EXTRA_SUBJECT, subject)
                email.putExtra(Intent.EXTRA_TEXT, bodyText)
                context.startActivity(Intent.createChooser(email, "Send Feedback via Email"))
            }
            3 -> {
                val browser = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.navfac.usace.safety"))
                context.startActivity(browser)
            }

            4 -> {
                val intent = Intent(context, FeaturesIntroActivity::class.java)
                context.startActivity(intent)
            }
            5 -> {
                val intent = Intent(context, WaiverActivity::class.java)
                intent.putExtra("item", "1")
                context.startActivity(intent)
            }

        }
    }
}