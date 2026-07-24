package com.navfac.usace.safety.features.bottomtab.search.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.platform.BaseSharedPreference
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.bottomtab.search.model.SearchModelItem
import com.navfac.usace.safety.features.sectionandappendices.sections.CATEGORY_CHAPTER
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class SearchAdapter(
    private val context: Context,
    private var searchlist: List<SearchModelItem>,
    private val realmBookmark: Realm
) :
    RecyclerView.Adapter<SearchAdapter.Holder>() {


    internal var actionListener: (model: SearchModelItem) -> Unit = { _ -> }
    internal var actionListenerforBookmark: (model: SearchModelItem, imageView: ImageView) -> Unit =
        { _, _ -> }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.custom_search_item,
            parent,
            false
        )
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val baseSharedPreference = BaseSharedPreference(context)
        val data = searchlist[position]

        // Set data to item view
        holder.name.text = data.name
        holder.data.text = if (data.category != CATEGORY_CHAPTER) data.data else ""
        holder.category.text = data.category

        if (baseSharedPreference.getBooleanData("state")!! && (data.status == 1)) {
            holder.status.text = "UPDATED"
            holder.status.setTextColor(Color.parseColor("#EE1D23"))
        } else {
            if (data.status == 2) {
                holder.status.text = context.getString(R.string.item_new)   //"NEW"
                holder.status.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.newItemColor
                    )
                )   //Color.parseColor("#4CAF50"))
            } else {
                holder.status.text = ""
            }
        }

        holder.itemView.setOnClickListener {
            actionListener.invoke(data)
        }

        holder.bookmarkicon.setOnClickListener {
            actionListenerforBookmark.invoke(data, holder.bookmarkicon)
        }

        holder.bookmarkicon.apply {
            val exists = realmBookmark.query<BookmarkModel>("name == $0", searchlist[position].name)
                .first()
                .find() != null

            val drawableRes = if (exists) {
                R.drawable.ic_bookmark_red_asset
            } else {
                R.drawable.ic_bookmark_gray_asset
            }

            setImageDrawable(ResourcesCompat.getDrawable(resources, drawableRes, null))
        }
    }

    override fun getItemCount(): Int {
        return searchlist.size
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val category: TextView
        val name: TextView
        val data: TextView
        val status: TextView
        val bookmarkicon: ImageView

        init {
            category = itemView.findViewById(R.id.search_item_categorys)
            name = itemView.findViewById(R.id.search_item_name)
            data = itemView.findViewById(R.id.search_item_data)
            status = itemView.findViewById(R.id.search_item_status)
            bookmarkicon = itemView.findViewById(R.id.item_search_bookmark)
        }
    }


    /*
     * Filter list method
     * */
    fun filterlist(ModelList: List<SearchModelItem>?) {
        searchlist = ModelList!!
        notifyDataSetChanged()
    }
}