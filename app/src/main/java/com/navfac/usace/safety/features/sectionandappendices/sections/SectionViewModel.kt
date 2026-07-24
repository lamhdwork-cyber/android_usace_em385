package com.navfac.usace.safety.features.sectionandappendices.sections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.navfac.usace.safety.base.application.App
import com.navfac.usace.safety.base.db.SectionsTitleEntity
import com.navfac.usace.safety.base.db.TitlesAndSubtitles
import com.navfac.usace.safety.base.db.UsaceDao
import com.navfac.usace.safety.base.extension.fromJson
import com.navfac.usace.safety.base.extension.toJson
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.features.sectionandappendices.chapter.model.ChapterModelItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SectionViewModel @Inject constructor(
    private val dao: UsaceDao
) : BaseViewModel() {

    /* val getAllSectionTitle: LiveData<List<SectionsTitleEntity>> = dao.getAllSectionTitle().asLiveData()
     val getAllSectionSubtitle: LiveData<List<SectionsSubtitleEntity>> = dao.getAllSectionSubtitle().asLiveData()*/
//  val getTitlesAndSubtitles: LiveData<List<TitlesAndSubtitles>> = dao.getTitlesAndSubtitles().asLiveData()
    val getTitlesAndSubtitles: LiveData<List<TitlesAndSubtitles>> = getTitlesAndSubtitles()

    fun getTitlesAndSubtitles(): LiveData<List<TitlesAndSubtitles>> {
        val liveData = MutableLiveData<List<TitlesAndSubtitles>>()
        val sections = mutableListOf<TitlesAndSubtitles>()
        var model = mutableListOf<ChapterModelItem>()
        val isExpanded = false
        val gson = Gson()
        model = gson.fromJson("content/chapter/chapter.json".toJson(App.context) ?: "")
        model.add(0, ChapterModelItem(key = "\tIntroduction"))

        sections.add(
            TitlesAndSubtitles(
                SectionsTitleEntity("", "", "References"), listOf(), isExpanded, model
            )
        )
        sections.add(
            TitlesAndSubtitles(
                SectionsTitleEntity("", "", "Definitions"), listOf(), isExpanded, model
            )
        )
        sections.add(
            TitlesAndSubtitles(
                SectionsTitleEntity("", "", "Personnel Required Qualification/Training"),
                listOf(),
                isExpanded, model
            )
        )
        sections.add(
            TitlesAndSubtitles(
                SectionsTitleEntity("", "", "Roles and Responsibilities"), listOf(), isExpanded, model
            )
        )
        sections.add(
            TitlesAndSubtitles(
                SectionsTitleEntity("", "", "Inspection Requirements"), listOf(), isExpanded, model
            )
        )
        sections.add(
            TitlesAndSubtitles(
                SectionsTitleEntity("", "", "Activity Hazard Analysis (AHA) Requirements"),
                listOf(),
                isExpanded, model
            )
        )
        sections.add(
            TitlesAndSubtitles(
                SectionsTitleEntity("", "", "Minimum Plan Requirements"),
                listOf(),
                isExpanded,
                model
            )
        )
        sections.add(
            TitlesAndSubtitles(
                SectionsTitleEntity("", "", "General Requirements"), listOf(), isExpanded, model
            )
        )
        sections.add(
            TitlesAndSubtitles(
                SectionsTitleEntity("", "", "Figures and Tables"), listOf(), isExpanded, model
            )
        )
        sections.add(
            TitlesAndSubtitles(
                SectionsTitleEntity("", "", "Checklists and Forms"), listOf(), isExpanded, model
            )
        )
        liveData.value = sections
        return liveData
    }

}
