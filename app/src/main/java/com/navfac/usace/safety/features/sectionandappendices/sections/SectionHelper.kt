package com.navfac.usace.safety.features.sectionandappendices.sections

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.extension.fromJson
import com.navfac.usace.safety.base.extension.readFileAssetIntoString
import com.navfac.usace.safety.base.extension.toJson
import com.navfac.usace.safety.base.utils.Constants
import com.navfac.usace.safety.features.bottomtab.search.model.SearchModelItem
import com.navfac.usace.safety.features.htmlpage.HtmlPage
import com.navfac.usace.safety.features.sectionandappendices.chapter.model.ChapterModelItem
import com.preference.PowerPreference
import org.jsoup.Jsoup
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.ArrayList as ArrayList1

const val CATEGORY_SECTION = "section"
const val CATEGORY_CHAPTER = "chapter"

object SectionHelper {

    fun loadSectionsFile(
        context: Context,
        groupPosition: String,
        childPosition: String
    ): ArrayList<HtmlPage> {
        val htmlList = ArrayList<HtmlPage>()
        val alphabet = "abcdefghijklmnopqrstuvwxyz"

        // For Group 1
        if (groupPosition == "0" && childPosition == "0") {
            getPageInfo(
                context,
                "file:///android_asset/content/chapter/purpose_of_manual.html"
            )?.let {
                htmlList.add(it)
            }

        } else {
            val prefix = "${groupPosition}${alphabet[childPosition.toInt()]}"
            listAssetFiles("sections", context).filter { file -> file.startsWith(prefix) }
                .forEach { name ->
                    getPageInfo(context, "file:///android_asset/content/sections_new/$name")?.let {
                        htmlList.add(it)
                    }
                }
        }
        return htmlList
    }

    private fun getPageInfo(context: Context, url: String): HtmlPage? {
        if (url.isEmpty())
            return null
        val name = Uri.parse(url).lastPathSegment ?: ""
        if (name.isEmpty())
            return null
        try {
            val path = url.substring(0, url.length - name.length)
            val pref = PowerPreference.getDefaultFile()
            val body =
                readFileAssetIntoString(context, url.replace("file:///android_asset/", "")) ?: ""
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
            return HtmlPage(
                path, body, pref.getString(Constants.PREF_TEXT_ALIGN),
                pref.getInt(Constants.PREF_FONT_SIZE), name,
                backColor = backColor, textColor = textColor
            )
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
        return null
    }

    fun listAssetFiles(path: String, context: Context): List<String> {
        return context.assets.list(path)?.filter { it.endsWith(".html") } as ArrayList
    }

    fun parseToSearchItems(context: Context): List<SearchModelItem> {
        val result = mutableListOf<SearchModelItem>()
        loadChapter(context)
        chapterList.forEachIndexed { index, chapter ->
            val sectionsFiles = getPageInfo(
                context,
                "file:///android_asset/content/chapter/purpose_of_manual.html"
            )?.let {
                arrayListOf(it)
            }

            sectionsFiles?.forEachIndexed { pageIndex, model ->
                model.fileName?.let { name ->
                    readFileAssetIntoString(
                        context,
                        "content/chapter/$name"
                    )?.let { content ->
                        val searchItem = SearchModelItem(
                            category = CATEGORY_CHAPTER,
                            file = name,
                            htmlContentValue = content
                        )

                        val doc = Jsoup.parse(content)
                        doc.select("h1").firstOrNull()?.let { titleElement ->
                            var nameElement = ""
                            titleElement.childNodes().forEach { node ->
                                nameElement += node.toString()
                            }
                            searchItem.name = HtmlCompat.fromHtml(
                                nameElement,
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            ).toString()
                        }
                        doc.select("h2").firstOrNull()?.let { dataElement ->
                            searchItem.data = HtmlCompat.fromHtml(
                                dataElement.childNodes().firstOrNull().toString(),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            ).toString()
                        }
                        if (searchItem.name.isNullOrBlank())
                            searchItem.name = chapter.key

                        if (searchItem.name?.contains("Introduction") == true)
                            result.add(searchItem)
                    }
                }
            }

            if (index != 0) {
                titleSections.forEachIndexed { idx, title ->
                    val titleRename =
                        title.replace(" / ", " and ").replace("/", "-").replace("+", "and")
                    val fileName = "${chapter.id!! + 1}-${idx + 1}.$titleRename.html"
                    val sectionsFiles = getPageInfo(
                        context,
                        "file:///android_asset/content/sections_new/$fileName"
                    )?.let {
                        arrayListOf(it)
                    }
                    sectionsFiles?.forEachIndexed { pageIndex, model ->
                        model.fileName?.let { name ->
                            readFileAssetIntoString(
                                context,
                                "content/sections_new/$name"
                            )?.let { content ->
                                val searchItem = SearchModelItem(
                                    category = CATEGORY_SECTION,
                                    file = name,
                                    htmlContentValue = content,
                                    data = title,
                                    key = "${chapter.id} ${chapter.key}",
                                    id = chapter.id,
                                    page = idx
                                )

                                if (searchItem.name.isNullOrBlank())
                                    searchItem.name = chapter.key
                                result.add(searchItem)
                            }
                        }
                    }
                }
            }
        }

        return result
    }

    private var chapterList = mutableListOf<ChapterModelItem>()
    private val titleSections = mutableListOf<String>()
    fun loadChapter(context: Context) {
        val gson = Gson()
        chapterList =
            gson.fromJson("content/chapter/chapter.json".toJson(context) ?: "")
        chapterList.add(0, ChapterModelItem(key = "\tIntroduction"))
        titleSections()
    }

    fun titleSections() {
        titleSections.clear()
        titleSections.add("References")
        titleSections.add("Definitions")
        titleSections.add("Personnel Required Qualification-Training")
        titleSections.add("Roles and Responsibilities")
        titleSections.add("Inspection Requirements")
        titleSections.add("Activity Hazard Analysis (AHA) Requirements")
        titleSections.add("Minimum Plan Requirements")
        titleSections.add("General Requirements")
        titleSections.add("Figures and Tables")
        titleSections.add("Checklists and Forms")
    }


}