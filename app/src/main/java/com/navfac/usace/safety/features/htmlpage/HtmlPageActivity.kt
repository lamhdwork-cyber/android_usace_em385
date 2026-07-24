package com.navfac.usace.safety.features.htmlpage

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.ixuea.android.downloader.DownloadService
import com.ixuea.android.downloader.callback.DownloadListener
import com.ixuea.android.downloader.callback.DownloadManager
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.android.downloader.exception.DownloadException
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.application.App
import com.navfac.usace.safety.base.data.definition.HtmlType
import com.navfac.usace.safety.base.extension.goToActivity
import com.navfac.usace.safety.base.extension.isValidPdf
import com.navfac.usace.safety.base.extension.observe
import com.navfac.usace.safety.base.extension.reduceDragSensitivity
import com.navfac.usace.safety.base.extension.showToast
import com.navfac.usace.safety.base.extension.tryCatch
import com.navfac.usace.safety.base.platform.BaseActivity
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.Constants
import com.navfac.usace.safety.base.utils.readHtmlFile
import com.navfac.usace.safety.base.utils.share
import com.navfac.usace.safety.databinding.ActivityHtmlPageBinding
import com.navfac.usace.safety.features.webview.AdjustTextDialog
import com.navfac.usace.safety.features.webview.AdjustTextListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.lang.reflect.Method
import javax.inject.Inject


