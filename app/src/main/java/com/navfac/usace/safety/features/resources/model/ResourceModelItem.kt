package com.navfac.usace.safety.features.resources.model


import com.google.gson.annotations.SerializedName

data class ResourceModelItem(
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("status")
        val status: Int? = null
)