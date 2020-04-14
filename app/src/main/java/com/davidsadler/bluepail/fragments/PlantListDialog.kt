package com.davidsadler.bluepail.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.model.Plant
import com.davidsadler.bluepail.util.*
import kotlinx.android.synthetic.main.fragment_plant_list_dialog.*
import java.util.*

class PlantListDialog(private val selectedPlant: Plant, private val plantUpdatedListener: PlantUpdatedListener) : DialogFragment() {

    private var deleteWasPressed = false
    private val deleteStatus = "delete_bool"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plant_list_dialog, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(deleteStatus,deleteWasPressed)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlantTitleTextView()
        setupWaterClickListener()
        setupFertilizerClickListener()
        setupEditClickListener()
        setupDeleteClickListener()
        checkSavedInstanceState(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        activity?.lockScreenOrientation()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.fullUserScreenOrientation()
    }

    private fun updateDeleteButtonIfNeeded() {
        if (deleteWasPressed) {
            button_delete_plant.setBackgroundColor(Color.RED)
        }
    }

    private fun setupPlantTitleTextView() {
        textView_plant_title_list_dialog.text = selectedPlant.name
    }

    private fun setupWaterClickListener() {
        button_water_plant.setOnClickListener {
            val wateringHour = selectedPlant.wateringDate.getHour(false)
            val wateringMinute = selectedPlant.wateringDate.getMinute()
            val todayAtCorrectTime = Date().getDateAtDesiredTime(wateringHour,wateringMinute)
            val newWateringFireDate = todayAtCorrectTime.getDateAmountOfDaysAway(selectedPlant.daysBetweenWatering)
            val updatePlant = Plant(selectedPlant.id,
                selectedPlant.name,
                selectedPlant.colorId,
                newWateringFireDate,
                selectedPlant.daysBetweenWatering,
                selectedPlant.fertilizerDate,
                selectedPlant.daysBetweenFertilizing,
                selectedPlant.photo)
            plantUpdatedListener.onPlantUpdated(updatePlant, PlantUpdateStatus.Water)
            dismiss()
        }
    }

    private fun setupFertilizerClickListener() {
        button_fertilize_plant.setOnClickListener {
            if (selectedPlant.fertilizerDate != null && selectedPlant.daysBetweenFertilizing != null) {
                val fertilizingHour = selectedPlant.fertilizerDate.getHour(false)
                val fertilizingMinute = selectedPlant.fertilizerDate.getMinute()
                val todayAtCorrectTime = Date().getDateAtDesiredTime(fertilizingHour,fertilizingMinute)
                val newFertilizingDate = todayAtCorrectTime.getDateAmountOfDaysAway(selectedPlant.daysBetweenFertilizing)
                val updatePlant = Plant(selectedPlant.id,
                    selectedPlant.name,
                    selectedPlant.colorId,
                    selectedPlant.wateringDate,
                    selectedPlant.daysBetweenWatering,
                    newFertilizingDate,
                    selectedPlant.daysBetweenFertilizing,
                    selectedPlant.photo)
                plantUpdatedListener.onPlantUpdated(updatePlant, PlantUpdateStatus.Fertilize)
            } else {
                context?.let { context ->
                    Toast.makeText(context,"No fertilizer reminders set for this plant", Toast.LENGTH_SHORT).show()
                }
            }
            this.dismiss()
        }
    }

    private fun setupEditClickListener() {
        button_edit_plant.setOnClickListener {
            plantUpdatedListener.onPlantUpdated(selectedPlant, PlantUpdateStatus.Edit)
            this.dismiss()
        }
    }

    private fun setupDeleteClickListener() {
        button_delete_plant.setOnClickListener {
            if (deleteWasPressed) {
                plantUpdatedListener.onPlantUpdated(selectedPlant, PlantUpdateStatus.Delete)
                dismiss()
            } else {
                val deleteRed = Color.RED
                button_delete_plant.setBackgroundColor(deleteRed)
                deleteWasPressed = true
            }
        }
    }

    private fun checkSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val deleteStatus = savedInstanceState.getBoolean(deleteStatus)
            deleteWasPressed = deleteStatus
            updateDeleteButtonIfNeeded()
        }
    }
}

interface PlantUpdatedListener {
    fun onPlantUpdated(selectedPlant: Plant, status: PlantUpdateStatus)
}

enum class PlantUpdateStatus {
    Water, Fertilize, Delete, Edit
}
