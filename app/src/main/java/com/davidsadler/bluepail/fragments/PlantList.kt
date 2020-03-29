package com.davidsadler.bluepail.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.adapters.OnItemClickedListener
import com.davidsadler.bluepail.adapters.PlantsAdapter
import com.davidsadler.bluepail.model.Plant
import com.davidsadler.bluepail.model.PlantViewModel
import com.davidsadler.bluepail.util.AlarmNotificationManager
import kotlinx.android.synthetic.main.fragment_plant_list.*


class PlantList : Fragment(), OnItemClickedListener, PlantUpdatedListener {

    override fun onPlantUpdated(selectedPlant: Plant, status: PlantUpdateStatus) {
        when (status) {
            PlantUpdateStatus.Water -> {
                // TODO: CHECK IF THERE IS ALREADY AN ALARM SCHEDULED
                AlarmNotificationManager.scheduleNotificationAlarm(selectedPlant.name,
                    selectedPlant.id,
                    true,
                    selectedPlant.wateringDate,
                    this.context!!)
                viewModel.update(selectedPlant)
                Toast.makeText(this.context!!,"Plant watering reminder re-scheduled",Toast.LENGTH_SHORT).show()
            }
            PlantUpdateStatus.Fertilize -> {
                // TODO: CHECK IF THERE IS ALREADY AN ALARM SCHEDULED
                AlarmNotificationManager.scheduleNotificationAlarm(selectedPlant.name,
                    selectedPlant.id,
                    false,
                    selectedPlant.fertilizerDate!!,
                    this.context!!)
                viewModel.update(selectedPlant)
                Toast.makeText(this.context!!,"Plant fertilizer reminder re-scheduled",Toast.LENGTH_SHORT).show()
            }
            PlantUpdateStatus.Edit -> {
                val plantId = selectedPlant.id
                println("passing Id : $plantId to plant detail fragment")
                val action = PlantListDirections.actionPlantListToPlantDetail(selectedPlant.id)
                Navigation.findNavController(this.view!!).navigate(action)
            }
            PlantUpdateStatus.Delete -> {
                // TODO: CANCEL NOTIFICATIONS BEFORE DELETION
                viewModel.delete(selectedPlant)
                Toast.makeText(this.context!!,"Plant deleted",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var viewModel: PlantViewModel
    private lateinit var adapter: PlantsAdapter

    override fun onItemClicked(selectedPlant: Plant) {
        val listDetailDialog = PlantListDialog(selectedPlant, this)
        listDetailDialog.show(this.activity!!.supportFragmentManager,"plant_list_detail_dialog")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupViewModel()
        return inflater.inflate(R.layout.fragment_plant_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViewLayout()
        setupPlantListObserver()
    }

    private fun setupRecyclerViewLayout() {
        this.context?.let { context ->
            recyclerView_plant_list.layoutManager = GridLayoutManager(context,2)
            val plantsListAdapter = PlantsAdapter(context,this)
            recyclerView_plant_list.adapter = plantsListAdapter
            adapter = plantsListAdapter
        }
    }

    private fun setupViewModel() {
        this.activity?.let {
            viewModel = ViewModelProvider(it).get(PlantViewModel::class.java)
        }
    }

    private fun setupPlantListObserver() {
        viewModel.allPlants.observe(this.activity!!, Observer { plants ->
            plants.let { adapter.setPlants(it) }
        })
    }
}
