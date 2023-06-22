package com.rebalance.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getToday(): String {
    return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

fun compressImage(originalImage: Bitmap?, context: Context): Bitmap? {
    if (originalImage == null) {
        return null
    }
    val outputStream = ByteArrayOutputStream()
    originalImage.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
    val filePath = context.cacheDir.absolutePath + "/compressed.jpg"
    val file = File(filePath)
    try {
        val fos = FileOutputStream(file)
        fos.write(outputStream.toByteArray())
        fos.flush()
        fos.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return BitmapFactory.decodeFile(filePath)
}
