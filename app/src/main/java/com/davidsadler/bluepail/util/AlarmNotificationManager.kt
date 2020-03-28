package com.davidsadler.bluepail.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.util.*

class AlarmNotificationManager {

    companion object {
        // Schedule:
        fun scheduleNotification(plantName: String,
                                 plantId: Int,
                                 isWateringNotification: Boolean,
                                 fireDate: Date,
                                 context: Context) {
            val intent = Intent(context, NotificationReceiver::class.java)
            intent.putExtra(EXTRA_NOTIFICATION_PLANT_NAME, plantName)
            intent.putExtra(EXTRA_NOTIFICATION_IS_FOR_WATERING_BOOL,isWateringNotification)
            intent.putExtra(EXTRA_NOTIFICATION_PLANT_ID, plantId)
            val dataURI = Uri.parse("$plantId/$isWateringNotification")
            intent.data = dataURI
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = if (isWateringNotification) {
                PendingIntent.getBroadcast(context,plantId,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                //TODO: FIGURE OUT A BETTER WAY OF DIFFERENTIATING FERTILIZER AND WATERING PENDING INTENTS
                PendingIntent.getBroadcast(context,plantId + 1000,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            }
            println("Scheduling alarm for $plantName")
            //val pendingIntent = PendingIntent.getBroadcast(context,plantId,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, fireDate.timeInMillis(), pendingIntent)
        }

        // Cancel:
        fun cancelNotification(plantId: Int,
                               isWateringNotification: Boolean,
                               context: Context) {
            val intent = Intent(context, NotificationReceiver::class.java)
            val dataUri = Uri.parse("$plantId/$isWateringNotification")
            intent.data = dataUri
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = if (isWateringNotification) {
                PendingIntent.getBroadcast(context,plantId,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                //TODO: FIGURE OUT A BETTER WAY OF DIFFERENTIATING FERTILIZER AND WATERING PENDING INTENTS
                PendingIntent.getBroadcast(context,plantId + 1000,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            }
            println("Canceling alarm for plant id: $plantId")
            alarmManager.cancel(pendingIntent)
        }
    }
}