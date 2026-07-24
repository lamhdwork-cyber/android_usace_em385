package com.navfac.usace.safety.features.table.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TableBaseModelItem(
        @SerializedName("headline")
        val headline: String? = null,
        @SerializedName("image")
        val image: String? = null,
        @SerializedName("terms")
        val terms: String? = null
) : Serializable