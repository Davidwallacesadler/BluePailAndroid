package com.davidsadler.bluepail.util

import android.content.pm.ActivityInfo
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.lockScreenOrientation() {
    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
}

fun FragmentActivity.fullUserScreenOrientation() {
    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
}