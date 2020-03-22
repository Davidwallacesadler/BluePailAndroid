package com.davidsadler.bluepail.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun Bitmap.resizeBitmap(width: Int, height: Int) : Bitmap {
    return Bitmap.createScaledBitmap(this,width,height,false)
}

fun Bitmap.compressBitMap(bitmap: Bitmap, quality: Int) : Bitmap {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream)
    val byteArray = stream.toByteArray()
    return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
}