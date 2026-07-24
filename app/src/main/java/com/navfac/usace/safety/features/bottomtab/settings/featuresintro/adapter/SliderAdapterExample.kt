package com.navfac.usace.safety.features.bottomtab.settings.featuresintro.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.features.bottomtab.settings.featuresintro.model.IntroDataModel
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapterExample(private var mSliderItems: MutableList<IntroDataModel>) :
        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_featuresintro_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        viewHolder.introTitle.text = mSliderItems[position].title
        viewHolder.introContent.text = mSliderItems[position].content
        val sliderItem: Drawable = mSliderItems[position].image
        viewHolder.introImage.setImageDrawable(sliderItem)
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    class SliderAdapterVH(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        internal var introImage: ImageView = itemView.findViewById(R.id.intro_image)
        internal var introTitle: TextView = itemView.findViewById(R.id.intro_title)
        internal var introContent: TextView = itemView.findViewById(R.id.intro_content)
    }
}