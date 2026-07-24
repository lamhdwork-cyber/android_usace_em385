package com.navfac.usace.safety.features.htmlpage

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.application.App
import com.navfac.usace.safety.base.data.definition.HtmlType
import com.navfac.usace.safety.base.db.AppendicesEntity
import com.navfac.usace.safety.base.db.SectionsSubtitleEntity
import com.navfac.usace.safety.base.db.UsaceDao
import com.navfac.usace.safety.base.extension.ioToMain
import com.navfac.usace.safety.base.extension.readFileAssetIntoString
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.Constants
import com.navfac.usace.safety.base.utils.RandomCodeGen
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.preference.PowerPreference
import com.preference.provider.PreferenceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxkotlin.addTo
import io.realm.kotlin.ext.query
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class HtmlPageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: UsaceDao
) : BaseViewModel() {
    private val _currentPageBookmarked = MutableLiveData(false)
    val currentPageBookmarked: LiveData<Boolean> = _currentPageBookmarked

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _htmlPages = MutableLiveData<List<HtmlPage>>()
    val htmlPages: LiveData<List<HtmlPage>> = _htmlPages

    private val _htmlType = MutableLiveData<String>()
    val htmlType: LiveData<String> = _htmlType

    private val _goToPage = MutableLiveData<Int>()
    val goToPage: LiveData<Int> = _goToPage

    private val _chapterID = MutableLiveData<Int>()
    val chapterID: LiveData<Int> = _chapterID

    private val _chapterKey = MutableLiveData<String>()
    val chapterKey: LiveData<String> = _chapterKey

    private val _sectionName = MutableLiveData<String>()
    val sectionName: LiveData<String> = _sectionName

    private val _goToPageCrawl = MutableLiveData<String>()
    val goToPageCrawl: LiveData<String> = _goToPageCrawl

    private val _textSize =
        MutableLiveData(PowerPreference.getDefaultFile().getInt(Constants.PREF_FONT_SIZE, 16))
    val textSize: LiveData<Int> = _textSize

    private val _textAlignment = MutableLiveData("")
    val textAlignment: LiveData<String> = _textAlignment

    val sections = MutableLiveData<List<SectionsSubtitleEntity>>()
    val appendices = MutableLiveData<List<AppendicesEntity>>()

    init {
        dao.getAllAppendicesRx().ioToMain()
            .subscribe({
                appendices.value = it
            }, {
                it.printStackTrace()
            }).addTo(disposeBag)

        dao.getAllSectionSubtitleRx().ioToMain()
            .subscribe({
                sections.value = it
            }, {
                it.printStackTrace()
            }).addTo(disposeBag)
    }

    fun setCurrentPageBookmarked(value: Boolean) {
        _currentPageBookmarked.value = value
    }

    fun toggleFontSize(increment: Boolean) {
        _textSize.value?.let {
            if (increment && it < 32) {
                _textSize.value = it + 4
            } else if (!increment && it > 16) {
                _textSize.value = it - 4
            }
        }
    }

    fun setFontSize(size: Int) {
        if (_textSize.value != size)
            _textSize.value = size
    }

    fun setTextAlignment(alignment: String) {
        if (_textAlignment.value != alignment)
            _textAlignment.value = alignment
    }

    fun setGoToPage(page: Int) {
        _goToPage.value = page
    }

    fun setGoToCrawlPage(page: String) {
        _goToPageCrawl.value = page
    }

    fun setChapterID(chapterId: Int) {
        _chapterID.value = chapterId
    }

    fun setChapterKey(chapterKey: String) {
        _chapterKey.value = chapterKey
    }

    fun setSectionName(sectionName: String) {
        _sectionName.value = sectionName
    }

    fun setHtmlType(@HtmlType type: String) {
        _htmlType.value = type
        loadContent(type)
        _title.value = "" //type
    }

    private fun loadContent(@HtmlType type: String) = when (type) {
        HtmlType.SECTIONS -> {
            sectionsToHtmlPage()
        }

        HtmlType.SECTIONS_NEW -> {
            sectionsNewToHtmlPage()
        }

        HtmlType.RESOURCES -> {
            val result = mutableListOf<HtmlPage>()
            val pdfFile = File(App.downloadDir.path, Constants.RESOURCE_FILE_DEFAULT)
            result.add(
                HtmlPage(
                    isPdf = true,
                    filePath = pdfFile.path
                )
            )

            _htmlPages.value = result
        }

        HtmlType.PDF -> {
            val result = mutableListOf<HtmlPage>()
            val pdfFile = File(App.downloadDir.path, Constants.FILE_PDF_DEFAULT)
            result.add(
                HtmlPage(
                    isPdf = true,
                    filePath = pdfFile.path
                )
            )

            _htmlPages.value = result
        }

        else -> {
            chapterToHtmlPage()
        }
    }

    private fun sectionsToHtmlPage() {
        val result = mutableListOf<HtmlPage>()

        val lengthThenNatural = compareBy<String> {
            it.take(3)
                .replace("_", "").length
        }
            .then(naturalOrder())
        val colors = getColors()
        val introductions = readFileAssetIntoString(
            PreferenceProvider.context!!,
            "sections/purpose_of_manual.html"
        ).toString()
        val man = getTitleFromBody(introductions)
        result.add(
            HtmlPage(
                "file:///android_asset/sections/", introductions, "",
                PowerPreference.getDefaultFile().getInt(Constants.PREF_FONT_SIZE, 16),
                "purpose_of_manual.html", title = man.first, description = man.second,
                backColor = colors.first, textColor = colors.second ?: "black"
            )
        )

        context.assets.list("sections")
            ?.filter { it.endsWith(".html") }
            ?.sortedWith(lengthThenNatural)
            ?.forEachIndexed { _, name -> //index
                val body = readFileAssetIntoString(
                    PreferenceProvider.context!!,
                    "sections/$name"
                ).toString()
                val desc = getTitleFromBody(body)
                result.add(
                    HtmlPage(
                        "file:///android_asset/sections/", body, "",
                        PowerPreference.getDefaultFile().getInt(Constants.PREF_FONT_SIZE, 16),
                        name, title = desc.first, description = desc.second,
                        backColor = colors.first, textColor = colors.second ?: "black"
                    )
                )
            }

        _htmlPages.value = result
    }

    private fun sectionsNewToHtmlPage() {
        val result = mutableListOf<HtmlPage>()

        val lengthThenNatural = compareBy<String> {
            val index = it.substringAfter("${chapterID.value}-").substringBefore(".")
            index.toInt()
        }
        val colors = getColors()

        context.assets.list("content/sections_new")
            ?.filter { it.endsWith(".html") && it.startsWith("${chapterID.value}-") }
            ?.sortedWith(lengthThenNatural)
            ?.forEachIndexed { _, name -> //index
                val body = readFileAssetIntoString(
                    PreferenceProvider.context!!,
                    "content/sections_new/$name"
                ).toString()
                val desc = getTitleFromBody(body)
                result.add(
                    HtmlPage(
                        "file:///android_asset/content/sections_new/",
                        body,
                        "",
                        PowerPreference.getDefaultFile().getInt(Constants.PREF_FONT_SIZE, 16),
                        name,
                        title = desc.first,
                        description = desc.second,
                        backColor = colors.first,
                        textColor = colors.second ?: "black",
                        chapter = chapterKey.value ?: ""
                    )
                )
            }

        _htmlPages.value = result
    }


    private fun appendicesToHtmlPage() {
        val result = mutableListOf<HtmlPage>()
        val colors = getColors()
        PreferenceProvider.context?.assets?.list("content/appendices")
            ?.filter { it.endsWith(".html") }
            ?.forEachIndexed { _, name -> //index
                val body = readFileAssetIntoString(
                    PreferenceProvider.context!!,
                    "content/appendices/$name"
                ).toString()
                val desc = getTitleFromBody(body)
                result.add(
                    HtmlPage(
                        "file:///android_asset/content/appendices/", body, "",
                        PowerPreference.getDefaultFile().getInt(Constants.PREF_FONT_SIZE, 16),
                        name, title = desc.first, description = desc.second,
                        backColor = colors.first, textColor = colors.second ?: "black"
                    )
                )
            }

        _htmlPages.value = result
    }

    private fun chapterToHtmlPage() {
        val result = mutableListOf<HtmlPage>()
        val colors = getColors()
        val introductions = readFileAssetIntoString(
            PreferenceProvider.context!!,
            "content/chapter/purpose_of_manual.html"
        ).toString()
        val man = getTitleFromBody(introductions)
        result.add(
            HtmlPage(
                "file:///android_asset/content/chapter/",
                introductions,
                "",
                PowerPreference.getDefaultFile().getInt(Constants.PREF_FONT_SIZE, 16),
                "purpose_of_manual.html",
                title = man.first,
                description = man.second,
                backColor = colors.first,
                textColor = colors.second ?: "black",
                chapter = chapterKey.value ?: ""
            )
        )
        _htmlPages.value = result
    }

    fun getBookmarkDetails(currentItem: Int, type: Int): Pair<String, String> {
        var title = ""
        var description = ""
        val lengthThenNatural = compareBy<String> { it.length }.then(naturalOrder())
        context.assets.list(/*"sections"*/ if (type == 1) "sections" else "appendices")
            ?.filter { it.endsWith(".html") }
            ?.sortedWith(lengthThenNatural)
            ?.forEachIndexed { index, data ->
                if (index == /*(if (type == 1) */currentItem.minus(1) /*else currentItem)*/) {
                    val body = readFileAssetIntoString(
                        PreferenceProvider.context!!,
                        (if (type == 1) "sections/" else "appendices/") + data
                    ).toString()
                    val header = body.substringAfter("<h1>").substringBefore("</h1>")
                    title = header.replace("&nbsp;", " ")
                        .replace("</new>", "")
                        .replace("<new>", "")

                    if (body.contains("<h2>")) {
                        val subHeader = body.substringAfter("<h2>")
                            .substringBefore("</h2>")
                        description = subHeader.replace("&#8211;", " ")
                            .replace("</new>", "")
                            .replace("<new>", "")
                    }
                }
            }
        return Pair(title, description)
    }


    private fun getTitleFromBody(body: String): Pair<String, String?> {
        val header = body.substringAfter("<h1>").substringBefore("</h1>")
        val title = header.replace("&nbsp;", " ")
            .replace("</new>", "")
            .replace("<new>", "")
        var description: String? = null

        if (body.contains("<h2>")) {
            val subHeader = body.substringAfter("<h2>").substringBefore("</h2>")
            description = subHeader.replace("&#8211;", " ")
                .replace("</new>", "")
                .replace("<new>", "")
        }

        return Pair(title, description)
    }

    private fun getColors(): Pair<String, String?> {
        var cv = ContextCompat.getColor(context, R.color.myColorForeground)
        val textColor: String = String.format(
            Locale.US, "#%02X%02X%02X%02X",
            Color.red(cv), Color.green(cv), Color.blue(cv), Color.alpha(cv)
        )
        cv = ContextCompat.getColor(context, R.color.myColorBackground)
        val backColor: String = String.format(
            Locale.US, "#%02X%02X%02X%02X",
            Color.red(cv), Color.green(cv), Color.blue(cv), Color.alpha(cv)
        )
        return Pair(backColor, textColor)
    }

    fun addToBookmark(
        title: String?,
        positionNew: Int,
        fileName: String,
        des: String? = ""
    ): Boolean {
        val bookmark =
            realmBookmark.query<BookmarkModel>("datafile == $0", fileName)
                .first()
                .find()
        if (bookmark != null) {
            Toast.makeText(context, "Already Added as Bookmark", Toast.LENGTH_LONG).show()
            return false
        } else {
            viewModelScope.launch {
                realmBookmark.write {
                    copyToRealm(
                        BookmarkModel().apply {
                            id = RandomCodeGen.generate()
                            name = title
                            type =
                                if (_htmlType.value == HtmlType.SECTIONS) "Sections" else if (_htmlType.value == HtmlType.APPENDICES) "Appendices" else if (_htmlType.value == HtmlType.SECTIONS_NEW) "Sections New" else "Chapter"
                            description = des
                            position = positionNew
                            isImage = false
                            isArticle = true
                            datafile = fileName
                        }
                    )
                }
                Toast.makeText(context, "Added To Bookmark", Toast.LENGTH_LONG).show()
            }
            return true
        }
    }
}
