package com.navfac.usace.safety.features.homesearch

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.definition.HtmlType
import com.navfac.usace.safety.base.extension.goToActivity
import com.navfac.usace.safety.base.extension.observe
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.databinding.FragmentHomeSearchBinding
import com.navfac.usace.safety.features.safetyvideos.imagedetails.ImageDetailsActivity
import com.navfac.usace.safety.features.sectionandappendices.appendices.CATEGORY_APPENDICES
import com.navfac.usace.safety.features.sectionandappendices.appendices.details.AppendicesDetailsActivity
import com.navfac.usace.safety.features.sectionandappendices.details.newdetails.SectionAppendicesDetailsActivity
import com.navfac.usace.safety.features.sectionandappendices.sections.CATEGORY_SECTION
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeSearchFragment : BaseFragment<FragmentHomeSearchBinding>() {
    @Inject
    lateinit var adapter: SearchResultAdapter

    private lateinit var viewModel: HomeSearchViewModel

    override val layoutRes: Int
        get() = R.layout.fragment_home_search

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreated(savedInstance: Bundle?) {
        viewModel = ViewModelProvider(this)[HomeSearchViewModel::class.java]
        initViews()
        initObserver()
        checkExtras()
    }

    private fun checkExtras() {
        activity?.intent?.extras?.let {
            if (it.containsKey(HomeSearchActivity.EXTRA_INITIAL_QUERY)) {
                val query = it.getString(HomeSearchActivity.EXTRA_INITIAL_QUERY) ?: ""
                binding.homeSearchEtQuery.setText(query)
                viewModel.searchQueryStream.onNext(query)
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            observe(queryResult) {
                it?.let {
                    adapter.collection = it
                    if (it.isNotEmpty()) {
                        binding.homeSearchRvResults.scrollToPosition(0)
                    }
                }
            }
            observe(searchItems){
                it?.apply {
                    checkExtras()
                }
            }
        }

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
    }

    private fun initViews() {
        binding.homeSearchRvResults.adapter = adapter
        binding.homeSearchRvResults.addItemDecoration(DividerItemDecoration(requireContext(),
                                                            DividerItemDecoration.VERTICAL))
        adapter.clickListener = {
            when (it.category) {
                CATEGORY_SECTION -> {
                    val htmlPositions = it.file?.split("-") ?: listOf("0", "0")
                    activity?.goToActivity(
                        SectionAppendicesDetailsActivity::class.java, false,
                        bundleOf(
                            SectionAppendicesDetailsActivity.EXTRA_TOOLBAR_TITLE to "", //"Sections",
                            SectionAppendicesDetailsActivity.EXTRA_TITLE to "", //it.data,
                            SectionAppendicesDetailsActivity.EXTRA_CHILD_POSITION to htmlPositions[1],
                            SectionAppendicesDetailsActivity.EXTRA_GROUP_POSITION to htmlPositions[0],
                            SectionAppendicesDetailsActivity.EXTRA_TYPE to HtmlType.SECTIONS
                        )
                    )
                }
                CATEGORY_APPENDICES -> {
                    activity?.goToActivity(AppendicesDetailsActivity::class.java, false,
                        bundleOf(AppendicesDetailsActivity.EXTRA_TITLE to it.name,
                                AppendicesDetailsActivity.EXTRA_CHILD_POSITION to it.file)
                    )
                }
                else -> {
                    if(it.file != null) {
                        activity?.goToActivity(
                            ImageDetailsActivity::class.java, false,
                                bundleOf(
                                    ImageDetailsActivity.EXTRA_IMAGE to it.file,
                                        ImageDetailsActivity.EXTRA_TERMS to it.data)
                        )
                    }
                }
            }
        }
    }

    override fun backPressedAction() {}

    override fun setupActions() {}
}