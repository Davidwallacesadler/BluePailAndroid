package com.davidsadler.bluepail.fragments

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.model.Plant
import com.davidsadler.bluepail.util.getDateAmountOfDaysAway
import com.davidsadler.bluepail.util.getDateAtDesiredTime
import com.davidsadler.bluepail.util.getHour
import com.davidsadler.bluepail.util.getMinute
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_plant_list_dialog.*
import java.util.*

class PlantListDialog(private val selectedPlant: Plant, private val plantUpdatedListener: PlantUpdatedListener) : DialogFragment() {

    private var deleteWasPressed = false
    private var deleteStatus = "delete_bool"

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
        if (savedInstanceState != null) {
            val deleteStatus = savedInstanceState.getBoolean(deleteStatus)
            deleteWasPressed = deleteStatus
            updateDeleteButtonIfNeeded()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        return super.onCreateDialog(savedInstanceState)
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
            // TODO: Need to check if there is already an alarm scheduled as well
            val updatePlant = Plant(selectedPlant.id,
                selectedPlant.name,
                selectedPlant.colorId,
                newWateringFireDate,
                selectedPlant.daysBetweenWatering,
                selectedPlant.fertilizerDate,
                selectedPlant.daysBetweenFertilizing,
                selectedPlant.photo)
            plantUpdatedListener.onPlantUpdated(updatePlant, PlantUpdateStatus.Water)
            this.dismiss()
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
                this.context?.let {
                    Toast.makeText(it,"No fertilizer reminders set for this plant", Toast.LENGTH_SHORT).show()
                }
            }
            this.dismiss()
        }
    }

    private fun setupEditClickListener() {
        button_edit_plant.setOnClickListener {
            // navigate to plant detail and pass the plant id as a safeArg
            println("$selectedPlant")
            plantUpdatedListener.onPlantUpdated(selectedPlant, PlantUpdateStatus.Edit)
            this.dismiss()
        }
    }

    private fun setupDeleteClickListener() {
        button_delete_plant.setOnClickListener {
            if (deleteWasPressed) {
                plantUpdatedListener.onPlantUpdated(selectedPlant, PlantUpdateStatus.Delete)
                this.dismiss()
            } else {
                //Toast.makeText(this.context!!, "Press delete again to confirm deletion",Toast.LENGTH_SHORT).show()
                val deleteRed = Color.RED
                button_delete_plant.setBackgroundColor(deleteRed)
                deleteWasPressed = true
            }
        }
    }
}

interface PlantUpdatedListener {
    fun onPlantUpdated(selectedPlant: Plant, status: PlantUpdateStatus)
}

enum class PlantUpdateStatus {
    Water, Fertilize, Delete, Edit
}
