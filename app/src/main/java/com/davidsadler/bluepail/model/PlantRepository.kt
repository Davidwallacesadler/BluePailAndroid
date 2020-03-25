package com.davidsadler.bluepail.model

import androidx.lifecycle.LiveData

class PlantRepository(private val plantDao: PlantDao) {

    // Room executes all queries on a separate thread
    // Observed Live Data will notify the observer when the data has changed.
    val allPlants: LiveData<List<Plant>> = plantDao.getAllPlants()


    // suspend: this modifier tells the compiler to call this from a co-routine or another
    // suspending function.
    suspend fun insert(plant: Plant) = plantDao.insert(plant)

    fun findById(plantId: Int) = plantDao.findByID(plantId)

    fun update(plant: Plant) = plantDao.update(plant)

    fun delete(plant: Plant) = plantDao.delete(plant)

    suspend fun deleteAll() = plantDao.deleteAll()

    companion object {
        @Volatile private var instance: PlantRepository? = null
        fun getInstance(plantDao: PlantDao) = instance?: synchronized(this) { instance ?: PlantRepository(plantDao). also { instance = it }}
    }
}