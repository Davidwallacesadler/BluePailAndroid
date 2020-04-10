package com.davidsadler.bluepail.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.davidsadler.bluepail.R

class AppSettings : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_settings, container, false)
    }

    override fun onResume() {
        super.onResume()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
    }
}
