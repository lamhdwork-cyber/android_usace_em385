package com.navfac.usace.safety.features.homesearch

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.navfac.usace.safety.base.extension.configureInterceptorWithEmpty
import com.navfac.usace.safety.base.extension.fromJson
import com.navfac.usace.safety.base.extension.ioToMain
import com.navfac.usace.safety.base.extension.toJson
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.features.bottomtab.search.model.SearchModelItem
import com.navfac.usace.safety.features.sectionandappendices.appendices.AppendicesHelper
import com.navfac.usace.safety.features.sectionandappendices.sections.SectionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


@HiltViewModel
class HomeSearchViewModel @Inject constructor(
    @ApplicationContext context: Context) : BaseViewModel() {

    private val _searchItems = MutableLiveData<List<SearchModelItem>>()
    val searchItems: LiveData<List<SearchModelItem>> = _searchItems

    private val _queryResult = MutableLiveData<List<SearchModelItem>>()
    val queryResult: LiveData<List<SearchModelItem>> = _queryResult

    val searchQueryStream = PublishSubject.create<String>()

    init {
        searchQueryStream.configureInterceptorWithEmpty(500)
                .subscribe { result -> onSearch(result) }.addTo(disposeBag)

        Observable.create<List<SearchModelItem>> {
            val searchItems = mutableListOf<SearchModelItem>()
            searchItems.addAll(gson.fromJson("search/search.json".toJson(context) ?: ""))

//            searchItems.addAll(SectionHelper.parseToSearchItems(context))
//            searchItems.addAll(AppendicesHelper.parseToSearchItem(context))
            it.onNext(searchItems)
        }.ioToMain()
                .doOnSubscribe { setLoading(true) }
                .subscribe(
                        {
                            _searchItems.value = it
                            setLoading(false)
                        },
                        {
                            it.printStackTrace()
                            setLoading(false)
                        }
                ).addTo(disposeBag)

        /*val searchItems = mutableListOf<SearchModelItem>()
        searchItems.addAll(gson.fromJson("search/search.json".toJson(context) ?: ""))
        searchItems.addAll(SectionHelper.parseToSearchItems(context))
        searchItems.addAll(AppendicesHelper.parseToSearchItem(context))
        _searchItems.value = searchItems */
    }

    private fun onSearch(query: String) {
        _searchItems.value?.let { modelItems ->
            if (query.isNotBlank()) {
                _queryResult.value = modelItems
                    .filter { item ->
                        gson.toJson(item).contains(query, true)
                    }.filter {
                        !it.file.isNullOrBlank()
                    }
            } else {
                _queryResult.value = emptyList()
            }
        }
    }
}