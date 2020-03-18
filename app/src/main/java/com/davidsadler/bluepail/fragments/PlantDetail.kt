package com.davidsadler.bluepail.fragments

import android.os.Bundle
import android.view.*
import android.view.View.inflate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.adapters.ColorsAdapter
import com.davidsadler.bluepail.adapters.OnColorSelectedListener
import kotlinx.android.synthetic.main.fragment_plant_detail.*

class PlantDetail : Fragment(), OnColorSelectedListener {

    var selectedColor = 0

    override fun colorSelected(colorId: Int) {
        selectedColor = colorId
        println("Selected Color: $selectedColor")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plant_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateBottomToolbar()
        setupColorRecyclerView()
    }


    private fun inflateBottomToolbar() {
        toolbar_cancel_save.inflateMenu(R.menu.detail_toolbar)
    }

    private fun setupColorRecyclerView() {
        this.context?.let { context ->
            recyclerView_plant_colors.layoutManager = GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
            recyclerView_plant_colors.adapter = ColorsAdapter(context,this)
        }
    }
}
