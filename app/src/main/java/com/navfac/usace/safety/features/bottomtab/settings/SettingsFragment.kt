package com.navfac.usace.safety.features.bottomtab.settings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.navfac.usace.safety.BuildConfig
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.databinding.FragmentSettingsBinding


class SettingsFragment : BaseFragment<FragmentSettingsBinding>(), View.OnClickListener {

    private lateinit var viewModel: SettingsViewModel

    override val layoutRes: Int
        get() = R.layout.fragment_settings

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreated(savedInstance: Bundle?) {
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        updateUIState()

        binding.itemContactSettings.setOnClickListener(this)
        binding.itemFeedbackSetting.setOnClickListener(this)
        binding.itemRateSettings.setOnClickListener(this)
        binding.itemShowFeatureintroduction.setOnClickListener(this)
        binding.itemShowWaver.setOnClickListener(this)
    }

    override fun backPressedAction() {
        findNavController().navigateUp()
    }

    override fun setupActions() {

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            /*R.id.ntoolbarbacku -> {
                backPressedAction()
            }*/
            R.id.item_contact_settings -> {
                viewModel.menuOtherItemOperation(1, requireContext())
            }
            R.id.item_feedback_setting -> {
                viewModel.menuOtherItemOperation(2, requireContext())
            }
            R.id.item_rate_settings -> {
                viewModel.menuOtherItemOperation(3, requireContext())
            }
            R.id.item_show_featureintroduction -> {
                viewModel.menuOtherItemOperation(4, requireContext())
            }
            R.id.item_show_waver -> {
                viewModel.menuOtherItemOperation(5, requireContext())
            }
        }
    }

    private fun updateUIState() {
        // Show app version number
        val appVersion: String = BuildConfig.VERSION_NAME
        binding.itemversionnumber.text = "App version: $appVersion"
    }
}