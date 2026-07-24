package com.navfac.usace.safety.features.formfigure.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FigureFormModelItem(
        @SerializedName("headline")
        val headline: String? = null,
        @SerializedName("image")
        val image: String? = null,
        @SerializedName("terms")
        val terms: String? = null
) : Serializable