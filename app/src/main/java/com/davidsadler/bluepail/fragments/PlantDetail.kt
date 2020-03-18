package com.davidsadler.bluepail.fragments

import android.os.Bundle
import android.view.*
import android.view.View.inflate
import androidx.fragment.app.Fragment

import com.davidsadler.bluepail.R
import kotlinx.android.synthetic.main.fragment_plant_detail.*

class PlantDetail : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plant_detail, container, false)
    }

    override fun onResume() {
        super.onResume()
        toolbar_cancel_save.inflateMenu(R.menu.detail_toolbar)
    }
}
