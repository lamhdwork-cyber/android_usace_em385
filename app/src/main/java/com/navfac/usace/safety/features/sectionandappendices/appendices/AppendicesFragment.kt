package com.navfac.usace.safety.features.sectionandappendices.appendices

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.definition.HtmlType
import com.navfac.usace.safety.base.extension.goToActivity
import com.navfac.usace.safety.base.extension.observe
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.databinding.FragmentAppendicesBinding
import com.navfac.usace.safety.features.htmlpage.HtmlPageActivity
import com.navfac.usace.safety.features.sectionandappendices.appendices.adapter.AppendicesAdapter
import com.navfac.usace.safety.features.sectionandappendices.appendices.adapter.AppendicesMainAdapter
import com.navfac.usace.safety.features.sectionandappendices.appendices.details.AppendicesDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppendicesFragment : BaseFragment<FragmentAppendicesBinding>() {
    private lateinit var vm: AppendicesViewModel

    @Inject
    lateinit var adapterAppendices: AppendicesMainAdapter

    override val layoutRes: Int
        get() = R.layout.fragment_appendices

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreated(savedInstance: Bundle?) {
        vm = ViewModelProvider(this)[AppendicesViewModel::class.java]
        val appendicesList = AppendicesHelper.data
        val appendicesAdapter = AppendicesAdapter(requireActivity(), appendicesList)
        binding.appendiceListView.adapter = appendicesAdapter
        appendicesAdapter.notifyDataSetChanged()

        binding.appendiceListView.setOnItemClickListener { adapterView, _, position, _ -> //view, position, id ->
            val intent = Intent(requireContext(), AppendicesDetailsActivity::class.java)
            intent.putExtra(AppendicesDetailsActivity.EXTRA_CHILD_POSITION, position.toString())
            intent.putExtra(AppendicesDetailsActivity.EXTRA_TITLE,
                    adapterView.getItemAtPosition(position).toString())
        }

        initViews()
        initObserver()
    }

    private fun initObserver() {
        vm.apply {
            observe(getAllAppendices) {
                it.apply { adapterAppendices.collection = this!! }
            }
        }
    }

    private fun initViews() {
        binding.appendicesRvAppendcies.adapter = adapterAppendices

        adapterAppendices.clickListener = {
            val page = it.first.firstPage.dropLast(5).takeLast(2).toInt()
            activity?.goToActivity(HtmlPageActivity::class.java, false,
                        bundleOf(Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.APPENDICES),
                                 Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, page))
            )
        }
    }

    override fun backPressedAction() {
        findNavController().navigateUp()
    }

    override fun setupActions() {}

}