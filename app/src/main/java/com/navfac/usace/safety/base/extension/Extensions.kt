package com.navfac.usace.safety.base.extension


import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

inline fun tryCatch(onTry: () -> Unit, onCatch: (Exception) -> Unit) {
    try {
        onTry.invoke()
    } catch (e: Exception) {
        onCatch.invoke(e)
    }
}

fun showToast(ctx: Context, msg: String) { //inline
    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
}

fun showMessageDialog(ctx: Context, msg: String) {  //inline
    val builder = AlertDialog.Builder(ctx)
    builder.setMessage(msg)
    builder.setIcon(android.R.drawable.ic_dialog_alert)
    builder.setPositiveButton("Ok") { dialogInterface, _ -> //which ->
        dialogInterface.dismiss()
    }
    val alertDialog: AlertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.show()
}

inline fun showMessageDialogWithAction(
    ctx: Context,
    msg: String,
    btnTxtPositive: String,
    btnTxtNegative: String,
    crossinline onPositiveButtonClick: () -> Unit
) {
    val builder = AlertDialog.Builder(ctx)
    builder.setMessage(msg)
    builder.setIcon(android.R.drawable.ic_dialog_alert)
    builder.setPositiveButton(btnTxtPositive) { _, _ -> //dialogInterface, which ->
        onPositiveButtonClick.invoke()
    }
    builder.setNegativeButton(btnTxtNegative) { dialogInterface, _ -> //which ->
        dialogInterface.dismiss()
    }
    val alertDialog: AlertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.show()
}


fun View.setVisibility(value: Boolean) {    //inline
    this.visibility = if (value) View.VISIBLE else View.GONE
}

fun isNightMode(context: Context) : Boolean {
    val flags: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return (flags == Configuration.UI_MODE_NIGHT_YES)
}

fun ViewPager2.reduceDragSensitivity() {
    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true
    val recyclerView = recyclerViewField.get(this) as RecyclerView

    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
    touchSlopField.isAccessible = true
    val touchSlop = touchSlopField.get(recyclerView) as Int
    touchSlopField.set(recyclerView, touchSlop*8)       // "8" was obtained experimentally
}