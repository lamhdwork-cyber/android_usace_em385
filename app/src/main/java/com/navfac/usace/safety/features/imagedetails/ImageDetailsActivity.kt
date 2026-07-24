package com.navfac.usace.safety.features.imagedetails

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.platform.BaseActivity
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.base.utils.share
import com.navfac.usace.safety.databinding.ActivityImageDetailsBinding
import java.io.IOException
import java.io.InputStream


class ImageDetailsActivity : BaseActivity<ActivityImageDetailsBinding>() {

    private lateinit var viewModel: ImageDetailsViewModel

    override fun getViewModel(): BaseViewModel = viewModel

    override val layoutRes: Int
        get() = R.layout.activity_image_details

    override fun onCreated(instance: Bundle?) {
        viewModel = ViewModelProvider(this)[ImageDetailsViewModel::class.java]
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 102)

        try {
            val image = intent.getStringExtra(EXTRA_IMAGE)
            val terms = intent.getStringExtra(EXTRA_TERMS)

            binding.includetoolbar.dtoolbartitle.text = terms
            binding.includetoolbar.dtoolbarback.setOnClickListener { onBackPressed() }
            val ims: InputStream = assets.open("imageresource/${image}")
            val d = Drawable.createFromStream(ims, null)
            binding.photoView.setImageDrawable(d)

            binding.includetoolbar.dtoolbardownload.setOnClickListener {
                viewModel.saveImageToGallery(this, terms!!, binding.photoView)
            }

            binding.includetoolbar.ivShare.setOnClickListener {
                share(this, terms?: "Share Image", binding.photoView)
            }


        } catch (ex: IOException) {
            return
        }

    }

    override fun processIntentData(data: Uri) {

    }

    companion object{
        const val EXTRA_IMAGE = "image"
        const val EXTRA_TERMS = "terms"
    }

}