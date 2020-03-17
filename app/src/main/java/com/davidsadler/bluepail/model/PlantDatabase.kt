package com.davidsadler.bluepail.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Plant::class], version = 1, exportSchema = false )
@TypeConverters(Converters::class)
abstract class PlantRoomDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao

    // use this callback to update
    private class PlantDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
//                    var plantDao = database.plantDao()
//                    val twoMinutes = 120
//                    val now = Calendar.getInstance().time
//                    val calendar = Calendar.getInstance()
//                    calendar.setTime(now)
//                    calendar.add(Calendar.SECOND, twoMinutes)
//                    val twoMinutesFromNow = calendar.time
//                    plantDao.deleteAll()
//                    val plant = SavedPlant(0,"Hello", Color.YELLOW, twoMinutesFromNow,1,null,null,null)
//                    val plant2 = SavedPlant(0, "World", Color.YELLOW, twoMinutesFromNow,1,null,null,null)
//                    plantDao.insert(plant)
//                    plantDao.insert(plant2)
                }
            }
        }
    }

    // SINGLETON OBJECT
    companion object {
        @Volatile
        // Our singleton instance:
        private var INSTANCE: PlantRoomDatabase? = null

        // Our method to return the singleton:
        fun getDatabase(context: Context, scope: CoroutineScope): PlantRoomDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, PlantRoomDatabase::class.java, "plant_table").addCallback(PlantDatabaseCallback(scope)).allowMainThreadQueries().build()
                INSTANCE = instance
                instance    // Returning instance...
            }
        }
    }
}