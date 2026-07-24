package com.navfac.usace.safety.features.htmlpage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.db.AutoUpdatableAdapter
import com.navfac.usace.safety.base.extension.isValidPdf
import com.navfac.usace.safety.databinding.ItemHtmlPageBinding
import com.navfac.usace.safety.features.webview.HighlightWebClient
import java.io.File
import javax.inject.Inject
import kotlin.properties.Delegates

class HtmlPageAdapter @Inject constructor() : RecyclerView.Adapter<HtmlPageAdapter.Holder>(),
    AutoUpdatableAdapter {
    internal var collection: List<HtmlPage> by Delegates.observable(emptyList()) { _, old, new -> //prop
        autoNotify(old, new) { o, n -> o == n }
    }
    internal var actionListener: (webView: WebView) -> Unit = { _ -> }
    var downloadListener: ((String) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder.from(parent, R.layout.item_html_page)

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            actionListener.invoke(webview)
            item = collection[position]
            val webClient = HighlightWebClient()
            webClient.downloadListener = downloadListener
            webview.webViewClient = webClient
            if (collection[position].isLink) {
                webview.visibility = View.GONE
                webviewUrl.visibility = View.VISIBLE
                pdfView.visibility = View.GONE
            } else if (collection[position].isPdf) {
                webview.visibility = View.GONE
                webviewUrl.visibility = View.GONE

                val pdfFile = File(collection[position].filePath)
                if (pdfFile.isValidPdf()) {
                    pdfView.visibility = View.VISIBLE
                    try {
                        pdfView.initWithFile(pdfFile)
                    } catch (e: Exception) {
                        pdfFile.delete()
                        pdfView.visibility = View.GONE
                    }
                } else {
                    pdfView.visibility = View.GONE
                    pdfFile.delete()
                }
            } else {
                webview.visibility = View.VISIBLE
                webviewUrl.visibility = View.GONE
                pdfView.visibility = View.GONE
            }
            executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    class Holder(val binding: ItemHtmlPageBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup, layout: Int): Holder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ItemHtmlPageBinding>(
                    inflater, layout, parent, false
                )
                return Holder(binding)
            }
        }
    }
}
