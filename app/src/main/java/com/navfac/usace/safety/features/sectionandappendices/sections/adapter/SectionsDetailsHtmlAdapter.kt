package com.navfac.usace.safety.features.sectionandappendices.sections.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.pojo.SectionsHtml
import com.navfac.usace.safety.base.db.AutoUpdatableAdapter
import com.navfac.usace.safety.databinding.ItemSectionsDetailBinding
import javax.inject.Inject
import kotlin.properties.Delegates

class SectionsDetailsHtmlAdapter @Inject constructor() :
            RecyclerView.Adapter<SectionsDetailsHtmlAdapter.Holder>(), AutoUpdatableAdapter {

    internal var collection: List<SectionsHtml> by Delegates.observable(emptyList()) { _ , old, new ->  //prop
        autoNotify(old, new) { o, n -> o == n }
    }

    //internal var clickListener: (SectionsHtml) -> Unit = { _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        Holder.from(parent, R.layout.item_sections_detail)

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            item = collection[position]
            executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class Holder(val binding: ItemSectionsDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup, layout: Int): Holder {
                val inflater = LayoutInflater.from(parent.context)
                val binding  = DataBindingUtil.inflate<ItemSectionsDetailBinding>(
                                        inflater, layout, parent, false)
                return Holder(binding)
            }
        }
    }
}
