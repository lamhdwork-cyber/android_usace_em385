package com.navfac.usace.safety.base.platform

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class BaseViewPagerAdapterWithoutTitle(fm: FragmentManager, behaviour: Int) : FragmentStatePagerAdapter(fm, behaviour) {

    private val mFragmentList = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }


}