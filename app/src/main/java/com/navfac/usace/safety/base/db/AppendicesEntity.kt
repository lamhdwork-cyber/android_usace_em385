package com.navfac.usace.safety.base.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.navfac.usace.safety.base.data.pojo.SectionsSubtitle

@Entity(tableName = "appendices_table")
class AppendicesEntity(
        val ordinal: String,
        val fileName: String,
        val title: String,
        val firstPage: String
        ) {
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
}
