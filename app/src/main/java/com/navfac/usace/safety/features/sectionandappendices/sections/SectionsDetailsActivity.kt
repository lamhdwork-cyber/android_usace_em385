package com.navfac.usace.safety.features.sectionandappendices.sections

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.gson.Gson
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.db.SectionsSubtitleEntity
import com.navfac.usace.safety.base.db.TitlesAndSubtitles
import com.navfac.usace.safety.base.extension.fromJson
import com.navfac.usace.safety.base.extension.observe
import com.navfac.usace.safety.base.extension.reduceDragSensitivity
import com.navfac.usace.safety.base.platform.BaseActivity
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.readHtmlFile
import com.navfac.usace.safety.base.utils.share
import com.navfac.usace.safety.databinding.ActivitySectionsDetailsBinding
import com.navfac.usace.safety.features.sectionandappendices.sections.adapter.SectionsDetailsHtmlAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SectionsDetailsActivity : BaseActivity<ActivitySectionsDetailsBinding>() {

    @Inject
    lateinit var adapterPages: SectionsDetailsHtmlAdapter
    private lateinit var vm: SectionsDetailsViewModel

    var gotoPage: Int? = null
    var groupId: Int? = null
    val subtitleList = mutableListOf<SectionsSubtitleEntity>()
    val titleList = mutableListOf<TitlesAndSubtitles>()
    var sectionsBundle: SectionsSubtitleEntity? = null

    override val layoutRes: Int
        get() = R.layout.activity_sections_details

    override fun onCreated(instance: Bundle?) {
        vm = ViewModelProvider(this)[SectionsDetailsViewModel::class.java]
        binding.include5.toolbartitle.text = "" //"Sections"
        binding.include5.toolbarBackicon.setOnClickListener { onBackPressed() }

        checkExtras()
        initViews()
        initObserver()

        binding.include5.apply {
            toolbarFontsettings.setOnClickListener {
                showFirstBottomDialog()
            }
            toolbarBookmark.setOnClickListener {
                val pos = binding.sectionsVpView.currentItem
                if (vm.addToBookmark(
                        vm.getAllSectionSubtitle.value!![pos].title, pos,
                        "file:///android_asset/sections/${vm.filenameList.value!![pos]}"
                    )
                )
                    updateBookmark(
                        R.drawable.ic_bookmark_red_asset,
                        binding.include5.toolbarBookmark
                    )
            }
            toolbarShare.setOnClickListener {
                onShare()
            }
        }
    }

    fun onShare() {
        val pos = binding.sectionsVpView.currentItem
        readHtmlFile("sections/", vm.filenameList.value!![pos], this)?.let { text ->
            share(this, subtitleList[pos].title, text)
        }
    }

    private fun initObserver() {
        vm.apply {
            vm.initSectionsData()
            observe(sections) {
                it.apply { adapterPages.collection = it!! }
            }
            observe(getAllSectionSubtitle) {
                it?.forEachIndexed { index, sectionsSubtitleEntity ->
                    subtitleList.add(sectionsSubtitleEntity)
                }
            }
//            observe(getTitlesAndSubtitles) {
//                it?.forEach { item ->
//                    titleList.add(TitlesAndSubtitles(item.title, item.subtitles,  item.expanded == false))
//                }
//            }
        }
        binding.lifecycleOwner = this
        binding.vm = vm
    }

    private fun checkExtras() {
        intent?.extras?.apply {
            if (containsKey(EXTRA_SECTIONS)) {
                sectionsBundle = Gson().fromJson(getString(EXTRA_SECTIONS, ""))
                gotoPage = sectionsBundle!!.firstPage.toInt() /*- 1*/
                groupId = sectionsBundle!!.id /*- 1*/
                remove(EXTRA_SECTIONS)
            } else finish()
        } ?: finish()
    }

    private fun initViews() {
        binding.sectionsVpView.apply {
            adapter = adapterPages
            reduceDragSensitivity()
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    //val currentSection = SectionHelper.loadSectionsFile(this@SectionsDetailsActivity,
                    //                                    position.toString(), titleList.toString())
                    //if (isInBookmark(currentSection))
                    //  updateBookmark(R.drawable.ic_bookmark_red_asset, binding.include5.toolbarBookmark)
                }
            })
        }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.sectionsVpView.setCurrentItem(gotoPage!!, false)
        }, 100)
    }

    fun showFirstBottomDialog() {
        val bottomDialog = Dialog(this, R.style.BottomDialog)
        val contentView: View = LayoutInflater.from(this).inflate(R.layout.custom_bottomsheet, null)
        bottomDialog.setContentView(contentView)
        val layoutParams: ViewGroup.LayoutParams = contentView.layoutParams
        layoutParams.width = this.resources.displayMetrics.widthPixels
        contentView.layoutParams = layoutParams
        bottomDialog.window!!.setGravity(Gravity.BOTTOM)
        bottomDialog.setCanceledOnTouchOutside(false)
        bottomDialog.window!!.setWindowAnimations(R.style.BottomDialog_Animation)
        bottomDialog.show()

        val increasefont = contentView.findViewById(R.id.size_large) as ConstraintLayout
        val decreasefont = contentView.findViewById(R.id.size_small) as ConstraintLayout
        val closebottomsheer = contentView.findViewById(R.id.close_dialog) as ImageView
        val smallalign = contentView.findViewById(R.id.align_left) as CardView
        val mediumalign = contentView.findViewById(R.id.align_center) as CardView
        val largealign = contentView.findViewById(R.id.align_right) as CardView

        closebottomsheer.setOnClickListener {
            bottomDialog.cancel()
        }

        smallalign.setOnClickListener {
            smallalign.setBackgroundResource(R.drawable.clickablealigndialog)
            mediumalign.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray))
            largealign.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray))
            textAlignment("left")
        }

        mediumalign.setOnClickListener {
            mediumalign.setBackgroundResource(R.drawable.clickablealigndialog)
            smallalign.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray))
            largealign.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray))
            textAlignment("center")
        }

        largealign.setOnClickListener {
            largealign.setBackgroundResource(R.drawable.clickablealigndialog)
            mediumalign.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray))
            smallalign.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray))
            textAlignment("right")
        }

        increasefont.setOnClickListener { textSize(2) }
        decreasefont.setOnClickListener { textSize(1) }
    }

    fun textAlignment(alignment: String) {
        var newAlign = if (alignment.isEmpty()) "left" else alignment
        val pos = binding.sectionsVpView.currentItem
        vm.sections.value!![pos].textAlignment = newAlign
        adapterPages.collection = vm.sections.value!!
        adapterPages.notifyItemChanged(pos)
    }

    fun textSize(operator: Int) {
        val pos = binding.sectionsVpView.currentItem
        val test = vm.sections.value!![pos]

        when {
            test.textSize == 16 && operator == 2 -> test.textSize = 20
            test.textSize == 20 && operator == 2 -> test.textSize = 24
            test.textSize == 24 && operator == 2 -> test.textSize = 30
            test.textSize == 20 && operator == 1 -> test.textSize = 16
            test.textSize == 24 && operator == 1 -> test.textSize = 20
            test.textSize == 30 && operator == 1 -> test.textSize = 24
            else -> {}
        }
        adapterPages.notifyItemChanged(pos)
    }


    override fun processIntentData(data: Uri) {}

    companion object {
        const val EXTRA_SECTIONS = "e_sections"
        val file = "file:///android_asset/content/sections/"
    }

    override fun getViewModel(): BaseViewModel? = null

}
