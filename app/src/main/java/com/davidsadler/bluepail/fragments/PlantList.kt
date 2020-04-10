package com.davidsadler.bluepail.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.adapters.OnItemClickedListener
import com.davidsadler.bluepail.adapters.PlantsAdapter
import com.davidsadler.bluepail.model.Plant
import com.davidsadler.bluepail.viewModels.PlantListViewModel
import com.davidsadler.bluepail.util.AlarmNotificationManager
import com.davidsadler.bluepail.util.MarginItemDecorator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_plant_list.*


class PlantList : Fragment(), OnItemClickedListener, PlantUpdatedListener {

    private lateinit var viewModel: PlantListViewModel
    private lateinit var adapter: PlantsAdapter
    private val args: PlantListArgs by navArgs()

    override fun onPlantUpdated(selectedPlant: Plant, status: PlantUpdateStatus) {
        when (status) {
            PlantUpdateStatus.Water -> {
                this.context?.let {
                    AlarmNotificationManager.scheduleNotificationAlarm(selectedPlant.name,
                        selectedPlant.id,
                        true,
                        selectedPlant.wateringDate,
                        it)
                    viewModel.update(selectedPlant)
                    Snackbar.make(view!!,R.string.snack_bar_watering_confirmation,Snackbar.LENGTH_SHORT).show()
                }
            }
            PlantUpdateStatus.Fertilize -> {
                this.context?.let {
                    AlarmNotificationManager.scheduleNotificationAlarm(selectedPlant.name,
                        selectedPlant.id,
                        false,
                        selectedPlant.fertilizerDate!!,
                        it)
                    viewModel.update(selectedPlant)
                    Snackbar.make(view!!,R.string.snack_bar_fertilizing_confirmation,Snackbar.LENGTH_SHORT).show()
                }
            }
            PlantUpdateStatus.Edit -> {
                val action = PlantListDirections.actionPlantListToPlantDetail(selectedPlant.id)
                findNavController().navigate(action)
            }
            PlantUpdateStatus.Delete -> {
                this.context?.let {
                    cancelAlarmNotification(selectedPlant)
                    viewModel.delete(selectedPlant)
                    Snackbar.make(view!!,R.string.snack_bar_deletion_confirmation,Snackbar.LENGTH_LONG)
                        .setAction(R.string.snack_bar_undo_action) {
                            viewModel.insert(selectedPlant)
                            scheduleAlarmNotification(selectedPlant)
                        }.show()
                }
            }
        }
    }

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
        observeAllPlants()
    }

    override fun onResume() {
        super.onResume()
        // TODO: Come up with a better way of refreshing plant list to show user plants need watered
        adapter.notifyDataSetChanged()
        if (args.colorToFilter != 0) {
            observeFilterPlants()
        } else {
            observeAllPlants()
        }
    }

    private fun setupRecyclerViewLayout() {
        this.context?.let { context ->
            recyclerView_plant_list.layoutManager = GridLayoutManager(context,2)
            val plantsListAdapter = PlantsAdapter(context,this)
            recyclerView_plant_list.adapter = plantsListAdapter
            adapter = plantsListAdapter
            recyclerView_plant_list.addItemDecoration(MarginItemDecorator(32))
        }
    }

    private fun setupViewModel() {
        this.activity?.let {
            viewModel = ViewModelProvider(it).get(PlantListViewModel::class.java)
        }
    }

    private fun observeAllPlants() {
        viewModel.allPlants.observe(viewLifecycleOwner, Observer {
             adapter.setPlants(it)
        })
    }

    private fun observeFilterPlants() {
        viewModel.filterByColor(args.colorToFilter).observe(this.activity!!, Observer {
            adapter.setPlants(it)
        })
    }

    private fun cancelAlarmNotification(selectedPlant: Plant) {
        this.context?.let {
            AlarmNotificationManager.cancelNotificationAlarm(selectedPlant.id,true,it)
            if (selectedPlant.fertilizerDate != null) {
                AlarmNotificationManager.cancelNotificationAlarm(selectedPlant.id,false,it)
            }
        }
    }

    private fun scheduleAlarmNotification(selectedPlant: Plant) {
        this.context?.let {
            AlarmNotificationManager.scheduleNotificationAlarm(
                selectedPlant.name,
                selectedPlant.id,
                true,
                selectedPlant.wateringDate,
                it
            )
            if (selectedPlant.fertilizerDate != null) {
                AlarmNotificationManager.scheduleNotificationAlarm(
                    selectedPlant.name,
                    selectedPlant.id,
                    false,
                    selectedPlant.fertilizerDate,
                    it
                )
            }
        }
    }
}
