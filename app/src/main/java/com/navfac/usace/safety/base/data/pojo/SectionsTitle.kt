package com.navfac.usace.safety.base.data.pojo

import com.google.gson.annotations.SerializedName

data class SectionsTitle (
        @SerializedName("title")
        val title: String,
        @SerializedName("fileName")
        val fileName: String
)
