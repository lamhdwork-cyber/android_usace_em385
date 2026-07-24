package com.navfac.usace.safety.features.resources

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
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
import com.navfac.usace.safety.base.extension.showToast
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.Constants
import com.navfac.usace.safety.base.utils.RandomCodeGen
import com.navfac.usace.safety.databinding.FragmentAcronymsBinding
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.htmlpage.HtmlPageActivity
import com.navfac.usace.safety.features.resources.adapter.ResourceAdapter
import com.navfac.usace.safety.features.resources.model.ResourceModelItem
import io.realm.kotlin.ext.query
import java.io.File

class ResourceFragment : BaseFragment<FragmentAcronymsBinding>() {

    private lateinit var adapter: ResourceAdapter

    //private lateinit var model: AcronymnModel
    private var model = mutableListOf<ResourceModelItem>()
    private lateinit var mDownloadManager: DownloadManager

    override val layoutRes: Int
        get() = R.layout.fragment_acronyms

    override fun getViewModel(): BaseViewModel? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreated(savedInstance: Bundle?) {
        mDownloadManager = DownloadService.getDownloadManager(getMainActivity())
        binding.include2.ntoolbartitle.text = "Resources"
        binding.include2.ntoolbarbacku.setOnClickListener {
            backPressedAction()
        }

        model.add(ResourceModelItem(name = "2014 EM 385-1-1 USACE Safety and Health Requirements Manual"))
        binding.recyclerviewAcronyms.setHasFixedSize(true)
        binding.recyclerviewAcronyms.layoutManager = LinearLayoutManager(requireContext())
        adapter = ResourceAdapter(requireContext(), model, realmBookmark)
        binding.recyclerviewAcronyms.adapter = adapter
        adapter.notifyDataSetChanged()


        /*
       * Action For Menu
       * */

        adapter.clickListener = {
            downloadFile("https://www.publications.usace.army.mil/Portals/76/Publications/EngineerManuals/EM_385-1-1.pdf?ver=7Cpck_22Ct_-w6QSGyOKVQ%3d%3d")
        }
        adapter.actionListener = { it: ResourceModelItem, bookMarkIcon: ImageView ->
            val options = arrayOf<CharSequence>("Add to Bookmark", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")
            builder.setCancelable(false)
            builder.setItems(options) { dialog: DialogInterface, item: Int ->
                if (options[item] == "Add to Bookmark") {
                    val bookmark =
                        realmBookmark.query<BookmarkModel>("name == $0", it.name)
                            .first()
                            .find()
                    if (bookmark != null) {
                        showMessage("Already Added as Bookmark")
                        dialog.dismiss()
                    } else {
                        realmBookmark.writeBlocking {
                            copyToRealm(
                                BookmarkModel().apply {
                                    id = RandomCodeGen.generate()
                                    type = "Resources"
                                    name = it.name
                                    description = ""
                                    isImage = false
                                    isArticle = false
                                }
                            )
                        }
                        showMessage("Added To Bookmark")
                        dialog.dismiss()
                        bookMarkIcon.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_bookmark_red_asset, null
                            )
                        )
                    }

                } else if (options[item] == "Cancel") {
                    dialog.dismiss()
                }
            }
            builder.show()
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