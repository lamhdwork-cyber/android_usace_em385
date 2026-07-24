package com.navfac.usace.safety.features.bottomtab.search

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.application.App
import com.navfac.usace.safety.base.data.definition.HtmlType
import com.navfac.usace.safety.base.extension.checkFileExistInAssets
import com.navfac.usace.safety.base.extension.configureInterceptorWithEmpty
import com.navfac.usace.safety.base.extension.goToActivity
import com.navfac.usace.safety.base.extension.showToast
import com.navfac.usace.safety.base.platform.BaseFragment
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.RandomCodeGen
import com.navfac.usace.safety.databinding.FragmentSearchBinding
import com.navfac.usace.safety.features.MainActivity
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.bottomtab.search.adapter.SearchAdapter
import com.navfac.usace.safety.features.bottomtab.search.model.SearchModelItem
import com.navfac.usace.safety.features.htmlpage.HtmlPageActivity
import com.navfac.usace.safety.features.safetyvideos.imagedetails.ImageDetailsActivity
import com.navfac.usace.safety.features.sectionandappendices.appendices.CATEGORY_APPENDICES
import com.navfac.usace.safety.features.sectionandappendices.chapter.model.ChapterModelItem
import com.navfac.usace.safety.features.sectionandappendices.sections.CATEGORY_CHAPTER
import com.navfac.usace.safety.features.sectionandappendices.sections.CATEGORY_SECTION
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.realm.kotlin.ext.query
import java.util.Locale


