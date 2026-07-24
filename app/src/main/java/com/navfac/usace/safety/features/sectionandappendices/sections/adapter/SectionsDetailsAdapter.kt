package com.navfac.usace.safety.features.sectionandappendices.sections.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.pojo.TitleSubtitleUnion
import com.navfac.usace.safety.base.db.AutoUpdatableAdapter
import com.navfac.usace.safety.base.db.SectionsSubtitleEntity
import com.navfac.usace.safety.base.db.TitlesAndSubtitles
import com.navfac.usace.safety.databinding.ListItemBinding
import com.navfac.usace.safety.databinding.ListSubtitleBinding
import javax.inject.Inject
import kotlin.properties.Delegates

class SectionsDetailsAdapter @Inject constructor() :
    RecyclerView.Adapter<SectionsDetailsAdapter.Holder>(),
        AutoUpdatableAdapter {

    internal var collection: List<SectionsSubtitleEntity> by Delegates.observable(emptyList()) { _ /* prop */, old, new ->
        autoNotify(old, new) { o, n -> o.title == n.title }
    }

//    internal var clickListener: ((Pair<SectionsSubtitleEntity, Int>) -> Unit?)? = null
    internal var clickListener: (SectionsSubtitleEntity) -> Unit = { _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder.from(
            parent,
            R.layout.list_subtitle
        )

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            item = collection[position]
            executePendingBindings()
            holder.itemView.setOnClickListener { clickListener(collection[position]) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class Holder(val binding: ListSubtitleBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup, layout: Int): Holder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ListSubtitleBinding>(
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
