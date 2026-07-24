package com.navfac.usace.safety.base.platform

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

object BaseFragmentController {

    fun load(fragment: Fragment, framelayout: Int, context: Context) {
        val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
        transaction.replace(framelayout, fragment, fragment::class.java.name)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        //transaction.addToBackStack(null)
        transaction.commit()
    }

}