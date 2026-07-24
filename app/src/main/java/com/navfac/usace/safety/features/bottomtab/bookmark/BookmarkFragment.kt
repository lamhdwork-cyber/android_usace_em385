package com.navfac.usace.safety.features.bottomtab.bookmark

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.navfac.usace.safety.base.extension.showMessageDialogWithAction
import com.navfac.usace.safety.base.extension.showToast
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.Constants
import com.navfac.usace.safety.databinding.FragmentBookmarkBinding
import com.navfac.usace.safety.features.bottomtab.bookmark.adapter.BookmarkAdapter
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.htmlpage.HtmlPageActivity
import com.navfac.usace.safety.features.safetyvideos.imagedetails.ImageDetailsActivity
import io.realm.kotlin.ext.query
import java.io.File


class BookmarkFragment : BaseFragment<FragmentBookmarkBinding>() {

    override val layoutRes: Int
        get() = R.layout.fragment_bookmark

    override fun getViewModel(): BaseViewModel? = null

    lateinit var adapter: BookmarkAdapter
    lateinit var bookmarkitemlist: List<BookmarkModel>
    lateinit var mDownloadManager: DownloadManager

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreated(savedInstance: Bundle?) {
        /*binding.include4.bookmarktitletoolbar.text = "Bookmarks"
        binding.include4.bookmarktoolbarback.setOnClickListener {
            backPressedAction()
        }

        //Clear Bookmark All list items
        binding.include4.deleteallbook.setOnClickListener {
            val realmResults: RealmResults<BookmarkModel> = realm.where(BookmarkModel::class.java).findAll()
            if (!realmResults.isEmpty()) {
                viewModel.clearBookmarkAllItem(requireContext()) {
                    checkBookmarkExistOrNot()
                    adapter.notifyDataSetChanged()
                }
            }
        }*/
        mDownloadManager = DownloadService.getDownloadManager(getMainActivity())
        bookmarkitemlist = ArrayList()
        bookmarkitemlist = realmBookmark.query<BookmarkModel>().find()
        checkBookmarkExistOrNot()

        binding.recyclerBookmark.setHasFixedSize(true)
        binding.recyclerBookmark.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBookmark.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        adapter = BookmarkAdapter(bookmarkitemlist)
        binding.recyclerBookmark.adapter = adapter
        adapter.notifyDataSetChanged()

        // Delete Single item from bookmark list callback
        adapter.actionListener = {
            showMessageDialogWithAction(
                requireContext(), "Are you sure you want to remove?",
                "Yes", "Not Now"
            ) {
                realmBookmark.writeBlocking {
                    delete(query<BookmarkModel>("datafile == $0", it.datafile).find())
                }
                bookmarkitemlist = realmBookmark.query<BookmarkModel>().find()
                adapter.updateData(bookmarkitemlist)

                showMessage("Bookmark Removed!")
                checkBookmarkExistOrNot()
            }
        }

        // Send Bookmark data to Details
        adapter.actionListenerfordetails = {
            if (it.isImage!!) {
                startActivity(
                    Intent(requireContext(), ImageDetailsActivity::class.java)
                        .putExtra(ImageDetailsActivity.EXTRA_IMAGE, it.datafile)
                        .putExtra(ImageDetailsActivity.EXTRA_TERMS, it.name)
                )
            }
            if (it.isArticle!!) {
                if (it.type.equals("Appendices")) {
                    //startActivity(Intent(requireContext(), AppendicesDetailsActivity::class.java)
                    //  .putExtra(AppendicesDetailsActivity.EXTRA_CHILD_POSITION, it.position.toString()))
                    activity?.goToActivity(
                        HtmlPageActivity::class.java, false,
                        bundleOf(
                            Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.APPENDICES),
                            Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, it.position)
                        )
                    )
                } else if (it.type.equals("Sections")) {
                    //startActivity(Intent(requireContext(), SectionAppendicesDetailsActivity::class.java)
                    //  .putExtra(SectionAppendicesDetailsActivity.EXTRA_CHILD_POSITION, it.position.toString())
                    //  .putExtra(SectionAppendicesDetailsActivity.EXTRA_GROUP_POSITION, it.groupPosition.toString()))
                    activity?.goToActivity(
                        HtmlPageActivity::class.java, false,
                        bundleOf(
                            Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.SECTIONS),
                            Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, it.position)
                        )
                    )
                } else if (it.type.equals("Sections New")) {
                    val page = it.position
                    val chapterID = if (it.name!!.isNotEmpty()) it.datafile!!.substringBefore("-")
                        .toInt() else 1
                    activity?.goToActivity(
                        HtmlPageActivity::class.java, false,
                        bundleOf(
                            Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.SECTIONS_NEW),
                            Pair(HtmlPageActivity.EXTRA_CHAPTER_ID, chapterID),
                            Pair(HtmlPageActivity.EXTRA_CHAPTER_KEY, it.name),
                            Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, page),
                            Pair(
                                HtmlPageActivity.EXTRA_GO_TO_PAGE_CRAWL,
                                it.datafile
                            )
                        )
                    )
                } else if (it.type.equals("Chapter")) {
                    activity?.goToActivity(
                        HtmlPageActivity::class.java, false,
                        bundleOf(
                            Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.CHAPTERS),
                            Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, 0),
                            Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE_CRAWL, "purpose_of_manual.html")
                        )
                    )
                }
            } else if (it.type.equals("Resources")) {
                downloadFile("https://www.publications.usace.army.mil/Portals/76/Publications/EngineerManuals/EM_385-1-1.pdf?ver=7Cpck_22Ct_-w6QSGyOKVQ%3d%3d")
            }
        }
    }

    // Check Bookmark list empty or not
    private fun checkBookmarkExistOrNot() {
        if (bookmarkitemlist.isEmpty()) {
            binding.tvNobookmark.visibility = View.VISIBLE
        } else {
            binding.tvNobookmark.visibility = View.GONE
        }
    }

    override fun backPressedAction() {
        findNavController().navigateUp()
    }

    override fun setupActions() {

    }

    private fun downloadFile(url: String) {
        // fileName -> fileName with extension
        showLoading(true)
        val file: File = File(App.downloadDir.path, Constants.RESOURCE_FILE_DEFAULT)


        if (file.exists() && file.isValidPdf()) {
            showLoading(false)
            activity?.goToActivity(
                HtmlPageActivity::class.java, false,
                bundleOf(
                    Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.RESOURCES),
                    Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, 0)
                )
            )
            return
        }

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
                    activity?.goToActivity(
                        HtmlPageActivity::class.java, false,
                        bundleOf(
                            Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.RESOURCES),
                            Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, 0)
                        )
                    )
                } else {
                    file.delete()
                    showToast(getMainActivity(), getString(R.string.download_file_fail))
                }
            }

            override fun onDownloadFailed(e: DownloadException?) {
                showLoading(false)
                file.delete()
                showToast(getMainActivity(), getString(R.string.download_file_fail))
            }
        }
        mDownloadManager.download(downloadInfo)
    }

}