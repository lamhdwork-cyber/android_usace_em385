package com.navfac.usace.safety.features.sectionandappendices.appendices

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.navfac.usace.safety.base.db.AppendicesEntity
import com.navfac.usace.safety.base.db.UsaceDao
import com.navfac.usace.safety.base.platform.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppendicesViewModel @Inject constructor(
        private val dao: UsaceDao
) : BaseViewModel() {

    val getAllAppendices: LiveData<List<AppendicesEntity>> = dao.getAllAppendices().asLiveData()

}
