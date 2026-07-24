package com.navfac.usace.safety.features.bottomtab.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.extension.configureInterceptorWithEmpty
import com.navfac.usace.safety.base.extension.ioToMain
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.features.bottomtab.home.model.MenuModel
import com.navfac.usace.safety.features.bottomtab.search.model.SearchModelItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(@ApplicationContext context: Context) : BaseViewModel() {

    private val menulist: MutableList<MenuModel> = ArrayList()
    private val mutableLiveData = MutableLiveData<List<MenuModel>>()

    /*
    * Load Home Menu
    * */
    fun loadMenuItems(): MutableLiveData<List<MenuModel>> {
        menulist.clear()
        menulist.add(MenuModel("Chapters/\n" +
                "Sections", R.drawable.ic_page))
        menulist.add(MenuModel("Resources", R.drawable.ic_resources))
        mutableLiveData.value = menulist
        return mutableLiveData
    }

    fun goToYoutubePlayList(context: Context) {
        val browser = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/playlist?list=PLs5WgQtrDryb2qsdtDurNuu_PVNxzf7Uz"))
        context.startActivity(browser)
    }


    private val _searchItems = MutableLiveData<List<SearchModelItem>>()
    val searchItems: LiveData<List<SearchModelItem>> = _searchItems

    private val _queryResult = MutableLiveData<List<SearchModelItem>>()
    val queryResult: LiveData<List<SearchModelItem>> = _queryResult

    private val _query = MutableLiveData("")
    val query: LiveData<String> = _query

    val searchQueryStream = PublishSubject.create<String>()

    private val _showSearchProgress = MutableLiveData(false)
    val showSearchProgress: LiveData<Boolean> = _showSearchProgress

    init {
        searchQueryStream.configureInterceptorWithEmpty(800)
                .subscribe { result -> onSearchQuery(result) }.addTo(disposeBag)

        /*
        Observable.create<List<SearchModelItem>> {
            val searchItems = mutableListOf<SearchModelItem>()
            searchItems.addAll(SectionHelper.parseToSearchItems(context))
            searchItems.addAll(AppendicesHelper.parseToSearchItem(context))
            searchItems.addAll(gson.fromJson("search/search.json".toJson(context) ?: ""))
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
        */

        /* val searchItems = mutableListOf<SearchModelItem>()
         searchItems.addAll(gson.fromJson("search/search.json".toJson(context) ?: ""))
         searchItems.addAll(SectionHelper.parseToSearchItems(context))
         searchItems.addAll(AppendicesHelper.parseToSearchItem(context))
         _searchItems.value = searchItems*/
    }

    fun onSearch(query: CharSequence) {
        _queryResult.value = emptyList()
        _showSearchProgress.value = true
        searchQueryStream.onNext(query.toString())
    }

    private fun onSearchQuery(query: String) {
        _query.value = query
        _searchItems.value?.let { modelItems ->
            if (query.isNotBlank()) {
                Observable.create<List<SearchModelItem>> {
                    it.onNext(modelItems
                                    .filter { item ->
                                        gson.toJson(item).contains(query, true)
                                    }.filter { item ->
                                        !item.file.isNullOrBlank()
                                    }
                    )
                }.ioToMain()
                        .subscribe({
                            _queryResult.value = it
                            _showSearchProgress.value = false
                        }, {
                            it.printStackTrace()
                            _queryResult.value = emptyList()
                            _showSearchProgress.value = false
                        }).addTo(disposeBag)

                /*
                _queryResult.value =
                        modelItems
                                .filter { item ->
                                    //gson.toJson(item.data).contains(query, true) || gson.toJson(item.name).contains(query, true)
                                    gson.toJson(item).contains(query, true)
                                }.filter {
                                    !it.file.isNullOrBlank()
                                }
                */
            } else {
                _queryResult.value = emptyList()
                _showSearchProgress.value = false
            }
        }
    }

    fun setSearchItems(value: List<SearchModelItem>) {
        _searchItems.value = value
    }
}