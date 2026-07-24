package com.navfac.usace.safety.features.table

import android.content.DialogInterface
import android.content.Intent
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
import com.navfac.usace.safety.databinding.FragmentTableBinding
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.safetyvideos.imagedetails.ImageDetailsActivity
import com.navfac.usace.safety.features.table.adapter.TableAdapter
import com.navfac.usace.safety.features.table.model.TableBaseModelItem
import io.realm.kotlin.ext.query


class TableFragment : BaseFragment<FragmentTableBinding>() {

    private lateinit var adapter: TableAdapter

    //private lateinit var model: TableBaseModel
    private var model = mutableListOf<TableBaseModelItem>()

    override val layoutRes: Int
        get() = R.layout.fragment_table

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreated(savedInstance: Bundle?) {
        binding.include3.ntoolbartitle.text = "Tables"
        binding.include3.ntoolbarbacku.setOnClickListener {
            backPressedAction()
        }

        val gson = Gson()
        //model = gson.fromJson(BaseLocalJsonParser.parseJSONData(requireContext(), "tables/tables.json"), TableBaseModel::class.java)
        model = gson.fromJson("tables/tables.json".toJson(requireContext()) ?: "")
        binding.tableRecyclerview.setHasFixedSize(true)
        binding.tableRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = TableAdapter(model,realmBookmark)
        binding.tableRecyclerview.adapter = adapter
        adapter.notifyDataSetChanged()

        /*
        * Action For Details View
        * */
        adapter.actionListener = {
            startActivity(
                Intent(requireContext(), ImageDetailsActivity::class.java)
                    .putExtra(ImageDetailsActivity.EXTRA_IMAGE, it.image)
                    .putExtra(ImageDetailsActivity.EXTRA_TERMS, it.terms)
            )
        }

        /*
        * Action For Menu
        * */
        adapter.actionListenerfordialog = { it: TableBaseModelItem, bookMarkIcon: ImageView ->
            val options = arrayOf<CharSequence>("Add to Bookmark", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")
            builder.setCancelable(false)
            builder.setItems(options) { dialog: DialogInterface, item: Int ->
                if (options[item] == "Add to Bookmark") {
                    val bookmark = realmBookmark.query<BookmarkModel>("name == $0", it.terms)
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
                                    type = "Tables"
                                    name = it.terms
                                    description = it.headline
                                    isImage = true
                                    isArticle = false
                                    datafile = it.image
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

    override fun setupActions() {}
}