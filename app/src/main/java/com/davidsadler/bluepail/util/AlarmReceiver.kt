package com.davidsadler.bluepail.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        println("On Receive called")
        if (intent?.action == "android.intent.BOOT_COMPLETED") {
            // TODO: Reset All alarms here
            // NEED TO HAVE ACCESS TO ALL THE REMINDERS SET or ALL THE PLANT OBJECTS
        } else {
            intent?.let {
                val plantName = it.getStringExtra(EXTRA_NOTIFICATION_PLANT_NAME)
                val plantId = it.getIntExtra(EXTRA_NOTIFICATION_PLANT_ID,0)
                val notificationIsForWatering = it.getBooleanExtra(EXTRA_NOTIFICATION_IS_FOR_WATERING_BOOL,true)
                NotificationHelper.createNotification(context!!,plantName,plantId,notificationIsForWatering)
            }
        }
    }
}