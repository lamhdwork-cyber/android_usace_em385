package com.navfac.usace.safety.features.sectionandappendices.chapter

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.definition.HtmlType
import com.navfac.usace.safety.base.extension.fromJson
import com.navfac.usace.safety.base.extension.goToActivity
import com.navfac.usace.safety.base.extension.toJson
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.databinding.FragmentChapterBinding
import com.navfac.usace.safety.features.htmlpage.HtmlPageActivity
import com.navfac.usace.safety.features.sectionandappendices.chapter.adapter.ChapterAdapter
import com.navfac.usace.safety.features.sectionandappendices.chapter.model.ChapterModelItem
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChapterFragment :
    BaseFragment<FragmentChapterBinding>() {

    private lateinit var adapter: ChapterAdapter
    private var model = mutableListOf<ChapterModelItem>()
    override val layoutRes: Int
        get() = R.layout.fragment_chapter

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreated(savedInstance: Bundle?) {
        initViews()
    }

    private fun initViews() {
        val gson = Gson()
        model = gson.fromJson("content/chapter/chapter.json".toJson(requireContext()) ?: "")
        binding.sectionsRvChapter.setHasFixedSize(true)
        binding.sectionsRvChapter.layoutManager = LinearLayoutManager(requireContext())
        model.add(0, ChapterModelItem(key = "\tPreamble"))
        adapter = ChapterAdapter(model)
        binding.sectionsRvChapter.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.clickListener = { index, model ->
            if (index == 0) {
                activity?.goToActivity(
                    HtmlPageActivity::class.java, false,
                    bundleOf(
                        Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.CHAPTERS),
                        Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, 0),
                        Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE_CRAWL, "purpose_of_manual.html")
                    )
                )
            } else {
//                baseFM.selectChapter(model)
                val intent = Intent()
                intent.action = "com.navfac.usace.safety.select_chap"
                intent.putExtra("Chapter", model)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }

//            val page = it.firstPage.toInt()
//            activity?.goToActivity(HtmlPageActivity::class.java,false,
//                                bundleOf(Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.SECTIONS),
//                                        Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, page),
//                                        Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE_CRAWL, it.fileName))
//            )
        }

    }

    override fun backPressedAction() {
        findNavController().navigateUp()
    }

    override fun setupActions() {

    }
}