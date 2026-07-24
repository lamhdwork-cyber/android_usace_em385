package com.navfac.usace.safety.features.definition.model


import com.google.gson.annotations.SerializedName

data class DefinitionBaseItem(
        @SerializedName("definition")
        val definition: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("status")
        val status: Int? = null
)