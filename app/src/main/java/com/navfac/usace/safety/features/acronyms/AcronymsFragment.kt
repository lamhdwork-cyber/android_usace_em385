package com.navfac.usace.safety.features.acronyms

import android.content.DialogInterface
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.extension.fromJson
import com.navfac.usace.safety.base.extension.toJson
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.RandomCodeGen
import com.navfac.usace.safety.databinding.FragmentAcronymsBinding
import com.navfac.usace.safety.features.acronyms.adapter.AcronymAdapter
import com.navfac.usace.safety.features.acronyms.model.AcronymModelItem
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import io.realm.kotlin.ext.query

class AcronymsFragment : BaseFragment<FragmentAcronymsBinding>() {

    private lateinit var adapter: AcronymAdapter

    //private lateinit var model: AcronymnModel
    private var model = mutableListOf<AcronymModelItem>()

    override val layoutRes: Int
        get() = R.layout.fragment_acronyms

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreated(savedInstance: Bundle?) {

        binding.include2.ntoolbartitle.text = "Acronyms"
        binding.include2.ntoolbarbacku.setOnClickListener {
            backPressedAction()
        }

        val gson = Gson()
        //model = gson.fromJson(BaseLocalJsonParser.parseJSONData(requireContext(), "acronyms/acronyms.json"), AcronymnModel::class.java)
        model = gson.fromJson("acronyms/acronyms.json".toJson(requireContext()) ?: "")
        binding.recyclerviewAcronyms.setHasFixedSize(true)
        binding.recyclerviewAcronyms.layoutManager = LinearLayoutManager(requireContext())
        adapter = AcronymAdapter(requireContext(), model, realmBookmark)
        binding.recyclerviewAcronyms.adapter = adapter
        adapter.notifyDataSetChanged()


        /*
       * Action For Menu
       * */
        adapter.actionListener = { it: AcronymModelItem, bookMarkIcon: ImageView ->
            val options = arrayOf<CharSequence>("Add to Bookmark", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")
            builder.setCancelable(false)
            builder.setItems(options) { dialog: DialogInterface, item: Int ->
                if (options[item] == "Add to Bookmark") {
                    val bookmark = realmBookmark.query<BookmarkModel>("name == $0", it.name?.trim())
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
                                    type = "Acronyms"
                                    name = it.name
                                    description = it.definition
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
}