package com.navfac.usace.safety.features.bottomtab.settings.featuresintro

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.application.App
import com.navfac.usace.safety.base.platform.BaseActivity
import com.navfac.usace.safety.base.platform.BaseViewModel
import com.navfac.usace.safety.databinding.ActivityFeaturesIntroBinding
import com.navfac.usace.safety.features.bottomtab.settings.featuresintro.adapter.SliderAdapterExample
import com.navfac.usace.safety.features.bottomtab.settings.featuresintro.model.IntroDataModel
import com.navfac.usace.safety.features.bottomtab.settings.waiver.WaiverActivity
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderPager
import com.smarteist.autoimageslider.SliderView

class FeaturesIntroActivity : BaseActivity<ActivityFeaturesIntroBinding>() {

    override val layoutRes: Int
        get() = R.layout.activity_features_intro

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreated(instance: Bundle?) {


        binding.introSlider.setSliderAdapter(SliderAdapterExample(getSliderData(this)))
        binding.introSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.introSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.introSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        binding.introSlider.indicatorSelectedColor = Color.parseColor("#EE1D23")
        binding.introSlider.indicatorUnselectedColor = Color.WHITE
        binding.introSlider.scrollTimeInSec = 15
        binding.introSlider.startAutoCycle()

        //if (!(application as App).kesho.has("feature")) binding.tvSkip.visibility = View.VISIBLE
        binding.introSlider.sliderPager.addOnPageChangeListener(object: SliderPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                binding.tvSkip.text = when (position) {
                    3 -> getString(R.string.app_intro_done) //"Done"
                    else -> getString(R.string.app_intro_skip) //"Skip Tutorial"
                }
            }
        })

        binding.tvSkip.setOnClickListener {
            val intent = Intent(this, WaiverActivity::class.java)
            intent.putExtra("item", "1")
            startActivity(intent)
            (application as App).kesho.push("feature", false)
        }
    }

    override fun processIntentData(data: Uri) {

    }

    private fun getSliderData(context: Context) = mutableListOf(
            IntroDataModel(
                    getString(R.string.features_into_title_1),
                    getString(R.string.features_into_content_1),
                    ContextCompat.getDrawable(context, R.drawable.viewmanual_icon)!!
            ),
            IntroDataModel(
                    getString(R.string.features_into_title_2),
                    getString(R.string.features_into_content_2),
                    ContextCompat.getDrawable(context, R.drawable.search_icon)!!
            ),
            IntroDataModel(
                    getString(R.string.features_into_title_3),
                    getString(R.string.features_into_content_3),
                    ContextCompat.getDrawable(context, R.drawable.bookmarking_icon)!!
            ),
            IntroDataModel(
                    getString(R.string.features_into_title_4),
                    getString(R.string.features_into_content_4),
                    ContextCompat.getDrawable(context, R.drawable.offline_icon)!!
            )
    )
}