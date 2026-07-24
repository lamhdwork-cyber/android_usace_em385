package com.navfac.usace.safety.base.utils

import android.annotation.SuppressLint
import android.util.Base64
import android.webkit.WebView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.navfac.usace.safety.base.data.pojo.AppendicesHtml
import com.navfac.usace.safety.base.data.pojo.SectionsHtml
import com.navfac.usace.safety.features.htmlpage.HtmlPage
import android.content.Intent
import android.net.Uri
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.widget.Toast
import android.content.ActivityNotFoundException
import android.content.Context
import android.app.AlertDialog


@BindingAdapter("android:text")
fun setText(view: TextView, value: String?) {
    view.text = value
}

@BindingAdapter("loadHtmlText")
fun setHtmlText(view: WebView, value: String?) {
    value?.let {
        val encodedValue =
                Base64.encodeToString(it.toByteArray(), Base64.NO_PADDING)
        view.loadData(encodedValue, "text/html", "base64")
    }
}

// Function to display an alert message
fun showAlert(context: Context?, title: String, message: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton("OK", null)
    builder.show()
}

@SuppressLint("SetJavaScriptEnabled")
@BindingAdapter("loadHtmlUrl")
fun setHtmlUrl(view: WebView, value: String?) {
    value?.let {
        view.settings.javaScriptEnabled = true
        view.settings.serifFontFamily
        view.settings.builtInZoomControls = true

        view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url?.endsWith(".pdf") == true) {

                    showAlert(view?.context, "Opening PDF", "This link is a PDF. It will be opened in an external viewer.")

                    // Open PDF with external viewer
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(url), "application/pdf")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    try {
                        view?.context?.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        // Handle case where no PDF viewer is available
                        Toast.makeText(view?.context, "No PDF viewer installed", Toast.LENGTH_SHORT).show()
                    }
                    return true
                }

                showAlert(view?.context, "Opening Regular Link", "This link is a PDF. It will be opened in an external viewer.")

                return false
            }
        }

        view.loadUrl(value)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@BindingAdapter("loadHtmlTextWithImage")
fun setHtmlTextWithImage(view: WebView, value: String?) {
    value?.let {

        view.settings.javaScriptEnabled = true
        view.settings.serifFontFamily
        view.settings.builtInZoomControls = true
        view.loadDataWithBaseURL(null,
                "<style>img{display: inline;height: auto;max-width: 100%;}</style>$value",
                "text/html",
                "UTF-8",
                null)
    }
}

private fun getBody(rawBody: String, textAlign: String, textSize: Int, backColor: String, textColor: String) : String {
    val styleTag  = "<style> img { display: inline;height: auto;max-width: 100%; } body, h1, h2 { " +
                    "text-align:${textAlign}; font-size: ${textSize}; color: ${textColor};" +
                    " background-color: ${backColor}}</style>"
    val scriptTag = "<script src=\"../hg.js\" type=\"text/javascript\"></script>"

    val pos1 = rawBody.indexOf("<HEAD>", 0, true)
    val pos2 = rawBody.indexOf("</HEAD>", 0, true)
    var headTag = ""
    var bodyTag = ""
    if ((pos1 >= 0) && (pos2 >= 0)) {
        headTag = "${rawBody.substring(pos1 + 6, pos2)}\n$styleTag\n$scriptTag"
        bodyTag = rawBody.substring(pos2 + 7)
        if (pos1 > 0)
            bodyTag = rawBody.substring(0, pos1) + bodyTag
    } else {
        val pos3 = rawBody.indexOf("<HEAD/>", 0, true)
        bodyTag = if (pos3 < 0) rawBody else rawBody.substring(pos3 + 7)
        headTag = "$styleTag\n$scriptTag"
    }
    return "<html><head>$headTag</head>\n<body>$bodyTag</body></html>"
}


@SuppressLint("SetJavaScriptEnabled")
@BindingAdapter("loadHtmlTextAndImage")
fun setHtmlTextAndImage(wv: WebView, data: AppendicesHtml) {
    data.let {
        wv.tag = data
        wv.settings.javaScriptEnabled = true
        wv.isScrollContainer = true
        //wv.bringToFront()
        //wv.isScrollbarFadingEnabled     = true
        //wv.isVerticalScrollBarEnabled   = true
        //wv.isHorizontalScrollBarEnabled = false
        //wv.settings.builtInZoomControls = true
        //wv.settings.useWideViewPort     = true
        //webView.settings.serifFontFamily
        wv.loadDataWithBaseURL(data.filePath, getBody(data.body, data.textAlignment,
                                        data.textSize, data.backColor, data.textColor),
                                    "text/html", "UTF-8",null)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@BindingAdapter("loadHtmlTextAndImageSections")
fun setHtmlTextAndImageSections(wv: WebView, data: SectionsHtml) {
    data.let {
        wv.tag = data
        wv.settings.javaScriptEnabled = true
        wv.isScrollContainer = true
        wv.settings.setSupportZoom(true)
        //wv.bringToFront()
        //wv.isScrollbarFadingEnabled     = true
        //wv.isVerticalScrollBarEnabled   = true
        //wv.isHorizontalScrollBarEnabled = false
        wv.settings.builtInZoomControls = true
        //wv.settings.useWideViewPort     = true
        //webView.settings.serifFontFamily
        wv.loadDataWithBaseURL(data.filePath, getBody(data.body, data.textAlignment,
                                        data.textSize, data.backColor, data.textColor),
                                    "text/html", "UTF-8",null)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@BindingAdapter("loadHtmlTextAndImage")
fun setHtmlTextAndImage(wv: WebView, data: HtmlPage) {
    data.let {
        wv.tag = data
        wv.settings.javaScriptEnabled = true
        wv.isScrollContainer = true
        wv.settings.setSupportZoom(true)
        wv.settings.builtInZoomControls = true
        //wv.bringToFront()
        //wv.isScrollbarFadingEnabled     = true
        //wv.isVerticalScrollBarEnabled   = true
        //wv.isHorizontalScrollBarEnabled = false
        //wv.settings.builtInZoomControls = true
        //wv.settings.useWideViewPort     = true
        //webView.settings.serifFontFamily
        wv.loadDataWithBaseURL(data.filePath, getBody(data.body, data.textAlignment,
                                        data.textSize, data.backColor, data.textColor),
                                    "text/html", "UTF-8",null)
    }
}
