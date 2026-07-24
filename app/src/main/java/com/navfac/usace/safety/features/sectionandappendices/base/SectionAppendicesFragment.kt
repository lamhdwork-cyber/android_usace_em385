package com.navfac.usace.safety.features.sectionandappendices.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.ViewPagerAdapter
import com.navfac.usace.safety.databinding.FragmentSectionAppendicesBinding
import com.navfac.usace.safety.features.sectionandappendices.chapter.ChapterFragment
import com.navfac.usace.safety.features.sectionandappendices.chapter.model.ChapterModelItem
import com.navfac.usace.safety.features.sectionandappendices.sections.Sections2Fragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SectionAppendicesFragment : BaseFragment<FragmentSectionAppendicesBinding>() {
    companion object{
        var mChapter : ChapterModelItem?= null
    }
    private lateinit var vm: SectionAppendicesViewModel
    lateinit var vpa: ViewPagerAdapter
    override val layoutRes: Int
        get() = R.layout.fragment_section_appendices
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreated(savedInstance: Bundle?) {
        vm = ViewModelProvider(this)[SectionAppendicesViewModel::class.java]
        binding.ntoolbartitle.text = "Chapters/Sections"
        binding.ntoolbarback.setOnClickListener {
            findNavController().navigateUp()
        }

        setupViewPager()
        initViews()
        initObserver()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "com.navfac.usace.safety.select_chap") {
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.sectionsAppendicesNsvp.setCurrentItem(1, true)
                    }
                    val chapter = intent.getParcelableExtra<ChapterModelItem>("Chapter")
                    mChapter = chapter
                }
            }
        }
    }

    private fun initObserver() {
        vm.apply {
        }
        vm.offscreenPageLimit = 2
        binding.lifecycleOwner = viewLifecycleOwner
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
                tab.text = if (position == 0) "Chapters" else "Sections"
            }.attach()
        }
    }

    private fun setupViewPager() {
        vpa = ViewPagerAdapter(requireActivity()) //childFragmentManager)
        vpa.addFragment(ChapterFragment(), ChapterFragment::class.java.simpleName)
        binding.apply {
            sectionsAppendicesCtl.addTab(sectionsAppendicesCtl.newTab().setText("Chapters"))
            vpa.addFragment(Sections2Fragment(), Sections2Fragment::class.java.simpleName)
            sectionsAppendicesCtl.addTab(sectionsAppendicesCtl.newTab().setText("Sections"))
        }
    }

    override fun getViewModel(): BaseViewModel? = null

    override fun backPressedAction() {
        findNavController().navigateUp()
    }

    override fun setupActions() {}

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            broadcastReceiver,
            IntentFilter("com.navfac.usace.safety.select_chap")
        )
    }

    override fun onDestroyView() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver)
        super.onDestroyView()
    }
}