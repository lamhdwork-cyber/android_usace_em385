package com.navfac.usace.safety.features.homesearch

import android.net.Uri
import android.os.Bundle
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.platform.BaseActivity
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.databinding.ActivityHomeSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeSearchActivity : BaseActivity<ActivityHomeSearchBinding>() {
    override val layoutRes: Int
        get() = R.layout.activity_home_search

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreated(instance: Bundle?) {
        setSupportActionBar(binding.toolbar)
        setToolbar(show = true, showBackButton = true)
    }

    override fun processIntentData(data: Uri) {}

    companion object {
        const val EXTRA_INITIAL_QUERY = "e_initial_query"
    }
}