package com.navfac.usace.safety.features.webview

import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.navfac.usace.safety.base.extension.isNightMode


class HighlightWebClient : WebViewClient() {
    var downloadListener : ((String)->Unit) ?= null
    override fun onPageStarted(view: WebView?, url: String?, favIcon: Bitmap?) {
        /*val code  = "(function() {\nvar script=document.createElement('script');\n" +
                    "script.type='text/javascript';script.src='file://android_asset/hg.js';\n" +
                    "document.getElementsByTagName('head').item(0).appendChild(script);\n})()"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view?.evaluateJavascript(code, null)
        } else {
            view?.loadUrl("javascript: $code")
        }*/
        super.onPageStarted(view, url, favIcon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (view == null)
            return
        //Init Highlighter in JS
        url?.let {
            if (view is HighlightWebView) {
                if (it.contains("file://") && !it.endsWith(".js") && !it.endsWith(".css")) { //only html loading
                    //class name for highlight
                    val name = if (isNightMode(view.context!!)) "hl_dark" else "hl"
                    //load pre-selected highlights information
                    val text = view.loadHighlights()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        view.evaluateJavascript("initHighlighter('$name', '$text');", null)
                    } else {
                        view.loadUrl("javascript: initHighlighter('$name', '$text');")
                    }
                }
            }
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request?.url?.toString()?.contains(".pdf") == true) {
            downloadListener?.invoke(request.url.toString())
            return true
        }
        return super.shouldOverrideUrlLoading(view, request)
    }
}