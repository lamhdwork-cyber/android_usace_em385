package com.navfac.usace.safety.features.sectionandappendices.sections

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.pojo.SectionsHtml
import com.navfac.usace.safety.base.db.SectionsSubtitleEntity
import com.navfac.usace.safety.base.db.UsaceDao
import com.navfac.usace.safety.base.extension.readFileAssetIntoString
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.Constants
import com.navfac.usace.safety.base.utils.RandomCodeGen
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.preference.PowerPreference
import com.preference.provider.PreferenceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.realm.kotlin.ext.query
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SectionsDetailsViewModel @Inject constructor(
    private val dao: UsaceDao,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    val getAllSectionSubtitle: LiveData<List<SectionsSubtitleEntity>> =
        dao.getAllSectionSubtitle().asLiveData()
//    val getTitlesAndSubtitles: LiveData<List<TitlesAndSubtitles>> = dao.getTitlesAndSubtitles()

    private val _sections = MutableLiveData<List<SectionsHtml>>()
    val sections: LiveData<List<SectionsHtml>> = _sections

    private val _filenameList = MutableLiveData<List<String>>()
    val filenameList: LiveData<List<String>> = _filenameList

    fun initSectionsData(/*getAllAppendices: List<AppendicesEntity>*/) {
        val sectionsHtml = mutableListOf<SectionsHtml>()
        val filename = mutableListOf<String>()
        val lengthThenNatural =
            compareBy<String> { it.take(3).replace("_", "").length }.then(naturalOrder())
        val introductions = readFileAssetIntoString(
            PreferenceProvider.context!!,
            "sections/purpose_of_manual.html"
        ).toString()
        val colors = getColors()
        filename.add("purpose_of_manual.html")
        sectionsHtml.add(
            SectionsHtml(
                "file:///android_asset/sections/", introductions, "",
                PowerPreference.getDefaultFile().getInt(Constants.PREF_FONT_SIZE, 16),
                backColor = colors.first, textColor = colors.second ?: "black"
            )
        )

        context.assets.list("sections")
            ?.filter { it.endsWith(".html") }
            ?.sortedWith(lengthThenNatural)
            ?.forEachIndexed { index, data ->
                val body = readFileAssetIntoString(
                    PreferenceProvider.context!!,
                    "sections/$data"
                ).toString()
                filename.add(data)
                sectionsHtml.add(
                    SectionsHtml(
                        "file:///android_asset/sections/", body, "",
                        PowerPreference.getDefaultFile().getInt(Constants.PREF_FONT_SIZE, 16),
                        backColor = colors.first, textColor = colors.second ?: "black"
                    )
                )
            }

        _sections.value = sectionsHtml
        _filenameList.value = filename
    }

    fun addToBookmark(title: String?, positionNew: Int, fileName: String): Boolean {
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
                            type = "Appendices"
                            description = ""
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

    companion object {
        val directory = "content/sections/"
        val file = "file:///android_asset/content/sections/"
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

}
