package com.navfac.usace.safety.features.safetyvideos

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.databinding.FragmentSafetyVideosBinding
import com.navfac.usace.safety.base.platform.BaseFragment

class SafetyVideosFragment : BaseFragment<FragmentSafetyVideosBinding>() {

    override val layoutRes: Int
        get() = R.layout.fragment_safety_videos

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreated(savedInstance: Bundle?) {}

    override fun backPressedAction() {
        findNavController().navigateUp()
    }

    override fun setupActions() {}

}