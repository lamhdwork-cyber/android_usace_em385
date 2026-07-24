package com.navfac.usace.safety.features.formfigure

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.ViewPagerAdapter
import com.navfac.usace.safety.databinding.FragmentFormFigureBaseBinding
import com.navfac.usace.safety.features.formfigure.figure.FigureFragment
import com.navfac.usace.safety.features.formfigure.form.FormFragment
import com.navfac.usace.safety.base.platform.BaseFragment

class FormFigureBaseFragment : BaseFragment<FragmentFormFigureBaseBinding>() {
    override val layoutRes: Int get() = R.layout.fragment_form_figure_base
    lateinit var vpa: ViewPagerAdapter

    override fun onCreated(savedInstance: Bundle?) {
        binding.ntoolbartitle.text = "Figures/Forms"
        binding.ntoolbarback.setOnClickListener {
            findNavController().navigateUp()
        }

        /*
        * Set Tab viewpager adapter
        * */
        vpa = ViewPagerAdapter(requireActivity())
        vpa.addFragment(FigureFragment(), "Figures")
        vpa.addFragment(FormFragment(), "Forms")
        binding.pager.adapter = vpa
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = if (position == 0) "Figures" else "Forms"
        }.attach()
    }

    override fun getViewModel(): BaseViewModel? = null

    override fun backPressedAction() {
        findNavController().navigateUp()
    }

    override fun setupActions() {}

}