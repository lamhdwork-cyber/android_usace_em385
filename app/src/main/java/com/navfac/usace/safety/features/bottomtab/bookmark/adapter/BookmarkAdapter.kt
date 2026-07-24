package com.navfac.usace.safety.features.bottomtab.bookmark.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel

class BookmarkAdapter(
    private var bookmarklist: List<BookmarkModel>?
) : RecyclerView.Adapter<BookmarkAdapter.Holder>() {

    internal var actionListener: (bookmark: BookmarkModel) -> Unit = { _ -> }
    internal var actionListenerfordetails: (bookmark: BookmarkModel) -> Unit = { _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.custom_bookmark_item,
            parent,
            false
        )
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val bookmark = bookmarklist!![position]
        holder.type.text = bookmark.type?.replace(" New", "")
        holder.name.text = bookmark.name
        holder.decription.text =
            bookmark.description?.replace("purpose_of_manual", "Purpose of manual")

        // Delete Click Action
        holder.deleteicon.setOnClickListener {
            actionListener.invoke(bookmark)
        }

        holder.itemView.setOnClickListener {
            actionListenerfordetails.invoke(bookmark)
        }
    }

    override fun getItemCount(): Int {
        return bookmarklist?.size ?: 0
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var type: TextView
        var name: TextView
        var decription: TextView
        var deleteicon: ImageView

        init {
            type = itemView.findViewById(R.id.search_item_category)
            name = itemView.findViewById(R.id.search_item_name)
            decription = itemView.findViewById(R.id.search_item_data)
            deleteicon = itemView.findViewById(R.id.icon_delete)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<BookmarkModel>) {
        bookmarklist = newList
        notifyDataSetChanged()
    }


}