package com.navfac.usace.safety.features.sectionandappendices.sections

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.definition.HtmlType
import com.navfac.usace.safety.base.db.SectionsTitleEntity
import com.navfac.usace.safety.base.extension.checkFileExistInAssets
import com.navfac.usace.safety.base.extension.formatNumber
import com.navfac.usace.safety.base.extension.goToActivity
import com.navfac.usace.safety.base.extension.observe
import com.navfac.usace.safety.base.extension.showToast
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.databinding.FragmentSectionsBinding
import com.navfac.usace.safety.features.htmlpage.HtmlPageActivity
import com.navfac.usace.safety.features.sectionandappendices.base.SectionAppendicesFragment
import com.navfac.usace.safety.features.sectionandappendices.chapter.model.ChapterModelItem
import com.navfac.usace.safety.features.sectionandappendices.sections.adapter.SectionsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Sections2Fragment :
    BaseFragment<FragmentSectionsBinding>() {

    private lateinit var vm: SectionViewModel

    override val layoutRes: Int
        get() = R.layout.fragment_sections

    @Inject
    lateinit var adapterSections: SectionsAdapter

    private var chapter: ChapterModelItem? = null
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreated(savedInstance: Bundle?) {
        vm = ViewModelProvider(this)[SectionViewModel::class.java]
        initViews()
        initObserver()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "com.navfac.usace.safety.select_chap") {
                    val chapter = intent.getParcelableExtra<ChapterModelItem>("Chapter")
                    if (chapter != null) {
                        updateChapter(chapter)
                    }
                }
            }
        }
    }

    private fun initObserver() {
        adapterSections.toScroll.observe(viewLifecycleOwner, Observer {
            binding.sectionsRvSections.smoothScrollToPosition(it)
        })
        vm.apply {
            observe(getTitlesAndSubtitles) {
                it.apply { adapterSections.collection = it!!.toMutableList() }
            }
        }
    }

    private fun initViews() {
        binding.sectionsRvSections.adapter = adapterSections
        adapterSections.clickListener = { index, data, title ->
            if (data.id == null) {
                activity?.goToActivity(
                    HtmlPageActivity::class.java, false,
                    bundleOf(
                        Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.CHAPTERS),
                        Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, 0),
                        Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE_CRAWL, "purpose_of_manual.html")
                    )
                )
            } else {
                openChapter(title, index, data)
            }
        }

        adapterSections.clickHeaderListener = { index, title ->
            if (chapter != null) {
                openChapter(title, index, chapter!!)
            }

        }
    }

    fun openChapter(title: SectionsTitleEntity, index: Int, chapter: ChapterModelItem) {
        val titleRename = title.title.replace(" / ", " and ").replace("/", "-").replace("+", "and")
        val fileName = "${chapter.id!! + 1}-${index + 1}.$titleRename.html"
        if (fileName.checkFileExistInAssets(requireContext(), "content/sections_new")) {
            val page = index
            activity?.goToActivity(
                HtmlPageActivity::class.java, false,
                bundleOf(
                    Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.SECTIONS_NEW),
                    Pair(HtmlPageActivity.EXTRA_CHAPTER_ID, chapter.id + 1),
                    Pair(HtmlPageActivity.EXTRA_CHAPTER_KEY, "${chapter.id + 1}. ${chapter.key}"),
                    Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, page),
                    Pair(
                        HtmlPageActivity.EXTRA_GO_TO_PAGE_CRAWL,
                        fileName
                    )
                )
            )
        } else {
            showToast(requireContext(), "File not exist")
        }
    }

    fun updateChapter(model: ChapterModelItem) {
        chapter = model
        binding.titleLayout.visibility = View.VISIBLE
        binding.sectionsTvIndex.text = "CH ${(chapter?.id!! + 1).formatNumber()}"
        binding.sectionsTvTitle.text = chapter?.key
        adapterSections.updateExpanded()
    }

    override fun backPressedAction() {
        findNavController().navigateUp()
    }

    override fun setupActions() {

    }


    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            broadcastReceiver,
            IntentFilter("com.navfac.usace.safety.select_chap")
        )
        if (SectionAppendicesFragment.mChapter != null){
            updateChapter(SectionAppendicesFragment.mChapter!!)
            SectionAppendicesFragment.mChapter = null
        }
    }

    override fun onDestroyView() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver);
        super.onDestroyView()
    }
}