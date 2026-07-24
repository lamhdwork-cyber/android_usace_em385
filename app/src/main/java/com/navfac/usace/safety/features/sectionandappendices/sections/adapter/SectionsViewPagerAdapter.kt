package com.navfac.usace.safety.features.sectionandappendices.sections.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.databinding.ItemHtmlPageBinding
import com.navfac.usace.safety.features.htmlpage.HtmlPage
import com.navfac.usace.safety.features.webview.HighlightWebClient
import java.util.*


class SectionsViewPagerAdapter(private val context: Context, val pageList: ArrayList<HtmlPage>) :
                        RecyclerView.Adapter<SectionsViewPagerAdapter.Holder>() { //PagerAdapter()
    internal var actionListener: (webView: WebView) -> Unit = { _ -> }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
                                    Holder.from(parent, R.layout.item_html_page)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            actionListener.invoke(webview)
            item = pageList[position]
            webview.webViewClient = HighlightWebClient()
            executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        return pageList.size
    }


    class Holder(val binding: ItemHtmlPageBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup, layout: Int): Holder {
                val inflater = LayoutInflater.from(parent.context)
                val binding  = DataBindingUtil.inflate<ItemHtmlPageBinding>(
                                inflater, layout, parent, false)
                return Holder(binding)
            }
        }
    }
}