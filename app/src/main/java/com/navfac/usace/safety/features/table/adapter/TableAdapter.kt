package com.navfac.usace.safety.features.table.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.table.model.TableBaseModelItem
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class TableAdapter(
    private val tableList: MutableList<TableBaseModelItem>, private var realm: Realm
) : RecyclerView.Adapter<TableAdapter.Holder>() {


    internal var actionListener: (model: TableBaseModelItem) -> Unit = { _ -> }
    internal var actionListenerfordialog: (model: TableBaseModelItem, imageView: ImageView) -> Unit =
        { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_formfigure, parent, false
        )
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = tableList[position]
        holder.name.text = data.terms
        holder.shortnote.text = data.headline

        holder.menuicon.setOnClickListener {
            actionListenerfordialog.invoke(data, holder.menuicon)
        }


        holder.itemView.setOnClickListener {
            actionListener.invoke(data)
        }

        holder.menuicon.apply {
            val exists = realm.query<BookmarkModel>("name == $0", tableList[position].terms).first()
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
        return tableList.size
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val shortnote: TextView
        val menuicon: ImageView

        init {
            name = itemView.findViewById(R.id.item_title_figform)
            shortnote = itemView.findViewById(R.id.item_shortnote)
            menuicon = itemView.findViewById(R.id.item_icon_menu)
        }
    }
}