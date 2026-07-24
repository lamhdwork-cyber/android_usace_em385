package com.navfac.usace.safety.features.sectionandappendices.appendices.details

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
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.db.AppendicesEntity
import com.navfac.usace.safety.base.extension.fromJson
import com.navfac.usace.safety.base.extension.observe
import com.navfac.usace.safety.base.extension.reduceDragSensitivity
import com.navfac.usace.safety.base.platform.BaseActivity
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.readHtmlFile
import com.navfac.usace.safety.base.utils.share
import com.navfac.usace.safety.databinding.ActivityAppendicesDetailsBinding
import com.navfac.usace.safety.features.sectionandappendices.appendices.adapter.AppendicesDetailsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AppendicesDetailsActivity : BaseActivity<ActivityAppendicesDetailsBinding>() {
    @Inject
    lateinit var adapterAppendices: AppendicesDetailsAdapter
    private lateinit var vm: AppendixDetailsViewModel

    var gotoPage: Int? = null
    var appendicesBundle: AppendicesEntity? = null

    override val layoutRes: Int
        get() = R.layout.activity_appendices_details

    override fun getViewModel(): BaseViewModel? =null

    override fun onCreated(instance: Bundle?) {
        vm = ViewModelProvider(this)[AppendixDetailsViewModel::class.java]
        binding.include5.toolbartitle.text = "" //"Appendices"
        binding.include5.toolbarBackicon.setOnClickListener { onBackPressed() }
        checkExtras()
        initViews()
        initObserver()
        onClick()
    }

    private fun initObserver() {
        vm.apply {
            vm.initAppendicesData()
            observe(appendices) {
                it.apply { adapterAppendices.collection = it!! }
            }
            observe(getAllAppendices) {
            }
        }
        binding.lifecycleOwner = this
        binding.vm = vm
    }

    private fun checkExtras() {
        intent?.extras?.apply {
            if(containsKey(EXTRA_APPENDICES)) {
                appendicesBundle = Gson().fromJson(getString(EXTRA_APPENDICES, ""))
                gotoPage = appendicesBundle!!.firstPage.dropLast(5).takeLast(2).toInt() - 1
                remove(EXTRA_APPENDICES)
            } else finish()
        } ?: finish()
    }

    private fun initViews() {
        binding.appendicesVpView.apply {
            adapter = adapterAppendices
            reduceDragSensitivity()
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }
            })
        }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.appendicesVpView.setCurrentItem(gotoPage!!, false)
        }, 100)
    }

    override fun processIntentData(data: Uri) {}

    private fun onClick() {
        binding.include5.apply {
            toolbarFontsettings.setOnClickListener {
                showFirstBottomDialog()
            }
            toolbarBookmark.setOnClickListener {
                if (vm.addToBookmark(vm.getAllAppendices.value!![binding.appendicesVpView.currentItem].title,
                        binding.appendicesVpView.currentItem,
                        "file:///android_asset/appendices/${vm.getAllAppendices.value!![binding.appendicesVpView.currentItem].firstPage}"))
                    updateBookmark(R.drawable.ic_bookmark_red_asset, binding.include5.toolbarBookmark)
            }
            toolbarShare.setOnClickListener {
                val pos = binding.appendicesVpView.currentItem
                val all = vm.getAllAppendices.value
                readHtmlFile("appendices/", "${all?.get(pos)?.firstPage}",
                    this@AppendicesDetailsActivity)?.apply {
                    share(this@AppendicesDetailsActivity,
                        all?.get(pos)?.title ?: "Appendix", this)
                }
            }
        }
    }

    private fun showFirstBottomDialog() {
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

        val increaseFont = contentView.findViewById(R.id.size_large) as ConstraintLayout
        val decreaseFont = contentView.findViewById(R.id.size_small) as ConstraintLayout
        val closeButton = contentView.findViewById(R.id.close_dialog) as ImageView
        val leftAlign   = contentView.findViewById(R.id.align_left) as CardView
        val centerAlign = contentView.findViewById(R.id.align_center) as CardView
        val rightAlign  = contentView.findViewById(R.id.align_right) as CardView

        closeButton.setOnClickListener {
            bottomDialog.cancel()
        }

        leftAlign.setOnClickListener {
            leftAlign.setCardBackgroundColor(ContextCompat.getColor(this, R.color.button_back))
            centerAlign.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            rightAlign.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            textAlignment("left")
        }

        centerAlign.setOnClickListener {
            centerAlign.setCardBackgroundColor(ContextCompat.getColor(this, R.color.button_back))
            leftAlign.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            rightAlign.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            textAlignment("center")
        }

        rightAlign.setOnClickListener {
            rightAlign.setCardBackgroundColor(ContextCompat.getColor(this, R.color.button_back))
            centerAlign.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            leftAlign.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            textAlignment("right")
        }

        increaseFont.setOnClickListener {
            textSize(2)
        }

        decreaseFont.setOnClickListener {
            textSize(1)
        }
    }

    fun textAlignment(alignment: String) {
        alignment.let {
            vm.appendices.value!![binding.appendicesVpView.currentItem].textAlignment = alignment
            adapterAppendices.collection = vm.appendices.value!!
            adapterAppendices.notifyItemChanged(binding.appendicesVpView.currentItem)
        }
    }

    fun textSize(operator: Int) {
        val test = vm.appendices.value!![binding.appendicesVpView.currentItem]
        when  {
            test.textSize == 16 && operator == 2 -> test.textSize = 20
            test.textSize == 20 && operator == 2 -> test.textSize = 24
            test.textSize == 24 && operator == 2 -> test.textSize = 30
            test.textSize == 20 && operator == 1 -> test.textSize = 16
            test.textSize == 24 && operator == 1 -> test.textSize = 20
            test.textSize == 30 && operator == 1 -> test.textSize = 24
            else -> {}
        }
        adapterAppendices.notifyItemChanged(binding.appendicesVpView.currentItem)
    }

    companion object {
        const val EXTRA_APPENDICES = "e_appendices"
        const val file = "file:///android_asset/content/appendices/"
        const val EXTRA_TITLE = "e_title"
        const val EXTRA_CHILD_POSITION = "e_child_position"
    }
}