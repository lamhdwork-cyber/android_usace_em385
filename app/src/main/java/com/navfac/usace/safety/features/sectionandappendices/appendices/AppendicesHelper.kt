package com.navfac.usace.safety.features.sectionandappendices.appendices

import android.content.Context
import androidx.core.text.HtmlCompat
import com.navfac.usace.safety.base.extension.readFileAssetIntoString
import com.navfac.usace.safety.features.bottomtab.search.model.SearchModelItem
import java.io.FileNotFoundException

const val CATEGORY_APPENDICES = "appendices"

object AppendicesHelper {

    val data: List<String>
        get() {
            val appendicesList = ArrayList<String>()
            appendicesList.add("A Minimum Basic Outlines for Accident Preventions Plans")
            appendicesList.add("B Emergency Operations")
            appendicesList.add("C Process for Requesting Interpretations")
            appendicesList.add("D Process for Requesting Waivers/Variances")
            appendicesList.add("E Assured Equipment Grounding Conductor Program")
            appendicesList.add("F Floating Plant and Marine Activities Diagrams")
            appendicesList.add("G Manning Levels for Dive Teams")
            return appendicesList
        }


    fun loadAppendicesFile(childPosition: String): ArrayList<String> {
        val htmlList = ArrayList<String>()
        if (childPosition == "0") {
            htmlList.add("a_1_001001.html")
            htmlList.add("a_2_001002.html")
            htmlList.add("a_3_001003.html")
            htmlList.add("a_4_001004.html")
            htmlList.add("a_5_001005.html")
        }
        if (childPosition == "1") {
            htmlList.add("b_1_001006.html")
            htmlList.add("b_2_001007.html")
            htmlList.add("b_3_001008.html")
            htmlList.add("b_4_001009.html")
            htmlList.add("b_5_0010010.html")
            htmlList.add("b_6_001011.html")
        }
        if (childPosition == "2") {
            htmlList.add("c_1_001012.html")
        }
        if (childPosition == "3") {
            htmlList.add("d_1_001013.html")
        }
        if (childPosition == "4") {
            htmlList.add("e_1_001014.html")
        }
        if (childPosition == "5") {
            htmlList.add("f_1_001015.html")
        }
        if (childPosition == "6") {
            htmlList.add("g_1_001016.html")
        }
        return htmlList
    }

    fun parseToSearchItem(context: Context): List<SearchModelItem> {
        val result = mutableListOf<SearchModelItem>()
        var htmlFileIndex = 0

        data.forEachIndexed { index, _ -> //value
            try {
                //Quick implementation for parsing html content and getting name and data from <h2> tags
                //Will check for better implementation later
                val fileNames = loadAppendicesFile(index.toString())
                fileNames.forEach {
                    readFileAssetIntoString(context, "appendices/$it")?.let { content ->
                        var header = content.substring(content.indexOf("<h2>"))
                        header = header.substring(0, content.indexOf("</h2>"))
                        val item = SearchModelItem(category = CATEGORY_APPENDICES,
                                                  file = htmlFileIndex.toString())
                        val headerValues = header.split("<h2>")
                        headerValues.forEachIndexed { index, value ->
                            if (index == 1) {
                                item.name = value.replace("</h2>", "")
                            } else if (index == 2) {
                                val pos = value.indexOf("</h2>")
                                item.data = HtmlCompat.fromHtml(value.substring(0, pos)
                                                    .replace("</h2>", ""),
                                                    HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                            }
                        }
                        item.htmlContentValue = content
                        result.add(item)
                    }
                    htmlFileIndex++
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        return result
    }
}