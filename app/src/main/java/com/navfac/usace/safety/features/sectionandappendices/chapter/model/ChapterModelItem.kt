package com.navfac.usace.safety.features.sectionandappendices.chapter.model
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChapterModelItem(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("key")
    val key: String? = null,
):Parcelable