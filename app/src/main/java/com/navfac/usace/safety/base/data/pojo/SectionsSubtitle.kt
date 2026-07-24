package com.navfac.usace.safety.base.data.pojo

import com.google.gson.annotations.SerializedName

data class SectionsSubtitle (
        @SerializedName("subtitle")
        val subtitle: String,
        @SerializedName("fileName")
        val fileName: String
)
