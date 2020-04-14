package com.davidsadler.bluepail.fragments

import android.content.Context
import android.graphics.Color
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
import com.davidsadler.bluepail.util.RecyclerViewLayoutType
import com.davidsadler.bluepail.util.SHARED_PREF_DARK_MODE_BOOL
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_plant_list.*


class PlantList : Fragment(), OnItemClickedListener, PlantUpdatedListener {

    private lateinit var viewModel: PlantListViewModel
    private lateinit var adapter: PlantsAdapter
    private val args: PlantListArgs by navArgs()

    override fun onPlantUpdated(selectedPlant: Plant, status: PlantUpdateStatus) {
        when (status) {
            PlantUpdateStatus.Water -> {
                context?.let { context ->
                    AlarmNotificationManager.scheduleNotificationAlarm(selectedPlant.name,
                        selectedPlant.id,
                        true,
                        selectedPlant.wateringDate,
                        context)
                    viewModel.update(selectedPlant)
                    Snackbar.make(view!!,R.string.snack_bar_watering_confirmation,Snackbar.LENGTH_SHORT).show()
                }
            }
            PlantUpdateStatus.Fertilize -> {
                context?.let { context ->
                    AlarmNotificationManager.scheduleNotificationAlarm(selectedPlant.name,
                        selectedPlant.id,
                        false,
                        selectedPlant.fertilizerDate!!,
                        context)
                    viewModel.update(selectedPlant)
                    view?.let {view ->
                        Snackbar.make(view,R.string.snack_bar_fertilizing_confirmation,Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            PlantUpdateStatus.Edit -> {
                val action = PlantListDirections.actionPlantListToPlantDetail(selectedPlant.id)
                findNavController().navigate(action)
            }
            PlantUpdateStatus.Delete -> {
                cancelAlarmNotification(selectedPlant)
                viewModel.delete(selectedPlant)
                view?.let {view ->
                    Snackbar.make(view,R.string.snack_bar_deletion_confirmation,Snackbar.LENGTH_LONG)
                        .setAction(R.string.snack_bar_undo_action) {
                            viewModel.insert(selectedPlant)
                            scheduleAlarmNotification(selectedPlant)
                        }.show()
                }
            }
        }
    }

    override fun onItemClicked(selectedPlant: Plant) {
        activity?.let { activity ->
            val listDetailDialog = PlantListDialog(selectedPlant, this)
            listDetailDialog.show(activity.supportFragmentManager,"plant_list_detail_dialog")
        }
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
        setListBackgroundToBlackIfDarkMode()
        setupRecyclerViewLayout()
        observeAllPlants()
    }

    override fun onResume() {
        super.onResume()
        // TODO: Come up with a better way of refreshing plant list to show user plants need watered
        filterPlantsIfNeeded()
    }

    private fun setupRecyclerViewLayout() {
        context?.let { context ->
            recyclerView_plant_list.layoutManager = GridLayoutManager(context,2)
            val plantsListAdapter = PlantsAdapter(context,this)
            recyclerView_plant_list.adapter = plantsListAdapter
            adapter = plantsListAdapter
            recyclerView_plant_list.addItemDecoration(MarginItemDecorator(32, RecyclerViewLayoutType.GRID))
        }
    }

    private fun setupViewModel() {
        activity?.let { activity ->
            viewModel = ViewModelProvider(activity).get(PlantListViewModel::class.java)
        }
    }

    private fun observeAllPlants() {
        viewModel.allPlants.observe(viewLifecycleOwner, Observer { allPlants ->
             adapter.setPlants(allPlants)
        })
    }

    private fun observeFilterPlants() {
        viewModel.filterByColor(args.colorToFilter).observe(viewLifecycleOwner, Observer { filteredPlants ->
            adapter.setPlants(filteredPlants)
        })
    }

    private fun cancelAlarmNotification(selectedPlant: Plant) {
        context?.let { context ->
            AlarmNotificationManager.cancelNotificationAlarm(selectedPlant.id,
                selectedPlant.name,
                true,
                context)
            if (selectedPlant.fertilizerDate != null) {
                AlarmNotificationManager.cancelNotificationAlarm(selectedPlant.id,
                    selectedPlant.name,
                    false,
                    context)
            }
        }
    }

    private fun scheduleAlarmNotification(selectedPlant: Plant) {
        context?.let { context ->
            AlarmNotificationManager.scheduleNotificationAlarm(
                selectedPlant.name,
                selectedPlant.id,
                true,
                selectedPlant.wateringDate,
                context
            )
            if (selectedPlant.fertilizerDate != null) {
                AlarmNotificationManager.scheduleNotificationAlarm(
                    selectedPlant.name,
                    selectedPlant.id,
                    false,
                    selectedPlant.fertilizerDate,
                    context
                )
            }
        }
    }

    private fun setListBackgroundToBlackIfDarkMode() {
        activity?.let { activity ->
            val sharedPrefs = activity.getPreferences(Context.MODE_PRIVATE)
            if (sharedPrefs.getBoolean(SHARED_PREF_DARK_MODE_BOOL, false)) {
                recyclerView_plant_list.setBackgroundColor(Color.BLACK)
            }
        }
    }

    private fun filterPlantsIfNeeded() {
        if (args.colorToFilter != 1) {
            when (args.colorToFilter) {
                0 -> observeAllPlants()
                else -> observeFilterPlants()
            }
        }
    }
}
