package com.navfac.usace.safety.features.sectionandappendices.appendices.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.pojo.SectionsTitleSubtitleSort
import com.navfac.usace.safety.base.db.AppendicesEntity
import com.navfac.usace.safety.base.db.AutoUpdatableAdapter
import com.navfac.usace.safety.base.db.TitlesAndSubtitles
import com.navfac.usace.safety.databinding.ItemAppendicesBinding
import com.navfac.usace.safety.databinding.ItemSectionsBinding
import javax.inject.Inject
import kotlin.properties.Delegates

class AppendicesMainAdapter @Inject constructor() :
    RecyclerView.Adapter<AppendicesMainAdapter.Holder>(),
        AutoUpdatableAdapter {

    internal var collection: List<AppendicesEntity> by Delegates.observable(emptyList()) { prop, old, new ->
        autoNotify(old, new) { o, n -> o.title == n.title }
    }

    internal var showAction: Boolean = false
    internal var clickListener: (Pair<AppendicesEntity, Int?>) -> Unit = { _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder.from(
            parent,
            R.layout.item_appendices
        )

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            item = collection[position]
            executePendingBindings()

            itemAppendicesClHeader.setOnClickListener {
                clickListener((Pair(collection[position], holder.adapterPosition)))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class Holder(val binding: ItemAppendicesBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup, layout: Int): Holder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ItemAppendicesBinding>(
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
