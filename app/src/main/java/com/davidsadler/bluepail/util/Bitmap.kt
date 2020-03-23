package com.davidsadler.bluepail.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun Bitmap.resize(width: Int, height: Int) : Bitmap {
    return Bitmap.createScaledBitmap(this,width,height,false)
}

fun Bitmap.compress(quality: Int) : Bitmap {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG,quality,stream)
    val byteArray = stream.toByteArray()
    return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
}