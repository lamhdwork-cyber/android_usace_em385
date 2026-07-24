package com.navfac.usace.safety.features.sectionandappendices.appendices.details

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.pojo.AppendicesHtml
import com.navfac.usace.safety.base.db.AppendicesEntity
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
class AppendixDetailsViewModel @Inject constructor(
    private val dao: UsaceDao,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    val getAllAppendices: LiveData<List<AppendicesEntity>> = dao.getAllAppendices().asLiveData()

    private val _appendices = MutableLiveData<List<AppendicesHtml>>()
    val appendices: LiveData<List<AppendicesHtml>> = _appendices

    fun initAppendicesData(/*getAllAppendices: List<AppendicesEntity>*/) {
        val appendicesHtml = mutableListOf<AppendicesHtml>()
        val colors = getColors()

        PreferenceProvider.context?.assets!!.list("content/appendices")
            ?.filter { it.endsWith(".html") }
            ?.forEachIndexed { index, data ->
                val body = readFileAssetIntoString(
                    PreferenceProvider.context!!,
                    "content/appendices/$data"
                ).toString()
                appendicesHtml.add(
                    AppendicesHtml(
                        "file:///android_asset/content/appendices/", body, "",
                        PowerPreference.getDefaultFile().getInt(Constants.PREF_FONT_SIZE, 16),
                        backColor = colors.first, textColor = colors.second ?: "black"
                    )
                )
            }

        _appendices.value = appendicesHtml
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
