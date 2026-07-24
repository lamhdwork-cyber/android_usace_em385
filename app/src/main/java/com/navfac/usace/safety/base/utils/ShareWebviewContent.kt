package com.navfac.usace.safety.base.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.MediaStore.Images
import android.view.View
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import java.io.*

private const val MAX_SHARE_TEXT_LENGTH = 100_000

fun share(context: Context, title: String, content: String) {
    if (content.length <= MAX_SHARE_TEXT_LENGTH) {
        shareAsText(context, title, content)
    } else {
        shareAsFile(context, title, content)
    }
}

private fun shareAsText(context: Context, title: String, content: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, content)
    }

    context.startActivity(Intent.createChooser(intent, title))
}

private fun shareAsFile(context: Context, title: String, content: String) {
    val directory = File(context.cacheDir, "shared").apply {
        mkdirs()
    }

    val safeTitle = title
        .replace(Regex("[^a-zA-Z0-9._-]"), "_")
        .take(60)
        .ifBlank { "shared_content" }

    val file = File(directory, "$safeTitle.txt")
    file.writeText(content, Charsets.UTF_8)

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_STREAM, uri)
        clipData = ClipData.newRawUri(title, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, title))
}

fun share(context: Context, title: String, content: ImageView) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "image/*"
    share.putExtra(Intent.EXTRA_STREAM, getImageUri(context, getBitmapFromView(content)!!))
    share.putExtra(Intent.EXTRA_TEXT, title)
    context.startActivity(Intent.createChooser(share, title))
}

fun getImageUri(context: Context, src: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    src.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = Images.Media.insertImage(context.contentResolver, src, "Title", null)
    return Uri.parse(path)
}

fun getBitmapFromView(view: View): Bitmap? {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

fun readHtmlFile(section: String, fileName: String, context: Context): String? {
    val returnString = StringBuilder()
    var fIn: InputStream? = null
    var isr: InputStreamReader? = null
    var input: BufferedReader? = null
    try {
        fIn = context.resources.assets
                .open(section + fileName)
        isr = InputStreamReader(fIn)
        input = BufferedReader(isr)
        var line: String? = ""
        while (input.readLine().also { line = it } != null) {
            returnString.append(line)
        }
    } catch (e: Exception) {
        e.message
    } finally {
        try {
            isr?.close()
            fIn?.close()
            input?.close()
        } catch (e2: Exception) {
            e2.message
        }
    }
    return HtmlCompat.fromHtml(returnString.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
}

 fun shareFileFromAsset(context: Context,assetName: String,filePath :String, intentAction: String) {
    try {
        val file = createFileInFilesDir(assetName)
        copyAssetToFile(context,assetName, filePath,file)
        val intent = createIntentForFile(context,file, intentAction)
        context.startActivity(Intent.createChooser(intent, assetName))
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
private fun createFileInFilesDir(filename: String): File {
    val file = File.createTempFile(filename.substringBeforeLast("."),".html")
    if (file.exists()) {
        if (!file.delete()) {
            throw IOException()
        }
    }
    if (!file.createNewFile()) {
        throw IOException()
    }
    return file
}
private fun createIntentForFile(context: Context,file: File,intentAction: String): Intent {
    val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
    val intent = Intent(intentAction)
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent.type = "text/html"
    return intent
}
private fun copyAssetToFile(context: Context,assetName: String,filePath:String, file: File) {
    val buffer = ByteArray(1024)
    val inputStream = context.resources.assets.open(filePath+assetName)
    val outputStream: OutputStream = FileOutputStream(file)
    while (inputStream.read(buffer) > 0) {
        outputStream.write(buffer)
    }
}