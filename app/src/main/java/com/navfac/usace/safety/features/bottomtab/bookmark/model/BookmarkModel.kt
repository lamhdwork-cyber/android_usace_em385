package com.navfac.usace.safety.features.bottomtab.bookmark.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.io.Serializable

open class BookmarkModel : RealmObject, Serializable {
    var id: String? = null
    var position: Int? = null
    var groupPosition: Int? = null
    var type: String? = null
    var name: String? = null
    var description: String? = null
    var isImage: Boolean? = null
    var isArticle: Boolean? = null
    var datafile: String? = null
}