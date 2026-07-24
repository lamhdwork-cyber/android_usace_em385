package com.navfac.usace.safety.base.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.navfac.usace.safety.base.data.pojo.SectionsSubtitle

@Entity(tableName = "section_subtitle_table")
class SectionsSubtitleEntity(
        val ordinal: String,
        val fileName: String,
        val title: String,
        val subSectionId: String,
        val firstPage: String
) {
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
}
