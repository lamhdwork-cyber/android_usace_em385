package com.navfac.usace.safety.base.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.navfac.usace.safety.base.data.pojo.SectionsSubtitle
import com.navfac.usace.safety.base.data.pojo.SectionsTitle
import com.navfac.usace.safety.base.data.pojo.TitleSubtitleUnion

class DataTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun sectionsTitlesToString(sectionsTitle: SectionsTitle): String {
        return gson.toJson(sectionsTitle)
    }

    @TypeConverter
    fun stringToSectionsTitle(data: String): SectionsTitle {
        val listType = object : TypeToken<SectionsTitle>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun sectionsSubtitlesToString(sectionsTitle: SectionsSubtitle): String {
        return gson.toJson(sectionsTitle)
    }

    @TypeConverter
    fun stringToSectionsSubtitle(data: String): SectionsSubtitle {
        val listType = object : TypeToken<SectionsSubtitle>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun titleAndSubtitleUnionToString(testModel: TitleSubtitleUnion): String {
        return gson.toJson(testModel)
    }

    @TypeConverter
    fun stringToTitleAndSubtitle(data: String): TitleSubtitleUnion {
        val listType = object : TypeToken<TitleSubtitleUnion>() {}.type
        return gson.fromJson(data, listType)
    }
}