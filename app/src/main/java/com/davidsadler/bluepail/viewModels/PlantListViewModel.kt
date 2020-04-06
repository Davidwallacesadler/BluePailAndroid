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

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: PlantRepository
    // LiveData gives us updated words when they change.
    val allPlants: LiveData<List<Plant>>

    init {
        // Gets reference to PlantDao from PlantRoomDatabase to construct
        // the correct Repository.
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
}