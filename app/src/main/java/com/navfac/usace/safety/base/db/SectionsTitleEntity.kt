package com.navfac.usace.safety.base.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "section_title_table")
class SectionsTitleEntity(
        val ordinal: String,
        val fileName: String,
        val title: String
) {
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
}
