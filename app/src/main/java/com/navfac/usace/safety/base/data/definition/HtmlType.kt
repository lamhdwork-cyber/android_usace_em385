package com.navfac.usace.safety.base.data.definition

import androidx.annotation.StringDef

@Retention(AnnotationRetention.RUNTIME)
@StringDef(HtmlType.SECTIONS, HtmlType.APPENDICES, HtmlType.CHAPTERS)
annotation class HtmlType {
    companion object {
        const val SECTIONS   = "Sections"
        const val APPENDICES = "Appendices"
        const val RESOURCES = "Resources"
        const val CHAPTERS = "Chapters"
        const val SECTIONS_NEW   = "Sections New"
        const val PDF   = "Pdf"
    }
}
