package com.davidsadler.bluepail.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

fun Bitmap.resize(width: Int, height: Int) : Bitmap {
    return Bitmap.createScaledBitmap(this,width,height,false)
}

fun Bitmap.rescale(scaleFactor: Int) : Bitmap {
    val bitmapHeight = this.height
    val bitmapWidth = this.width
    return Bitmap.createScaledBitmap(this,bitmapWidth.div(scaleFactor),bitmapHeight.div(scaleFactor),false)
}

fun Bitmap.compress(quality: Int) : Bitmap {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG,quality,stream)
    val byteArray = stream.toByteArray()
    return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
}

