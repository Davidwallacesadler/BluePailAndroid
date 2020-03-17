package com.davidsadler.bluepail.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "plant_table")
data class Plant(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") val name: String,
    @ColumnInfo(name = "color_id") val colorId: Int,
    @ColumnInfo(name = "needs_watered_fire_date") val wateringDate: Date,
    @ColumnInfo(name = "watering_day_interval") val daysBetweenWatering: Int,
    @ColumnInfo(name = "needs_fertilized_fire_date") val fertilizerDate: Date?,
    @ColumnInfo(name = "fertilizer_day_interval") val daysBetweenFertilizing: Int?,
    @ColumnInfo(name = "photo") val photo: String?
)