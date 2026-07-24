package com.navfac.usace.safety.features.sectionandappendices.base

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.platform.BaseActivity
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.ViewPagerAdapter
import com.navfac.usace.safety.databinding.ActivitySectionAppendicesBinding
import com.navfac.usace.safety.features.sectionandappendices.appendices.AppendicesFragment
import com.navfac.usace.safety.features.sectionandappendices.sections.SectionsFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SectionAppendicesActivity : BaseActivity<ActivitySectionAppendicesBinding>() {
    private lateinit var vm: SectionAppendicesViewModel
    lateinit var vpa: ViewPagerAdapter

    override val layoutRes: Int
        get() = R.layout.activity_section_appendices

    override fun getViewModel(): BaseViewModel? =null

    override fun onCreated(instance: Bundle?) {
        vm = ViewModelProvider(this)[SectionAppendicesViewModel::class.java]
        binding.ntoolbartitle.text = "Sections/Appendices"
        binding.ntoolbarback.setOnClickListener {
            onBackPressed()
        }

        setupViewPager()
        initViews()
        initObserver()
    }

    private fun initObserver() {
        vm.apply{
        }
        vm.offscreenPageLimit = 2
        binding.lifecycleOwner = this
        binding.vm = vm
        binding.viewPagerAdapter = vpa
    }

    private fun initViews() {
        binding.apply {
            //sectionsAppendicesCtl.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(sectionsAppendicesNsvp))
            //sectionsAppendicesNsvp.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(sectionsAppendicesCtl))
            sectionsAppendicesNsvp.isUserInputEnabled = false
            sectionsAppendicesNsvp.adapter = vpa
            TabLayoutMediator(sectionsAppendicesCtl, sectionsAppendicesNsvp) { tab, position ->
                tab.text = if (position == 0) "Sections" else "Appendices"
            }.attach()
        }
    }

    private fun setupViewPager() {
        vpa = ViewPagerAdapter(this)    //supportFragmentManager)
        vpa.addFragment(SectionsFragment(), SectionsFragment::class.java.simpleName)
        binding.apply {
            sectionsAppendicesCtl.addTab(sectionsAppendicesCtl.newTab().setText("Sections"))
            vpa.addFragment(AppendicesFragment(), AppendicesFragment::class.java.simpleName)
            sectionsAppendicesCtl.addTab(sectionsAppendicesCtl.newTab().setText("Appendices"))
        }
    }


    override fun processIntentData(data: Uri) {}


}