package com.navfac.usace.safety.features.sectionandappendices.sections

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.db.SectionsSubtitleEntity
import com.navfac.usace.safety.base.db.SectionsTitleEntity
import com.navfac.usace.safety.base.db.TitlesAndSubtitles
import com.navfac.usace.safety.base.extension.observe
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.databinding.FragmentSectionsBinding
import com.navfac.usace.safety.features.sectionandappendices.sections.adapter.ExpandableAdapter
import com.navfac.usace.safety.features.sectionandappendices.sections.adapter.SectionsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SectionsFragment : BaseFragment<FragmentSectionsBinding>() {

    private lateinit var vm: SectionViewModel

    override val layoutRes: Int
        get() = R.layout.fragment_sections

    @Inject
    lateinit var adapterSections: SectionsAdapter

    internal var adapter: ExpandableAdapter? = null
//    internal var titleList: List<String>? = null

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreated(savedInstance: Bundle?) {
        vm = ViewModelProvider(this)[SectionViewModel::class.java]
//        val listData = SectionHelper.data
//        titleList = ArrayList(listData.keys)
        /*adapter = ExpandableAdapter(requireContext(), titleList as ArrayList<String>, listData)
        binding.expandableListView.setAdapter(adapter)

        binding.expandableListView.setOnGroupExpandListener { //groupPosition ->
            //Toast.makeText(requireContext(), (titleList as ArrayList<String>)[groupPosition] + " List Expanded.", Toast.LENGTH_SHORT).show()
        }

        binding.expandableListView.setOnGroupCollapseListener { //groupPosition ->
            //Toast.makeText(requireContext(), (titleList as ArrayList<String>)[groupPosition] + " List Collapsed.", Toast.LENGTH_SHORT).show()
            //Toast.makeText(requireContext(), "Click on Group Positions." + groupPosition, Toast.LENGTH_SHORT).show()
        }

        binding.expandableListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ -> //parent, v, groupPosition, childPosition, id ->
            //Toast.makeText(requireContext(), "Clicked: " + (titleList as ArrayList<String>)[groupPosition] + " -> " + listData[(titleList as ArrayList<String>)[groupPosition]]!!.get(childPosition), Toast.LENGTH_SHORT).show()
            // For get child position
            //val intent = Intent(requireContext(),HtmlDetailsViewActivity::class.java)
            val intent = Intent(requireContext(), SectionAppendicesDetailsActivity::class.java)
            intent.putExtra(SectionAppendicesDetailsActivity.EXTRA_TOOLBAR_TITLE, "Sections")
            intent.putExtra(SectionAppendicesDetailsActivity.EXTRA_TITLE,
                listData[(titleList as ArrayList<String>)[groupPosition]]!![childPosition])
            intent.putExtra(SectionAppendicesDetailsActivity.EXTRA_CHILD_POSITION, childPosition.toString())
            intent.putExtra(SectionAppendicesDetailsActivity.EXTRA_GROUP_POSITION, groupPosition.toString())
            intent.putExtra(SectionAppendicesDetailsActivity.EXTRA_TYPE, "Sections")
            startActivity(intent)

            false
        }*/

        initViews()
        initObserver()
    }

    private fun initObserver() {
        adapterSections.toScroll.observe(viewLifecycleOwner, Observer {
            binding.sectionsRvSections.smoothScrollToPosition(it)
        })
        vm.apply {
            observe(getTitlesAndSubtitles) {
                val sections = mutableListOf<TitlesAndSubtitles>()
                sections.add(
                    TitlesAndSubtitles(
                        SectionsTitleEntity("", "purpose_of_manual.html", "Introduction"),
                        listOf(
                            SectionsSubtitleEntity(
                                "A", "purpose_of_manual.html",
                                "Introduction", "1", "0"
                            )
                        ), false
                    )
                )
                it?.forEach { info ->
                    sections.add(
                        TitlesAndSubtitles(
                            info.title, info.subtitles,
                            info.expanded == false
                        )
                    )
                }
                it.apply { adapterSections.collection = sections }
            }
        }
    }

    private fun initViews() {
        binding.sectionsRvSections.adapter = adapterSections

//        adapterSections.clickListener = {
//            val page = it.firstPage.toInt()
//            activity?.goToActivity(
//                HtmlPageActivity::class.java, false,
//                bundleOf(
//                    Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.SECTIONS),
//                    Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, page),
//                    Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE_CRAWL, it.fileName)
//                )
//            )
//        }
    }

    override fun backPressedAction() {
        findNavController().navigateUp()
    }

    override fun setupActions() {

    }
}