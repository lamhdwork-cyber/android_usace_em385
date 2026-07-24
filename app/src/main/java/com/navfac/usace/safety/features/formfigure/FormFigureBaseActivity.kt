package com.navfac.usace.safety.features.formfigure

import android.net.Uri
import android.os.Bundle
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.platform.BaseActivity
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.databinding.ActivityFormFigureBaseBinding
import com.navfac.usace.safety.features.formfigure.figure.FigureFragment
import com.navfac.usace.safety.features.formfigure.form.FormFragment


class FormFigureBaseActivity : BaseActivity<ActivityFormFigureBaseBinding>() {

    override val layoutRes: Int
        get() = R.layout.activity_form_figure_base

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreated(savedInstance: Bundle?) {

        binding.ntoolbartitle.text = "Figures/Forms"
        binding.ntoolbarback.setOnClickListener {
            onBackPressed()
        }

        /*
        * Set Tab viewpager adapter
        * */
        baseViewPagerAdapter.addFragment(FigureFragment(), "Figures")
        baseViewPagerAdapter.addFragment(FormFragment(), "Forms")
        /*binding.formfigureViewpager.adapter = baseViewPagerAdapter
        binding.formfigureTablayout.setupWithViewPager(binding.formfigureViewpager)*/

    }


    override fun processIntentData(data: Uri) {

    }

}