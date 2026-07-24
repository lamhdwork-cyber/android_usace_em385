package com.navfac.usace.safety.features.homesearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.utils.AutoUpdatableAdapter
import com.navfac.usace.safety.databinding.ItemSearchBinding
import com.navfac.usace.safety.features.bottomtab.search.model.SearchModelItem
import java.io.File
import javax.inject.Inject
import kotlin.properties.Delegates

class SearchResultAdapter @Inject constructor() :
        RecyclerView.Adapter<SearchResultAdapter.Holder>(),
        AutoUpdatableAdapter {

    internal var clickListener: (SearchModelItem) -> Unit = { _ -> }
    internal var bookmarkListener: (SearchModelItem) -> Unit = { _ -> }

    internal var collection: List<SearchModelItem> by Delegates.observable(emptyList()) { prop, old, new ->
        autoNotify(old, new) { o, n -> o.name == n.name }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            Holder.from(
                    parent,
                    R.layout.item_search
            )

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            item = collection[position]

            holder.itemView.setOnClickListener { clickListener.invoke(collection[position]) }

            executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class Holder(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup, layout: Int): Holder {
                val inflater = LayoutInflater.from(parent.context)
                val binding =
                        DataBindingUtil.inflate<ItemSearchBinding>(
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
