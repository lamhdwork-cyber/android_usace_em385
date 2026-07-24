package com.navfac.usace.safety.base.extension

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified T : ViewModel> FragmentActivity.viewModel(factory: ViewModelProvider.Factory, body: T.() -> Unit): T {
    val vm = ViewModelProvider(this, factory).get(T::class.java) //ViewModelProviders.of(this, factory)[T::class.java]
    vm.body()
    return vm
}

fun Activity.goToActivity(mClass: Class<*>, finishCurrent: Boolean, extras: Bundle? = null,
                          withResult: Boolean = false, requestCode: Int = -1) {
    val intent = Intent(this, mClass)
    extras?.apply { intent.putExtras(this) }
    if (!withResult)
        this.startActivity(intent)
    else
        this.startActivityForResult(intent, requestCode)

    if (finishCurrent)
        this.finish()
}
