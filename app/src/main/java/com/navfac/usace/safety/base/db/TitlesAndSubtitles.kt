package com.navfac.usace.safety.base.db

import androidx.room.Embedded
import androidx.room.Relation
import com.navfac.usace.safety.features.sectionandappendices.chapter.model.ChapterModelItem

data class TitlesAndSubtitles(
    @Embedded
    val title: SectionsTitleEntity,
    @Relation(
            parentColumn = "ordinal",
            entityColumn = "subSectionId"
    )
    val subtitles: List<SectionsSubtitleEntity>,
    var expanded: Boolean? = false,
    val chapters: List<ChapterModelItem>? = listOf(),
)

