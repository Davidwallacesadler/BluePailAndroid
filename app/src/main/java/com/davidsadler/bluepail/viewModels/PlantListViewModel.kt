package com.davidsadler.bluepail.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.davidsadler.bluepail.model.Plant
import com.davidsadler.bluepail.model.PlantRepository
import com.davidsadler.bluepail.model.PlantRoomDatabase
import kotlinx.coroutines.launch

class PlantListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlantRepository
    val allPlants: LiveData<List<Plant>>

    init {
        val plantsDao = PlantRoomDatabase.getDatabase(
            application,
            viewModelScope
        ).plantDao()
        repository = PlantRepository(plantsDao)
        allPlants = repository.allPlants
    }

    fun insert(plant: Plant) = viewModelScope.launch {
        repository.insert(plant)
    }

    fun update(plant: Plant) = viewModelScope.launch {
        repository.update(plant)
    }

    fun delete(plant: Plant) {
        repository.delete(plant)
    }

    fun filterByColor(colorId: Int): LiveData<List<Plant>>{
        return repository.filterByColor(colorId)
    }
}