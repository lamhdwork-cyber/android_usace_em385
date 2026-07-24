package com.navfac.usace.safety.features

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.navfac.usace.safety.base.application.App
import com.navfac.usace.safety.base.db.SectionsSubtitleEntity
import com.navfac.usace.safety.base.extension.checkSectionsFirstPage
import com.navfac.usace.safety.base.extension.extractSection
import com.navfac.usace.safety.base.extension.extractSectionsFirstPage
import com.navfac.usace.safety.base.extension.extractSubsectionOrdinal
import com.navfac.usace.safety.base.extension.ioToMain
import com.navfac.usace.safety.base.extension.isSubsection
import com.navfac.usace.safety.base.extension.readFileAssetIntoString
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.source.local.SectionsRepository
import com.navfac.usace.safety.features.bottomtab.search.model.SearchModelItem
import com.navfac.usace.safety.features.sectionandappendices.sections.SectionHelper
import com.preference.provider.PreferenceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
        private val repo: SectionsRepository,
        @ApplicationContext private val context: Context
) : BaseViewModel() {
    private val _subtitle = MutableLiveData<List<SectionsSubtitleEntity>>()
    val subsection: LiveData<List<SectionsSubtitleEntity>> = _subtitle

    val subtitles = mutableListOf<SectionsSubtitleEntity>()

    private val _searchItems = MutableLiveData<List<SearchModelItem>>()
    val searchItems: LiveData<List<SearchModelItem>> = _searchItems

    init {
        initializeData()

        Observable.create<List<SearchModelItem>> {
            val searchItems = mutableListOf<SearchModelItem>()
            searchItems.addAll(SectionHelper.parseToSearchItems(context))
            it.onNext(searchItems)
        }.ioToMain()
                .doOnSubscribe { setLoading(true) }
                .subscribe(
                        {
                            ((context as App))._searchItems.addAll(it)
                            _searchItems.value = it
                            setLoading(false)
                        },
                        {
                            it.printStackTrace()
                            setLoading(false)
                        }
                ).addTo(disposeBag)
    }

    private fun initializeData() {
        val filenameList = PreferenceProvider.context?.assets!!.list("content/section_new")
        val firstPageSections = mutableListOf<String>()
        val filterSubtitlesSections = mutableListOf<String>()
        val lengthThenNatural = compareBy<String> { it.length }.then(naturalOrder())

        filenameList!!.sortedWith(lengthThenNatural).filter { it.endsWith(".html") }
                .forEachIndexed { _, data -> //index
                    if (data.checkSectionsFirstPage()) {
                        firstPageSections.add(data.extractSectionsFirstPage())
                    }
                }

        filenameList.filter { it.endsWith(".title") }.sortedWith(lengthThenNatural).forEachIndexed { index, data ->
            if (data.isSubsection()) {
                filterSubtitlesSections.add(data)
            }
        }

        filterSubtitlesSections.forEachIndexed { index, data ->
            val title = readFileAssetIntoString(PreferenceProvider.context!!, "content/sections/$data").toString()
            subtitles.add(SectionsSubtitleEntity(data.extractSubsectionOrdinal().toString(), data,
                                title, data.extractSection().toString(), firstPageSections[index]))
        }
        _subtitle.value = subtitles
    }


    fun getDataFromAssets(subsection: LiveData<List<SectionsSubtitleEntity>>) =
            viewModelScope.launch(Dispatchers.IO) {
                repo.setSubtitleFileFromAssets(subtitles)
                repo.setTitleFileFromAssets(context.assets.list("content/section_new"))
            }
}