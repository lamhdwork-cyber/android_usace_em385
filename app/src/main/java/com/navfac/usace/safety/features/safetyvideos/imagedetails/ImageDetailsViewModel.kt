package com.navfac.usace.safety.features.safetyvideos.imagedetails

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import com.github.chrisbanes.photoview.PhotoView
import com.navfac.usace.safety.base.platform.BaseViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ImageDetailsViewModel : BaseViewModel() {

    fun saveImageToGallery(context: Context, fileName: String, photoView: PhotoView) {
        val bitmapDrawable = photoView.drawable as BitmapDrawable
        val bitmap = bitmapDrawable.bitmap
        val filename = String.format("%s.png", fileName)
        if (Build.VERSION.SDK_INT >= 29) {
            val values = contentValues(filename)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + "USACE")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri), context, fileName)
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "USACE")
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, filename)
            saveImageToStream(bitmap, FileOutputStream(file), context, fileName)
            if (file.absolutePath != null) {
                val values = contentValues(filename)
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }
    }

    private fun contentValues(filename: String): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?, context: Context, fileName: String) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()

                val builder = AlertDialog.Builder(context)
                builder.setTitle("File Saved")
                builder.setCancelable(false)
                builder.setMessage("Would you like to go to USACE folder?")
                builder.setPositiveButton("Yes") { _, _ ->
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        val uri = Uri.parse(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + "/USACE")
                        intent.setDataAndType(uri, "image/*")
                        context.startActivity(Intent.createChooser(intent, "Open folder"))
                    }, 500)
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}