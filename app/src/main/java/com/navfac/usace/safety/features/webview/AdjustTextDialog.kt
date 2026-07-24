package com.navfac.usace.safety.features.webview

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.utils.Constants
import com.navfac.usace.safety.databinding.CustomBottomsheetBinding
import com.preference.PowerPreference

class AdjustTextDialog(private val listener: AdjustTextListener) : BottomSheetDialogFragment() {
    var binding: CustomBottomsheetBinding? = null
    val layoutResId = R.layout.custom_bottomsheet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setOnShowListener(DialogInterface.OnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    ?: return@OnShowListener
            BottomSheetBehavior.from(bottomSheet)
            bottomSheet.background = null
        })
        //dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        initView()
        onClick()
    }

    private fun initView() {
        applySizeStatus()
        applyAlignStatus()
    }

    private fun applySizeStatus() {
        val curSize = PowerPreference.getDefaultFile().getInt(Constants.PREF_FONT_SIZE)
        val buttons = arrayListOf(binding?.sizeSmall, binding?.sizeMedium, binding?.sizeLarge)
        val values = arrayListOf(13, 16, 19)
        for (x in buttons.indices) {
            /*buttons[x]?.strokeColor = ContextCompat.getColorStateList(requireContext(),
                                        if (curSize == values[x]) R.color.myColorForeground
                                                            else R.color.myColorBackground)*/
            buttons[x]?.strokeWidth = if (curSize != values[x]) 0 else
                requireContext().resources.getDimensionPixelSize(R.dimen.button_stroke)
        }
    }

    private fun applyAlignStatus() {
        val pref = PowerPreference.getDefaultFile()
        val align = pref.getString(Constants.PREF_TEXT_ALIGN).trim()
        val values = arrayListOf("left", "center", "right")
        val buttons = arrayListOf(binding?.alignLeft, binding?.alignCenter, binding?.alignRight)
        var correct = 0

        for (x in buttons.indices) {
            if (align == values[x]) {
                buttons[x]?.setBackgroundResource(R.drawable.clickablealigndialog)
                correct += 1
            } else
                buttons[x]?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.transparent
                    )
                )
        }
        if (correct == 0) {
            pref.putString(Constants.PREF_TEXT_ALIGN, "left")
            buttons[0]?.setBackgroundResource(R.drawable.clickablealigndialog)
        }
    }

    private fun onClick() {
        binding?.closeDialog?.setOnClickListener {
            dialog?.dismiss()
        }

        binding?.sizeSmall?.setOnClickListener {
            onSize(13)
        }
        binding?.sizeMedium?.setOnClickListener {
            onSize(16)
        }
        binding?.sizeLarge?.setOnClickListener {
            onSize(19)
        }

        binding?.alignLeft?.setOnClickListener {
            onAlign("left")
        }
        binding?.alignCenter?.setOnClickListener {
            onAlign("center")
        }
        binding?.alignRight?.setOnClickListener {
            onAlign("right")
        }
    }

    private fun onSize(newSize: Int) {
        val pref = PowerPreference.getDefaultFile()
        val oldSize = pref.getInt(Constants.PREF_FONT_SIZE)
        if (newSize != oldSize) {
            pref.putInt(Constants.PREF_FONT_SIZE, newSize)
            listener.onTextSizeChanged(newSize)
            applySizeStatus()
            //dialog?.dismiss()
        }
    }

    private fun onAlign(newAlign: String) {
        val pref = PowerPreference.getDefaultFile()
        val oldAlign = pref.getString(Constants.PREF_TEXT_ALIGN)
        if (newAlign != oldAlign) {
            pref.putString(Constants.PREF_TEXT_ALIGN, newAlign)
            listener.onAlignmentChanged(newAlign)
            applyAlignStatus()
            //dialog?.dismiss()
        }
    }
}

interface AdjustTextListener {
    fun onTextSizeChanged(size: Int)
    fun onAlignmentChanged(align: String)
}