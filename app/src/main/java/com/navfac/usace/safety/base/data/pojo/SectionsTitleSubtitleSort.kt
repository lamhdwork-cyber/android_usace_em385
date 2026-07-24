package com.navfac.usace.safety.base.data.pojo


data class SectionsTitleSubtitleSort(
        val title: String,
        var details: List<TitleSubtitleUnion>?,
        var expand: Boolean = false
) {
    fun testtest(sectionsDetails: MutableList<TitleSubtitleUnion>) {
        details = sectionsDetails
    }
}
