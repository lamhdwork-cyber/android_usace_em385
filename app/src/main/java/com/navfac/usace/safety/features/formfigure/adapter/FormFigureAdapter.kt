package com.navfac.usace.safety.features.formfigure.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.formfigure.model.FigureFormModelItem
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class FormFigureAdapter(
    private val context: Context,
    private var formfigurelist: List<FigureFormModelItem>,
    private val realmBookmark: Realm
) : RecyclerView.Adapter<FormFigureAdapter.Holder>() {


    internal var actionListener: (model: FigureFormModelItem) -> Unit = { _ -> }
    internal var dialogActListener: (model: FigureFormModelItem, imageView: ImageView) -> Unit =
        { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_formfigure,
            parent,
            false
        )
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = formfigurelist[position]
        holder.name.text = data.terms
        holder.shortnote.text = data.headline

        holder.itemView.setOnClickListener {
            actionListener.invoke(data)
        }

        holder.menuicon.setOnClickListener {
            dialogActListener.invoke(data, holder.menuicon)
        }

        holder.menuicon.apply {
            val exists =
                realmBookmark.query<BookmarkModel>("name == $0", formfigurelist[position].terms)
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
        return formfigurelist.size
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