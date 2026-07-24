package com.navfac.usace.safety.features.acronyms.model


import com.google.gson.annotations.SerializedName

data class AcronymModelItem(
        @SerializedName("definition")
        val definition: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("status")
        val status: Int? = null
)