package com.davidsadler.bluepail.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.davidsadler.bluepail.model.Plant
import com.davidsadler.bluepail.model.PlantRepository
import com.davidsadler.bluepail.model.PlantRoomDatabase
import com.davidsadler.bluepail.util.getDateAtDesiredTime
import kotlinx.coroutines.launch
import java.util.*

class PlantCreationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlantRepository

    init {
        val plantsDao = PlantRoomDatabase.getDatabase(
            application,
            viewModelScope
        ).plantDao()
        repository = PlantRepository(plantsDao)
    }

    private var id: Int = 0
    private var name: String = ""
    private var colorId: Int = -1754827
    private var wateringDate: Date? = null
    private var wateringInterval: Int? = null
    private var fertilizingDate: Date? = null
    private var fertilizingInterval: Int? = null
    private var photoUri: String? = null

    fun getId(): Int {
        return id
    }

    fun getName(): String {
        return name
    }

    fun getColorId(): Int {
        return colorId
    }

    fun getWateringDate(): Date? {
        return wateringDate
    }

    fun getWateringInterval(): Int? {
        return wateringInterval
    }

    fun getFertilizingDate(): Date? {
        return fertilizingDate
    }

    fun getFertilizingInterval(): Int? {
        return fertilizingInterval
    }

    fun getPhotoUri(): String? {
        return photoUri
    }

    fun getReminderIntervalInDays(isWateringReminder: Boolean): String {
        return when (val interval = if (isWateringReminder) wateringInterval!! else fertilizingInterval!!) {
            1 -> "1 Day"
            else -> "$interval Days"
        }
    }

    fun setPlantId(id: Int) {
        this.id = id
    }

    fun setPlantName(name: String) {
        this.name = name
    }

    fun setColorId(colorId: Int) {
        this.colorId = colorId
    }

    fun setWateringDate(wateringDate: Date) {
        this.wateringDate = wateringDate
    }

    fun setWateringTime(hour: Int, minute: Int) {
        println("Setting Waterin time to $hour : $minute")
        this.wateringDate = wateringDate?.getDateAtDesiredTime(hour,minute)
    }

    fun setWateringInterval(wateringInterval: Int) {
        this.wateringInterval = wateringInterval
    }

    fun setFertilizingDate(fertilizingDate: Date?) {
        this.fertilizingDate = fertilizingDate
    }

    fun setFertilizingTime(hour: Int, minute: Int) {
        if (fertilizingDate != null) {
            this.fertilizingDate = fertilizingDate!!.getDateAtDesiredTime(hour,minute)
        }
    }

    fun setFertilizingInterval(fertilizingInterval: Int?) {
        this.fertilizingInterval = fertilizingInterval
    }

    fun setPhotoUri(photoUri: String?) {
        this.photoUri = photoUri
    }

    fun findById(plantId: Int) : LiveData<Plant> {
        return repository.findById(plantId)
    }

    private fun insert(plant: Plant) = viewModelScope.launch {
        repository.insert(plant)
    }

    private fun update(plant: Plant) = viewModelScope.launch {
        repository.update(plant)
    }

    fun savePlant() {
        val plant = Plant(
            id,
            name,
            colorId,
            wateringDate!!,
            wateringInterval!!,
            fertilizingDate,
            fertilizingInterval,
            photoUri
        )
        if (id != 0) {
            update(plant)
        } else {
            insert(plant)
        }
    }
}