package com.navfac.usace.safety.base.data.pojo

data class Sections (
    val header: String,
    val subHeader: List<SubHeader>,
    val expanded: Boolean = false
)

data class SubHeader (
    val name: String
)