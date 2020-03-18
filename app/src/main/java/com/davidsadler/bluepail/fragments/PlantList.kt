package com.davidsadler.bluepail.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager

import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.adapters.OnItemClickedListener
import com.davidsadler.bluepail.adapters.PlantsAdapter
import com.davidsadler.bluepail.model.Plant
import kotlinx.android.synthetic.main.fragment_plant_list.*


class PlantList : Fragment(), OnItemClickedListener {

    override fun onItemClicked(selectedPlant: Plant) {
        println("Plant List Cell Tapped...")
        this.view?.let { fragmentView ->
            val navController = Navigation.findNavController(fragmentView)
            navController.navigate(R.id.plantDetail)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plant_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViewLayout()
    }

    private fun setupRecyclerViewLayout() {
        this.context?.let { context ->
            recyclerView_plant_list.layoutManager = GridLayoutManager(context,2)
            recyclerView_plant_list.adapter = PlantsAdapter(context,this)
        }
    }
}
