package com.navfac.usace.safety.features.sectionandappendices.sections.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.db.AutoUpdatableAdapter
import com.navfac.usace.safety.base.db.SectionsTitleEntity
import com.navfac.usace.safety.base.db.TitlesAndSubtitles
import com.navfac.usace.safety.databinding.ItemSectionsBinding
import com.navfac.usace.safety.features.sectionandappendices.chapter.adapter.ChapterAdapter
import com.navfac.usace.safety.features.sectionandappendices.chapter.model.ChapterModelItem
import javax.inject.Inject
import kotlin.properties.Delegates

class SectionsAdapter @Inject constructor() :
    RecyclerView.Adapter<SectionsAdapter.Holder>(),
    AutoUpdatableAdapter {
    internal var collection: List<TitlesAndSubtitles> by Delegates.observable(emptyList()) { _ /* prop */, old, new ->
        autoNotify(old, new) { o, n -> o.title == n.title }
    }

    internal var clickListener: (Int, ChapterModelItem,SectionsTitleEntity) -> Unit = { _,_,_ -> }
    internal var clickHeaderListener: (Int, SectionsTitleEntity) -> Unit = { _, _ -> }

    var toScroll = MutableLiveData<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder.from(parent, R.layout.item_sections)

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            item = collection[position]
            val adapter = ChapterAdapter(collection[position].chapters!!)
            itemSectionRvSubtitle.adapter = adapter
            executePendingBindings()

            itemSectionsTvHeader.setOnClickListener {
                if (collection[position].expanded == null)
                    clickHeaderListener.invoke(position, collection[position].title)
                else {
                    collection[position].expanded = !collection[position].expanded!!
                    notifyItemChanged(position)

                    if (position >= itemCount - 1) toScroll.value = position
                }
            }

            adapter.clickListener = { index, data ->
                clickListener.apply {
                    clickListener(position, data,collection[position].title)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateExpanded() {
        for (i in 0 until itemCount){
            collection[i].expanded = null
        }
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemSectionsBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup, layout: Int): Holder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ItemSectionsBinding>(
                    inflater,
                    layout,
                    parent,
                    false
                )
                return Holder(
                    binding
                )
            }
        }
    }
}
