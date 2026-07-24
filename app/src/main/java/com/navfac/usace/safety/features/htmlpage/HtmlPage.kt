package com.navfac.usace.safety.features.htmlpage

data class HtmlPage(
    var filePath: String = "",
    var body: String = "",
    var textAlignment: String = "",
    var textSize: Int = 20,
    var fileName: String? = null,
    var bookmarked: Boolean = false,
    var title: String? = null,
    var description: String? = null,
    var backColor: String = "white",
    var textColor: String = "black",
    var chapter : String = "",
    var isLink : Boolean = false,
    var link : String = "",
    var isPdf : Boolean = false
) {
    fun getUrl(): String {
        return "$filePath$fileName"
    }
}