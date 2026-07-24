package com.navfac.usace.safety.base.source.local

import android.content.Context
import com.navfac.usace.safety.base.db.*
import com.navfac.usace.safety.base.extension.*
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class SectionsRepository @Inject constructor(
        private val localDataSource: UsaceDao,
        @ApplicationContext private val context: Context
) {

    suspend fun setSubtitleFileFromAssets(list: MutableList<SectionsSubtitleEntity>) {

        list.forEach {
            localDataSource.insertSectionSubtitle(
                SectionsSubtitleEntity(
                    it.ordinal,
                    it.fileName,
                    it.title,
                    it.subSectionId,
                    it.firstPage
                )
            )
        }
    }

    suspend fun setTitleFileFromAssets(list: Array<String>?) {
        list!!.filter { it.endsWith(".title") }
                .forEachIndexed { _ /* index */, data ->
                    val location = "content/sections/$data"
                    val title    = readFileAssetIntoString(context, location).toString()
                    if (!data.isSubsection()) {
                        localDataSource.insertSectionTitle(
                            SectionsTitleEntity(data.extractSubsection(), data, title))
                    }
                }
    }

    suspend fun setAppendicesFileFromAssets(list: Array<String>?) {
        val firstPageAppendices = mutableListOf<String>()

        list?.filter { it.endsWith(".html") }
                ?.forEachIndexed { _, data -> //index, data ->
                    if (data.extractAppendicesFirstPage()) {
                        firstPageAppendices.add(data)
                    }
                }

        list?.filter { it.endsWith(".title") }
                ?.forEachIndexed { index, data ->
                    val title = readFileAssetIntoString(context, "content/appendices/$data").toString()
                    localDataSource.insertAppendices(
                            AppendicesEntity(
                                    data.extractSubsectionOrdinal().toString(),
                                    data,
                                    title,
                                    firstPageAppendices[index]
                            )
                    )
                }

    }

}