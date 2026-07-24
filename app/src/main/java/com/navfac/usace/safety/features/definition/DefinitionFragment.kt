package com.navfac.usace.safety.features.definition

import android.annotation.SuppressLint
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
import com.navfac.usace.safety.databinding.FragmentDefinitionBinding
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.definition.adapter.DefinitionAdapter
import com.navfac.usace.safety.features.definition.model.DefinitionBaseItem
import io.realm.kotlin.ext.query

class DefinitionFragment : BaseFragment<FragmentDefinitionBinding>() {


    private lateinit var adapter: DefinitionAdapter

    //private lateinit var model: DefinitionBase
    private var model = mutableListOf<DefinitionBaseItem>()

    override val layoutRes: Int get() = R.layout.fragment_definition

    override fun getViewModel(): BaseViewModel? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreated(savedInstance: Bundle?) {

        binding.include4eee.ntoolbartitle.text = "Definitions"
        binding.include4eee.ntoolbarbacku.setOnClickListener {
            backPressedAction()
        }

        val gson = Gson()
        //model = gson.fromJson(BaseLocalJsonParser.parseJSONData(requireContext(), "definition/definitions.json"), DefinitionBaseItem::class.java)
        model = gson.fromJson("definition/definitions.json".toJson(requireContext()) ?: "")
        binding.recyclerviewDefinitions.setHasFixedSize(true)
        binding.recyclerviewDefinitions.layoutManager = LinearLayoutManager(requireContext())
        adapter = DefinitionAdapter(requireContext(), model, realmBookmark)
        binding.recyclerviewDefinitions.adapter = adapter
        adapter.notifyDataSetChanged()


        /*
        * Action For Menu
        * */
        adapter.actionListener = { it: DefinitionBaseItem, bookMarkIcon: ImageView ->
            val options = arrayOf<CharSequence>("Add to Bookmark", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")
            builder.setCancelable(false)
            builder.setItems(options) { dialog: DialogInterface, item: Int ->
                //if (options[item] == "Add to Bookmark") {
                if (item == 0) {
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
                                    type = "Definitions"
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
                                R.drawable.ic_bookmark_red_asset,
                                null
                            )
                        )
                    }

                } else if (item == 1) { //if (options[item] == "Cancel") {
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