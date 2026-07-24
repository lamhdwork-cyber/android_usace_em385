package com.navfac.usace.safety.features.sectionandappendices.chapter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.features.sectionandappendices.chapter.model.ChapterModelItem

class ChapterAdapter(
    private var chapterlist: List<ChapterModelItem>
) : RecyclerView.Adapter<ChapterAdapter.Holder>() {

    internal var clickListener: (index : Int,model: ChapterModelItem) -> Unit = {_,_->}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_chapter,
            parent,
            false
        )
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = chapterlist[position]
        holder.title.text = if (data.id != null) "${data.id+1} ${data.key}" else data.key
        holder.itemView.setOnClickListener {
            clickListener.invoke(position,data)
        }

    }

    override fun getItemCount(): Int {
        return chapterlist.size
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView

        init {
            title = itemView.findViewById(R.id.item_sections_tv_header)
        }
    }
}