package com.navfac.usace.safety.features.formfigure.figure

import android.annotation.SuppressLint
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
import com.navfac.usace.safety.databinding.FragmentFigureBinding
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.formfigure.adapter.FormFigureAdapter
import com.navfac.usace.safety.features.formfigure.model.FigureFormModelItem
import com.navfac.usace.safety.features.safetyvideos.imagedetails.ImageDetailsActivity
import io.realm.kotlin.ext.query

class FigureFragment : BaseFragment<FragmentFigureBinding>() {

    //private lateinit var model: FigureFormModel
    private var model = mutableListOf<FigureFormModelItem>()
    private lateinit var adapter: FormFigureAdapter

    override val layoutRes: Int
        get() = R.layout.fragment_figure

    override fun getViewModel(): BaseViewModel? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreated(savedInstance: Bundle?) {
        val gson = Gson()
        //model = gson.fromJson(BaseLocalJsonParser.parseJSONData(requireContext(), "figure/figure.json"), FigureFormModel::class.java)
        model = gson.fromJson("figure/figure.json".toJson(requireContext()) ?: "")
        binding.figureRecyclerview.setHasFixedSize(true)
        binding.figureRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = FormFigureAdapter(requireContext(), model, realmBookmark)
        binding.figureRecyclerview.adapter = adapter
        adapter.notifyDataSetChanged()

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
        adapter.dialogActListener = { it: FigureFormModelItem, bookMarkIcon: ImageView ->
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
                                    type = "Figures"
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

    override fun setupActions() {

    }
}