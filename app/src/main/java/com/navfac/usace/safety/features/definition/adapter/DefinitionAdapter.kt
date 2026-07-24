package com.navfac.usace.safety.features.definition.adapter

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
import com.navfac.usace.safety.features.definition.model.DefinitionBaseItem
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class DefinitionAdapter(
    private val context: Context,
    private var definitionslist: List<DefinitionBaseItem>,
    private val realmBookmark: Realm
) : RecyclerView.Adapter<DefinitionAdapter.Holder>() {

    internal var actionListener: (model: DefinitionBaseItem, imageView: ImageView) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_acronyms,
                parent,
                false
        )
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val baseSharedPreference = BaseSharedPreference(context)
        val data = definitionslist[position]
        holder.name.text = data.name
        holder.definition.text = data.definition

        when (data.status) {
            0 -> {
                holder.status.text = ""
            }
            1 -> {
                holder.status.text = "UPDATED"
                holder.status.setTextColor(Color.parseColor("#EE1D23"))
            }
            2 -> {
                holder.status.text = context.getString(R.string.item_new)   //"NEW"
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.newItemColor))   //Color.parseColor("#4CAF50"))
            }
        }

        holder.menuicon.setOnClickListener {
            actionListener.invoke(data, holder.menuicon)
        }

        holder.menuicon.apply {
            val exists = realmBookmark.query<BookmarkModel>("name == $0", definitionslist[position].name)
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
        return definitionslist.size
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val definition: TextView
        val status: TextView
        val menuicon: ImageView

        init {
            name = itemView.findViewById(R.id.item_acronyms_name)
            definition = itemView.findViewById(R.id.item_acronyms_definition)
            status = itemView.findViewById(R.id.item_acronyms_filestatus)
            menuicon = itemView.findViewById(R.id.item_acronyms_menu)
        }
    }
}