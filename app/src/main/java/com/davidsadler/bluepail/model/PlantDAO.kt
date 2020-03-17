package com.davidsadler.bluepail.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlantDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plant: Plant)

    // READ
    @Query("SELECT * FROM plant_table")
    fun getAllPlants(): LiveData<List<Plant>>

    @Query("SELECT * FROM plant_table WHERE id IS (:savedPlantIds)")
    fun loadAllById(savedPlantIds: IntArray): LiveData<List<Plant>>

    @Query("SELECT * FROM plant_table WHERE id = :savedPlantId")
    fun findByID(savedPlantId: Int): LiveData<Plant>

    // UPDATE
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(plant: Plant)

    // DELETE
    @Delete
    fun delete(plant: Plant)

    @Query("DELETE FROM plant_table")
    suspend fun deleteAll()

}