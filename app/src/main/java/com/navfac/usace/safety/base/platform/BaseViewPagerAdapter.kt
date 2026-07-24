package com.navfac.usace.safety.base.platform

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BaseViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

    private val fragmentlist: MutableList<Fragment> = ArrayList()
    private val titlelist: MutableList<String> = ArrayList()

    override fun getCount(): Int {
        return fragmentlist.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentlist[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentlist.add(fragment)
        titlelist.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titlelist[position]
    }
}