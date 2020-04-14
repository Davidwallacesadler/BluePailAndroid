package com.davidsadler.bluepail.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.adapters.ColorsAdapter
import com.davidsadler.bluepail.adapters.OnColorSelectedListener
import com.davidsadler.bluepail.util.fullUserScreenOrientation
import com.davidsadler.bluepail.util.lockScreenOrientation
import com.davidsadler.bluepail.viewModels.PlantListFilterViewModel
import kotlinx.android.synthetic.main.fragment_plant_list_filter.*

class PlantListFilterDialog : DialogFragment(), OnColorSelectedListener {

    private lateinit var colorsAdapter: ColorsAdapter
    private lateinit var viewModel:  PlantListFilterViewModel

    override fun colorSelected(colorId: Int) {
        viewModel.updateSelectedColor(colorId)
//        button_filter_by_color.text = when(colorId) {
//            R.color.colorGroupBlue ->
//        }
        button_filter_by_color.text = getString(R.string.menu_item_filter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(PlantListFilterViewModel::class.java)
        return inflater.inflate(R.layout.fragment_plant_list_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupColorRecyclerView()
        setupFilterButton()
    }

    override fun onResume() {
        super.onResume()
        activity?.lockScreenOrientation()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.fullUserScreenOrientation()
    }

    private fun setupColorRecyclerView() {
        recycer_view_color_filter.layoutManager = GridLayoutManager(context,
            1,
            GridLayoutManager.HORIZONTAL,
            false)
        context?.let { context ->
            colorsAdapter = ColorsAdapter(context,this)
            recycer_view_color_filter.adapter = colorsAdapter
            colorsAdapter.selectedColor = 0
        }
    }

    private fun setupFilterButton() {
        button_filter_by_color.setOnClickListener {
            val action = PlantListFilterDialogDirections.actionPlantListFilterDialogToPlantList(viewModel.getSelectedColorId())
            findNavController().navigate(action)
        }
    }
}