@AndroidEntryPoint
class HtmlPageActivity : BaseActivity<ActivityHtmlPageBinding>(),
    HtmlPageHandler, AdjustTextListener {
    companion object {
        const val EXTRA_TYPE = "e_type"
        const val EXTRA_GO_TO_PAGE = "_e_go_to_page"
        const val EXTRA_GO_TO_PAGE_CRAWL = "_e_go_to_page_crawl"
        const val EXTRA_CHAPTER_ID = "_e_chapter_id"
        const val EXTRA_CHAPTER_KEY = "_e_chapter_key"
    }

    private lateinit var viewModel: HtmlPageViewModel

    @Inject
    lateinit var adapterPages: HtmlPageAdapter

    override val layoutRes: Int
        get() = R.layout.activity_html_page

    private var textControlDialog: AdjustTextDialog? = null
    private lateinit var mDownloadManager: DownloadManager

    override fun onShare() {
        val currentPage = adapterPages.collection[binding.htmlPageVpContents.currentItem]
        val type =
            if (viewModel.htmlType.value == HtmlType.SECTIONS) "sections" else if (viewModel.htmlType.value == HtmlType.APPENDICES) "appendices" else if (viewModel.htmlType.value == HtmlType.SECTIONS_NEW) "content/sections_new" else "content/chapter"
        var name = currentPage.fileName ?: ""
        tryCatch({
            name = name.substring(0, name.lastIndexOf("."))
        }, {
            name = ""
        })
        readHtmlFile("$type/", currentPage.fileName ?: "", this)?.let { txt ->
            share(this, name, txt)
        }
//        shareFileFromAsset(this, currentPage.fileName ?: "", "$type/", Intent.ACTION_SEND)
    }

    override fun onBookMark() {
        val curPos = binding.htmlPageVpContents.currentItem
        val curPage = adapterPages.collection[curPos]
        var name = curPage.fileName ?: ""
        tryCatch({
            name = name.substring(0, name.lastIndexOf("."))
        }, {
            name = ""
        })
        val chapter =
            if (viewModel.chapterKey.value.isNullOrEmpty()) "Introduction" else curPage.chapter
        if (viewModel.addToBookmark(chapter, curPos, curPage.fileName ?: "", name)) {
            curPage.bookmarked = true
            adapterPages.notifyItemChanged(curPos)
            viewModel.setCurrentPageBookmarked(true)
        }
    }

    override fun onFontSettings() {
        if (textControlDialog == null)
            textControlDialog = AdjustTextDialog(this)
        if (textControlDialog?.dialog?.isShowing == true)
            textControlDialog?.dialog?.dismiss()
        textControlDialog?.show(supportFragmentManager, null)
    }

    override fun onTextSizeChanged(size: Int) {
        viewModel.setFontSize(size)
    }

    override fun onAlignmentChanged(align: String) {
        viewModel.setTextAlignment(align)
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreated(instance: Bundle?) {
        viewModel = ViewModelProvider(this)[HtmlPageViewModel::class.java]
        setSupportActionBar(binding.toolbar)
        setToolbar(show = true)
        mDownloadManager = DownloadService.getDownloadManager(this)
        initViews()
        initObserver()
        checkExtras()
    }

    private fun checkExtras() {
        intent.extras?.apply {
            if (containsKey(EXTRA_CHAPTER_KEY))
                viewModel.setChapterKey(getString(EXTRA_CHAPTER_KEY, ""))
            if (containsKey(EXTRA_CHAPTER_ID))
                viewModel.setChapterID(getInt(EXTRA_CHAPTER_ID))
            if (containsKey(EXTRA_TYPE)) {
                viewModel.setHtmlType(getString(EXTRA_TYPE, ""))
                if (getString(EXTRA_TYPE, "") == HtmlType.RESOURCES || getString(
                        EXTRA_TYPE,
                        ""
                    ) == HtmlType.PDF
                ) {
                    binding.tvToolbarFontSettings.visibility = View.GONE
                    binding.ivToolbarBookmark.visibility = View.GONE
                    binding.ivToolbarShare.visibility = View.GONE
                }
            } else {
                finish()
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            observe(htmlPages) {
                it?.let { list ->
                    adapterPages.collection = list
                    intent.extras?.apply {
                        when {
                            containsKey(EXTRA_GO_TO_PAGE_CRAWL) -> {
                                viewModel.setGoToCrawlPage(getString(EXTRA_GO_TO_PAGE_CRAWL)!!)
                            }

                            containsKey(EXTRA_GO_TO_PAGE) -> {
                                viewModel.setGoToPage(getInt(EXTRA_GO_TO_PAGE))
                            }

                            else -> viewModel.setGoToPage(0)
                        }
                    }
                }
            }
            observe(textSize) {
                it?.let { size ->
                    adapterPages.collection.map { htmlPage -> htmlPage.textSize = size }
                    adapterPages.notifyDataSetChanged()
                }
            }
            observe(textAlignment) {
                it?.let { alignment ->
                    adapterPages.collection.map { htmlPage -> htmlPage.textAlignment = alignment }
                    adapterPages.notifyDataSetChanged()
                }
            }
            observe(goToPageCrawl) {
                it?.let { name ->
                    val pageName = name.substringBefore("_")
                    val index = adapterPages.collection.indexOfFirst { goTo ->
                        goTo.fileName?.substringBefore("_") == pageName
                    }
                    binding.htmlPageVpContents.setCurrentItem(index, true)
                    //viewModel.setCurrentPageBookmarked(adapterPages.collection[index].bookmarked)
                }
            }
            observe(goToPage) {
                it?.let { index ->
                    binding.htmlPageVpContents.setCurrentItem(index, true)
                }
            }
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.handler = this
    }

    private fun initViews() {
        binding.htmlPageVpContents.apply {
            adapterPages.downloadListener = {
                downloadFile(it)
            }
            adapter = adapterPages
            reduceDragSensitivity()
            registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        viewModel.setCurrentPageBookmarked(adapterPages.collection[position].bookmarked)
                    }
                }
            )
            offscreenPageLimit = 3
        }

        // received search word
        val query = intent.getStringExtra("searchdata")
        if (!query.isNullOrEmpty()) {
            adapterPages.actionListener = { webView ->
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
                        val m = WebView::class.java.getMethod(
                            "setFindIsUp",
                            java.lang.Boolean.TYPE
                        )
                        m.isAccessible = true
                        m.invoke(webView, true)
                    } catch (ignored: Throwable) {
                    }
                }
            }
        }
    }

    override fun processIntentData(data: Uri) {}

    private fun downloadFile(url: String) {
        // fileName -> fileName with extension
        showLoading(true)
        val file: File = File(App.downloadDir.path, Constants.FILE_PDF_DEFAULT)
        if (file.exists() && url == App.urlPDFLast && file.isValidPdf()) {
            showLoading(false)
            goToActivity(
                HtmlPageActivity::class.java, false,
                bundleOf(
                    Pair(EXTRA_TYPE, HtmlType.PDF),
                    Pair(EXTRA_GO_TO_PAGE, 0)
                )
            )
            return
        }
        App.urlPDFLast = url
        if (file.exists()) {
            file.delete()
        }

        val downloadInfo = DownloadInfo.Builder().setUrl(url)
            .setPath(file.absolutePath)
            .build()

        downloadInfo!!.downloadListener = object : DownloadListener {
            override fun onStart() {

            }

            override fun onWaited() {
            }

            override fun onPaused() {
            }

            override fun onDownloading(progress: Long, size: Long) {
            }

            override fun onRemoved() {
            }

            override fun onDownloadSuccess() {
                showLoading(false)
                if (file.isValidPdf()) {
                    goToActivity(
                        HtmlPageActivity::class.java, false,
                        bundleOf(
                            Pair(EXTRA_TYPE, HtmlType.PDF),
                            Pair(EXTRA_GO_TO_PAGE, 0)
                        )
                    )
                } else {
                    file.delete()
                    showToast(this@HtmlPageActivity, getString(R.string.download_file_fail))
                }
            }

            override fun onDownloadFailed(e: DownloadException?) {
                showLoading(false)
                file.delete()
                showToast(this@HtmlPageActivity, getString(R.string.download_file_fail))
            }
        }
        mDownloadManager.download(downloadInfo)
    }
}