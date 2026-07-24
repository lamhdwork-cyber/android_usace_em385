package com.navfac.usace.safety.features.sectionandappendices.details.newdetails

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.extension.reduceDragSensitivity
import com.navfac.usace.safety.base.platform.BaseActivity
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.RandomCodeGen
import com.navfac.usace.safety.base.utils.readHtmlFile
import com.navfac.usace.safety.base.utils.share
import com.navfac.usace.safety.databinding.ActivitySectionAppendicesDetailsBinding
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.sectionandappendices.sections.SectionHelper
import com.navfac.usace.safety.features.sectionandappendices.sections.adapter.SectionsViewPagerAdapter
import com.navfac.usace.safety.features.webview.AdjustTextDialog
import com.navfac.usace.safety.features.webview.AdjustTextListener
import io.realm.kotlin.ext.query
import java.lang.reflect.Method


class SectionAppendicesDetailsActivity : BaseActivity<ActivitySectionAppendicesDetailsBinding>(),
    AdjustTextListener {
    companion object {
        const val EXTRA_TYPE = "e_type"
        const val EXTRA_TITLE = "e_title"
        const val EXTRA_CONTENT_PAGE = "e_content_page"
        const val EXTRA_TOOLBAR_TITLE = "e_toolbar_title"
        const val EXTRA_CHILD_POSITION = "e_child_position"
        const val EXTRA_GROUP_POSITION = "e_group_position"
    }

    override val layoutRes: Int
        get() = R.layout.activity_section_appendices_details

    override fun getViewModel(): BaseViewModel? = null
    private lateinit var sectionAdapter: SectionsViewPagerAdapter

    var shareTitle = ""
    var childPos = "0"
    var groupPos = "0"

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreated(instance: Bundle?) {
        initList()
        initSearch()
        showBookmarkState()
        onClick()
    }

    override fun processIntentData(data: Uri) {}

    private fun initList() {
        shareTitle = intent.getStringExtra(EXTRA_TITLE) ?: "" //Section
        childPos = intent.getStringExtra(EXTRA_CHILD_POSITION) ?: "0"
        groupPos = intent.getStringExtra(EXTRA_GROUP_POSITION) ?: "0"

        val sectionList = SectionHelper.loadSectionsFile(this, groupPos, childPos)
        sectionList.forEach { page ->
            page.bookmarked = isInBookmark(arrayListOf(page))
        }
        sectionAdapter = SectionsViewPagerAdapter(this, sectionList)
    }

    private fun initSearch() {
        val query = intent.getStringExtra("searchdata") ?: ""
        if (query != "") {
            sectionAdapter.actionListener = { webView ->
                try {
                    val m: Method = WebView::class.java.getMethod(
                        "findAllAsync",
                        *arrayOf(String::class.java)
                    )
                    m.isAccessible = true
                    m.invoke(webView, query)
                } catch (notIgnored: Throwable) {
                    webView.findAll(query)
                    try {
                        val m = WebView::class.java.getMethod("setFindIsUp", java.lang.Boolean.TYPE)
                        m.isAccessible = true
                        m.invoke(webView, true)
                    } catch (ignored: Throwable) {
                    }
                }
            }
        }
    }

    private fun onClick() {
        binding.sectionsVpView.apply {
            adapter = sectionAdapter
            reduceDragSensitivity()
            registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        showBookmarkState()
                    }
                }
            )
            postDelayed({
                val contentPage = intent.getIntExtra(EXTRA_CONTENT_PAGE, 0)
                if (contentPage > 0)
                    binding.sectionsVpView.setCurrentItem(contentPage, false)
            }, 100L)
        }

        val title = intent.getStringExtra(EXTRA_TOOLBAR_TITLE) ?: ""
        binding.include533.apply {
            toolbartitle.text = title
            toolbarBackicon.setOnClickListener { onBackPressed() }
            toolbarFontsettings.setOnClickListener {
                showSettings()
            }
            toolbarBookmark.setOnClickListener {
                switchBookmark()
            }
            toolbarShare.setOnClickListener {
                onShare()
            }
        }
    }

    private fun showSettings() {
        val dlg = AdjustTextDialog(this)
        dlg.show(supportFragmentManager, null)
    }

    override fun onTextSizeChanged(size: Int) {
        sectionAdapter.pageList.map { page -> page.textSize = size }
        sectionAdapter.notifyDataSetChanged()
    }

    override fun onAlignmentChanged(align: String) {
        sectionAdapter.pageList.map { page -> page.textAlignment = align }
        sectionAdapter.notifyDataSetChanged()
    }

    private fun switchBookmark() {
        val pos = binding.sectionsVpView.currentItem
        if ((pos < 0) || (pos >= sectionAdapter.pageList.size))
            return
        val item = sectionAdapter.pageList[pos]
        val url = item.getUrl()

        val bookmark =
            realmBookmark.query<BookmarkModel>("datafile == $0", url)
                .first()
                .find()
        if (bookmark != null) {
            realmBookmark.writeBlocking {
                findLatest(bookmark)?.let { delete(it) }
            }
            item.bookmarked = false
        } else {
            realmBookmark.writeBlocking {
                copyToRealm(
                    BookmarkModel().apply {
                        id = RandomCodeGen.generate()
                        name = shareTitle
                        type = "Sections"
                        description = ""
                        position = childPos.toInt()
                        groupPosition = groupPos.toInt()
                        isImage = false
                        isArticle = true
                        datafile = url
                    }
                )
            }
            Toast.makeText(this, "Added To Bookmark", Toast.LENGTH_LONG).show()
            item.bookmarked = true
        }
        showBookmarkState()
    }

    private fun showBookmarkState() {
        val pos = binding.sectionsVpView.currentItem
        updateBookmark(
            if (sectionAdapter.pageList[pos].bookmarked) R.drawable.ic_bookmark_red_asset
            else R.drawable.ic_bookmark_gray_asset, binding.include533.toolbarBookmark
        )
    }

    private fun onShare() {
        val fileName = sectionAdapter.pageList[binding.sectionsVpView.currentItem].fileName
        readHtmlFile("sections/", "$fileName", this)?.let { content ->
            if (shareTitle.isEmpty())

                share(this, shareTitle, content)
        }
    }
}