class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    private val searchQueryStream = PublishSubject.create<String>()

    init {
        searchQueryStream.configureInterceptorWithEmpty(500)
            .subscribe { result -> onSearch(result) }.addTo(disposeBag)
    }

    private lateinit var adapter: SearchAdapter
    private var model = mutableListOf<SearchModelItem>()

    override val layoutRes: Int
        get() = R.layout.fragment_search

    override fun getViewModel(): BaseViewModel? = null

    var lastSearch = ""
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreated(savedInstance: Bundle?) {
        /*binding.include6.ntoolbarbacku.setOnClickListener {
            backPressedAction()
        }

        binding.include6.ntoolbartitle.text = "Search"*/

        val gson = Gson()
        //model = gson.fromJson(BaseLocalJsonParser.parseJSONData(requireContext(), "search/search.json"), SearchModel::class.java)
        //model = gson.fromJson("search/search.json".toJson(requireContext()) ?: "")
        //model.addAll(SectionHelper.parseToSearchItems(requireContext()))
        //model.addAll(AppendicesHelper.parseToSearchItem(requireContext()))
        model.addAll(((requireContext() as MainActivity).application as App)._searchItems)
        binding.searchRecyclerview.setHasFixedSize(true)
        binding.searchRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchAdapter(requireContext(), arrayListOf(), realmBookmark)
        binding.searchRecyclerview.adapter = adapter
        adapter.notifyDataSetChanged()

        adapter.actionListener = {
            val keyword = binding.searchEdittextTable.text.toString()
            lastSearch = keyword
            closeKeyboard()
            when (it.category) {
                CATEGORY_SECTION -> {
                    if (it.file?.checkFileExistInAssets(
                            requireContext(),
                            "content/sections_new"
                        ) == true
                    ) {
                        val page = it.page
                        activity?.goToActivity(
                            HtmlPageActivity::class.java, false,
                            bundleOf(
                                Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.SECTIONS_NEW),
                                Pair(HtmlPageActivity.EXTRA_CHAPTER_ID, it.id + 1),
                                Pair(HtmlPageActivity.EXTRA_CHAPTER_KEY, it.key),
                                Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, page),
                                Pair(
                                    HtmlPageActivity.EXTRA_GO_TO_PAGE_CRAWL,
                                    it.file
                                ),
                                "searchdata" to keyword
                            )
                        )
                    } else {
                        showToast(requireContext(), "File not exist")
                    }
                }

                CATEGORY_APPENDICES -> {
                    activity?.goToActivity(
                        HtmlPageActivity::class.java, false,
                        bundleOf(
                            Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.APPENDICES),
                            Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, it.file?.toInt()),
                            "searchdata" to keyword
                        )
                    )
                }

                CATEGORY_CHAPTER -> {
                    if (it.data == null)
                        activity?.goToActivity(
                            HtmlPageActivity::class.java, false,
                            bundleOf(
                                Pair(HtmlPageActivity.EXTRA_TYPE, HtmlType.CHAPTERS),
                                Pair(HtmlPageActivity.EXTRA_GO_TO_PAGE, 0),
                                Pair(
                                    HtmlPageActivity.EXTRA_GO_TO_PAGE_CRAWL,
                                    "purpose_of_manual.html"
                                ),
                                "searchdata" to keyword
                            )
                        )
                    else {
                        findNavController().navigate(R.id.sectionAppendicesFragment)
                        Handler().postDelayed({
                            val intent = Intent()
                            intent.action = "com.navfac.usace.safety.select_chap"
                            intent.putExtra(
                                "Chapter",
                                gson.fromJson(it.data, ChapterModelItem::class.java)
                            )
                            LocalBroadcastManager.getInstance(requireContext())
                                .sendBroadcast(intent)
                        }, 500)
                    }
                }

                else -> {
                    if (!it.file.equals("")) {
                        startActivity(
                            Intent(requireContext(), ImageDetailsActivity::class.java)
                                .putExtra(ImageDetailsActivity.EXTRA_IMAGE, it.file)
                                .putExtra(ImageDetailsActivity.EXTRA_TERMS, it.data)
                        )
                    }
                }
            }
        }

        adapter.actionListenerforBookmark = { it: SearchModelItem, bookMarkIcon: ImageView ->
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
                    } else {
                        if (it.category.equals("table") || it.category.equals("figure") || it.category.equals(
                                "form"
                            )
                        ) {
                            realmBookmark.writeBlocking {
                                copyToRealm(
                                    BookmarkModel().apply {
                                        id = RandomCodeGen.generate()
                                        type = it.category
                                        name = it.data
                                        description = it.name
                                        isImage = true
                                        isArticle = false
                                        datafile = it.file
                                    }
                                )
                            }
                            showMessage("Added To Bookmark")
                            dialog.dismiss()
                        } else {
                            realmBookmark.writeBlocking {
                                copyToRealm(
                                    BookmarkModel().apply {
                                        id = RandomCodeGen.generate()
                                        type = it.category
                                        name = it.name
                                        description = it.data
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
                    }

                } else if (options[item] == "Cancel") {
                    dialog.dismiss()
                }
            }
            builder.show()
        }

        /*
        * Search box
        * */
        binding.searchEdittextTable.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (lastSearch != s.toString()) {
                    binding.progress.visibility = View.VISIBLE
                    adapter.filterlist(emptyList())
                    searchQueryStream.onNext(s.toString())
                }
            }
        })


        binding.itemMicrophone.setOnClickListener {
            // Get the Intent action
            val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            // Language model defines the purpose, there are special models for other use cases, like search.
            sttIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            // Adding an extra language, you can use any language from the Locale class.
            sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            // Text that shows up on the Speech input prompt.
            sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")
            try {
                // Start the intent for a result, and pass in our request code.
                startActivityForResult(sttIntent, 1)
            } catch (e: ActivityNotFoundException) {
                // Handling error when the service is not available.
                e.printStackTrace()
                showMessage("Your device does not support Speech to text recognition")
            }
        }

        binding.searchEdittextTable.requestFocus()
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            binding.searchEdittextTable,
            InputMethodManager.SHOW_IMPLICIT
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // Handle the result for our request code.
            1 -> {
                // Safety checks to ensure data is available.
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Retrieve the result array.
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    // Ensure result array is not null or empty to avoid errors.
                    if (!result.isNullOrEmpty()) {
                        // Recognized text is in the first position.
                        val recognizedText = result[0]
                        // Do what you want with the recognized text.
                        binding.searchEdittextTable.setText(recognizedText)
                    }
                }
            }
        }

    }

    private fun onSearch(searchText: String) {
        binding.progress.visibility = View.GONE
        when {
            searchText.isEmpty() -> adapter.filterlist(arrayListOf())
            else -> {
                val list: MutableList<SearchModelItem> = ArrayList()
                list.addAll(model.filter { item ->
                    item.htmlContentValue?.lowercase()
                        ?.contains(searchText.lowercase(), true) == true
                }.filter {
                    it.category == "chapter" || !it.file.isNullOrBlank()
                })

                when {
                    list.size > 0 -> binding.nodatatext.visibility = View.GONE
                    else -> binding.nodatatext.visibility = View.VISIBLE
                }
                list.sortBy { it.id }
                adapter.filterlist(list)
            }
        }
    }

    override fun backPressedAction() {
        findNavController().navigateUp()
    }

    override fun setupActions() {

    }

    /*class populateList() : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            TODO("Not yet implemented")
        }
    }*/


    private fun closeKeyboard() {
        // on below line we are creating a variable
        // for input manager and initializing it.
        val inputMethodManager =
            context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        // on below line hiding our keyboard.
        inputMethodManager.hideSoftInputFromWindow(binding.searchEdittextTable.windowToken, 0)

    }
}