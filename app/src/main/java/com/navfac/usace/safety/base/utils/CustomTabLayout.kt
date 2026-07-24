package com.navfac.usace.safety.base.utils

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.navfac.usace.safety.R

class CustomTabLayout : TabLayout {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val tabLayout = getChildAt(0) as ViewGroup
        val childCount = tabLayout.childCount
        if (childCount != 0) {
            val displayMetrics = context.resources.displayMetrics
            val tabMinWidth = displayMetrics.widthPixels / childCount
            for (i in 0 until childCount) {
                tabLayout.getChildAt(i).minimumWidth = tabMinWidth
            }

            if (childCount > 1)
                setSelectedTabIndicatorColor(
                    ContextCompat.getColor(
                        tabLayout.context,
                        R.color.colorPrimaryDark
                    )
                )
            else
                setSelectedTabIndicatorColor(
                    ContextCompat.getColor(
                        tabLayout.context,
                        R.color.colorWhite
                    )
                )
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
