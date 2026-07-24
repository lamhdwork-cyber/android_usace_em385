package com.navfac.usace.safety.features.bottomtab.search.model


import com.google.gson.annotations.SerializedName

data class SearchModelItem(
        @SerializedName("category")
        val category: String? = null,
        @SerializedName("data")
        var `data`: String? = null,
        @SerializedName("file")
        val `file`: String? = null,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("status")
        val status: Int? = null,
        @SerializedName("html_content_value")
        var htmlContentValue: String? = null,
        var id : Int = 0,
        var key : String?=null,
        var page : Int?= 0
)