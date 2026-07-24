package com.navfac.usace.safety.features.bottomtab.home

import android.os.Bundle
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.definition.HtmlType
import com.navfac.usace.safety.base.extension.goToActivity
import com.navfac.usace.safety.base.extension.observe
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.databinding.FragmentHomeBinding
import com.navfac.usace.safety.features.MainActivity
import com.navfac.usace.safety.features.bottomtab.home.adapter.HomeMenuAdapter
import com.navfac.usace.safety.features.homesearch.SearchResultAdapter
import com.navfac.usace.safety.features.htmlpage.HtmlPageActivity
import com.navfac.usace.safety.features.safetyvideos.imagedetails.ImageDetailsActivity
import com.navfac.usace.safety.features.sectionandappendices.appendices.CATEGORY_APPENDICES
import com.navfac.usace.safety.features.sectionandappendices.details.newdetails.SectionAppendicesDetailsActivity
import com.navfac.usace.safety.features.sectionandappendices.sections.CATEGORY_SECTION
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    @Inject
    lateinit var searchAdapter: SearchResultAdapter

    private lateinit var viewModel: HomeViewModel

    private var showHomeSearchTriggered = false

    override val layoutRes: Int
        get() = R.layout.fragment_home

    override fun getViewModel() = viewModel

    override fun onCreated(savedInstance: Bundle?) {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        initObserver()
        viewModel.loadMenuItems().observe(this) {
            binding.recyclerviewMenu.setHasFixedSize(true)
            binding.recyclerviewMenu.layoutManager = GridLayoutManager(activity, 3)
            val adapter = HomeMenuAdapter(it)
            binding.recyclerviewMenu.adapter = adapter
            adapter.notifyDataSetChanged()

            adapter.actionListener = { position, _ -> //menu ->
                when (position) {
                    0 -> findNavController().navigate(R.id.sectionAppendicesFragment)
//                    1 -> findNavController().navigate(R.id.acronymsFragment)
                    1 -> findNavController().navigate(R.id.resourceFragment)
                    2 -> findNavController().navigate(R.id.formFigureBaseFragment)
                    3 -> findNavController().navigate(R.id.tableFragment)
                    4 -> findNavController().navigate(R.id.definitionFragment)
                    5 -> viewModel.goToYoutubePlayList(requireContext())
                }
            }
        }

        binding.searchEdittextTable.doAfterTextChanged {
            if (!showHomeSearchTriggered) {
                viewModel.searchQueryStream.onNext(it.toString())
            }
        }

        binding.rvSearchResult.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvSearchResult.adapter = searchAdapter
        //binding.rvSearchResult.addItemDecoration(DividerItemDecoration(requireContext(),
        //                                                DividerItemDecoration.VERTICAL))

        searchAdapter.clickListener = {
            val keyword = binding.searchEdittextTable.text.toString()
            when (it.category) {
                CATEGORY_SECTION -> {
                    val htmlPositions = it.file?.split("-") ?: listOf("0", "0", "-1")

                    activity?.goToActivity(
                        SectionAppendicesDetailsActivity::class.java, false,
                        bundleOf(
                            SectionAppendicesDetailsActivity.EXTRA_TOOLBAR_TITLE to "", //"Sections",
                            SectionAppendicesDetailsActivity.EXTRA_TITLE to "", //it.data,
                            SectionAppendicesDetailsActivity.EXTRA_CHILD_POSITION to htmlPositions[1],
                            SectionAppendicesDetailsActivity.EXTRA_GROUP_POSITION to htmlPositions[0],
                            SectionAppendicesDetailsActivity.EXTRA_CONTENT_PAGE to htmlPositions[2].toInt(),
                            SectionAppendicesDetailsActivity.EXTRA_TYPE to HtmlType.SECTIONS,
                            "searchdata" to keyword
                        )
                    )
                }

                CATEGORY_APPENDICES -> {
                    activity?.goToActivity(
                        HtmlPageActivity::class.java, false,
                        bundleOf(
                            Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.APPENDICES),
                            Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, it.file?.toInt()),
                            "searchdata" to keyword
                        )
                    )
                }

                else -> {
                    if (it.file != null) {
                        activity?.goToActivity(
                            ImageDetailsActivity::class.java, false,
                            bundleOf(
                                ImageDetailsActivity.EXTRA_IMAGE to it.file,
                                ImageDetailsActivity.EXTRA_TERMS to it.data
                            )
                        )
                    }
                }
            }
        }

        //binding.searchEdittextTable.doAfterTextChanged {
        //    viewModel.searchQueryStream.onNext(it.toString())
        //}
        initBackPress()
    }

    private fun initBackPress() {
        activity?.onBackPressedDispatcher?.addCallback {

        }
    }

    private fun initObserver() {
        viewModel.apply {
            observe(queryResult) {
                it?.let {
                    searchAdapter.collection = it
                    when {
                        it.isNotEmpty() -> {
                            binding.rvSearchResult.scrollToPosition(0)
                        }

                        else -> {
                            searchAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        (activity as? MainActivity)?.searchItemsStream
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                viewModel.setSearchItems(it)
            }, {
                it.printStackTrace()
            })?.addTo(disposeBag)
    }

    override fun onResume() {
        super.onResume()
        showHomeSearchTriggered = false
    }

    override fun backPressedAction() {
        findNavController().navigateUp()
    }

    override fun setupActions() {}

}