package com.navfac.usace.safety.features

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.application.App
import com.navfac.usace.safety.base.extension.observe
import com.navfac.usace.safety.base.platform.BaseActivity
import com.navfac.usace.safety.base.utils.Constants
import com.navfac.usace.safety.databinding.ActivityMainBinding
import com.navfac.usace.safety.features.bottomtab.search.model.SearchModelItem
import com.preference.PowerPreference
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.subjects.BehaviorSubject
import java.io.File


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    val searchItemsStream = BehaviorSubject.create<List<SearchModelItem>>()

    private lateinit var viewModel: MainViewModel

    override fun getViewModel() = viewModel

    override val layoutRes: Int
        get() = R.layout.activity_main

    override fun onCreated(instance: Bundle?) {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        (supportFragmentManager.findFragmentById(R.id.home_navigation) as NavHostFragment?)?.let { host ->
            binding.bottomNavigationMain.setupWithNavController(host.navController)
        }
        initObserver()
    }

    private fun initObserver() {
        viewModel.apply {
            observe(subsection) {
                it.let {
                    val defFile = PowerPreference.getDefaultFile()
                    val isDataAlreadyInit = defFile.getBoolean(Constants.PREF_INIT_DATA, false)
                    if (!isDataAlreadyInit) {
                        getDataFromAssets(subsection)
                        defFile.putInt(Constants.PREF_FONT_SIZE, 14)
                        defFile.putString(Constants.PREF_TEXT_ALIGN, "left")
                        defFile.putBoolean(Constants.PREF_INIT_DATA, true)
                    }
                }
                viewModel.apply {
                    observe(searchItems) { list ->
                        list?.apply { searchItemsStream.onNext(list) }
                    }
                }
            }
        }
    }

    override fun processIntentData(data: Uri) {}

    override fun onDestroy() {
        super.onDestroy()
        val resourceFile = File(App.downloadDir.path, Constants.RESOURCE_FILE_DEFAULT)
        if (resourceFile.exists()) {
            resourceFile.delete()
        }
        val file = File(App.downloadDir.path, Constants.FILE_PDF_DEFAULT)
        if (file.exists()) {
            file.delete()
        }
    }
}