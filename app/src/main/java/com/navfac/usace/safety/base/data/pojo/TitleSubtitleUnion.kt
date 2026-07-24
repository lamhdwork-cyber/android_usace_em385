package com.navfac.usace.safety.base.data.pojo

data class TitleSubtitleUnion (
        var title: String? = null,
        var subtitle: String? = null,
        var fileName: String? = null,
        val ordinal: String? = null,
        var expand: Boolean? = false
)

