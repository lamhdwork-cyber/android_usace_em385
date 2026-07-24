package com.navfac.usace.safety.features.sectionandappendices.base

import androidx.lifecycle.MutableLiveData
import com.navfac.usace.safety.base.db.UsaceDao
import com.navfac.usace.safety.base.platform.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



@HiltViewModel
class SectionAppendicesViewModel @Inject constructor(private val dao: UsaceDao) : BaseViewModel() {
    var pageSelected = MutableLiveData(0)

    var currentPage = MutableLiveData<Int>()
    var offscreenPageLimit: Int = 2

    fun onPageSelected(position: Int) {
        pageSelected.value = position
    }
}
