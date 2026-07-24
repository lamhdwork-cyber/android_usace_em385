package com.navfac.usace.safety.features.sectionandappendices.appendices.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.pojo.AppendicesHtml
import com.navfac.usace.safety.base.db.AutoUpdatableAdapter
import com.navfac.usace.safety.databinding.ItemAppendicesDetailBinding
import com.navfac.usace.safety.features.sectionandappendices.appendices.details.AppendicesDetailsActivity
import javax.inject.Inject
import kotlin.properties.Delegates

class AppendicesDetailsAdapter @Inject constructor() :
        RecyclerView.Adapter<AppendicesDetailsAdapter.Holder>(), AutoUpdatableAdapter {

    internal var collection: List<AppendicesHtml> by Delegates.observable(emptyList()) { _, old, new -> //prop
        autoNotify(old, new) { o, n -> o == n }
    }

    internal var onZoom: WebView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder.from(parent,R.layout.item_appendices_detail)

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            holder.binding.webview
            item = collection[position]
            onZoom = holder.binding.webview
            executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    class Holder(val binding: ItemAppendicesDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup, layout: Int): Holder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ItemAppendicesDetailBinding>(inflater,
                                                            layout, parent, false)
                return Holder(binding)
            }
        }
    }
}
