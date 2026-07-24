package com.navfac.usace.safety.features.webview

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.webkit.WebView
import androidx.annotation.RequiresApi
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.data.pojo.AppendicesHtml
import com.navfac.usace.safety.base.data.pojo.SectionsHtml
import com.navfac.usace.safety.features.htmlpage.HtmlPage


class HighlightWebView : WebView {
    constructor(context: Context) : super(context) {
        mDetector = GestureDetector(context, CustomGestureListener())
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mDetector = GestureDetector(context, CustomGestureListener())
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
                                        super(context, attrs, defStyleAttr) {
        mDetector = GestureDetector(context, CustomGestureListener())
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
                                                super(context, attrs, defStyleAttr, defStyleRes) {
        mDetector = GestureDetector(context, CustomGestureListener())
    }

    private fun getKeyName(): String {
        var key = ""
        tag?.let { obj ->
            when (obj) {
                is HtmlPage -> {
                    obj.fileName?.let { name ->
                        key = name.replace(".html", "")
                    }
                }
                is AppendicesHtml -> {
                    key = Uri.parse(obj.filePath).lastPathSegment ?: ""
                    key = key.replace(".html", "")
                }
                is SectionsHtml -> {
                    key = Uri.parse(obj.filePath).lastPathSegment ?: ""
                    key = key.replace(".html", "")
                }
                else -> { key = "" }
            }
        }
        return key
    }

    fun loadHighlights() : String {
        tag?.let {
            val key = getKeyName()
            if (key.isEmpty())
                return ""
            val pref: SharedPreferences = context.getSharedPreferences(
                        "com.navfac.usace.safety", Context.MODE_PRIVATE)
            val value = pref.getString("highlights_$key", "") ?: ""
            return value.replace("\"", "").replace("'", "")
        }
        return ""
    }

    fun saveHighlights(text : String) {
        val value = text.replace("\"", "").replace("'", "")
        if (value.isEmpty())
            return
        tag?.let {
            val key = getKeyName()
            if (key.isEmpty())
                return
            val pref: SharedPreferences.Editor = context.getSharedPreferences(
                                "com.navfac.usace.safety", Context.MODE_PRIVATE).edit()
            pref.putString("highlights_$key", value).apply()
        }
    }

    fun highlightSelection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("myHighlightSelection();") { res ->
                res?.let { data ->
                    saveHighlights(data)
                } ?: run {
                    saveHighlights("")
                }
            }
        } else {
            loadUrl("javascript: myHighlightSelection();")
        }
    }

    fun removeHighlights() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("myRemoveHighlights();") { res ->
                res?.let { data ->
                    saveHighlights(data)
                } ?: run {
                    saveHighlights("")
                }
            }
        } else {
            loadUrl("javascript: myRemoveHighlights();")
        }
    }


    inner class CustomGestureListener : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (mActionMode != null) {
                mActionMode!!.finish()
                return true
            }
            return false
        }
    }
    var mDetector: GestureDetector? = null

    private var mActionMode: ActionMode? = null
    private var mCustomCallback: ActionMode.Callback? = null
    // Add this class variable
    private var mSelectCallback: ActionMode.Callback? = null

    override fun startActionMode(callback: ActionMode.Callback?): ActionMode? {
        if (parent != null) {
            return parent.startActionModeForChild(this, wrapCallback(callback))
        }
        return null
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun startActionMode(callback: ActionMode.Callback?, type: Int): ActionMode? {
        if (callback != null) {
            val name: String = callback::class.java.toString()
            if (name.contains("SelectActionModeCallback")) {
                mSelectCallback = callback
            }
        }
        if (mCustomCallback == null)
            mCustomCallback = CustomActionModeCallback()
        // We haven't actually done anything yet. Send our custom callback
        // to the superclass so it will be shown on screen.
        return if (parent != null) {
            parent.startActionModeForChild(this, wrapCallback(callback), type)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   } else
            null //super.startActionMode(mCustomCallback, type)
        //return super.startActionMode(callback, type)
    }

    private inner class CustomActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mActionMode = mode
            mode?.menuInflater?.inflate(R.menu.highlight_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.findItem(R.id.highlight)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            menu?.findItem(R.id.unhighlight)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.highlight -> {
                    highlightSelection()
                    mode?.finish()
                    return true
                }
                R.id.unhighlight -> {
                    removeHighlights()
                    mode?.finish()
                    return true
                }
            }

            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            //clearFocus() // Remove the selection highlight and handles.

            // Semi-hack in order to clear the selection
            // when running Android earlier than KitKat.
            mSelectCallback?.let{
                it.onDestroyActionMode(mode)
            }
            // Relevant to part 2.
            mActionMode = null
        }
    }

    private fun wrapCallback(callback: ActionMode.Callback?): ActionMode.Callback? {
        return if (mCustomCallback != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CallbackWrapperM(mCustomCallback, callback)
            } else {
                CallbackWrapperBase(mCustomCallback, callback)
            }
        } else callback
    }


    private class CallbackWrapperBase(
        private val mWrappedCustomCallback: ActionMode.Callback?,
        private val mWrappedSystemCallback: ActionMode.Callback?
    ) :
        ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return (mWrappedCustomCallback?.onCreateActionMode(mode, menu) == true)
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return (mWrappedCustomCallback?.onPrepareActionMode(mode, menu) == true)
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return (mWrappedCustomCallback?.onActionItemClicked(mode, item) == true)
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            try {
                mWrappedCustomCallback?.onDestroyActionMode(mode)
                mWrappedSystemCallback?.onDestroyActionMode(mode)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private class CallbackWrapperM(
        private val mWrappedCustomCallback: ActionMode.Callback?,
        private val mWrappedSystemCallback: ActionMode.Callback?
    ) :
        ActionMode.Callback2() {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return (mWrappedCustomCallback?.onCreateActionMode(mode, menu) == true)
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return (mWrappedCustomCallback?.onPrepareActionMode(mode, menu) == true)
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return (mWrappedCustomCallback?.onActionItemClicked(mode, item) == true)
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            mWrappedCustomCallback?.onDestroyActionMode(mode)
            mWrappedSystemCallback?.onDestroyActionMode(mode)
        }

        override fun onGetContentRect(mode: ActionMode?, view: View?, outRect: Rect?) {
            when {
                mWrappedCustomCallback is ActionMode.Callback2 -> {
                    mWrappedCustomCallback.onGetContentRect(mode, view, outRect)
                }
                mWrappedSystemCallback is ActionMode.Callback2 -> {
                    mWrappedSystemCallback.onGetContentRect(mode, view, outRect)
                }
                else -> {
                    super.onGetContentRect(mode, view, outRect)
                }
            }
        }
    }
}