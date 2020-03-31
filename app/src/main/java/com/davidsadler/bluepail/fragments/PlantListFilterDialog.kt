package com.davidsadler.bluepail.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.davidsadler.bluepail.R

class PlantListFilterDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plant_list_filter, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        return super.onCreateDialog(savedInstanceState)
    }
}
