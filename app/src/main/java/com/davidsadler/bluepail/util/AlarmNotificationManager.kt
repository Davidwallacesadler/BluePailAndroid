package com.davidsadler.bluepail.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.davidsadler.bluepail.R
import java.util.*

object AlarmNotificationManager {

    private fun createPendingIntent(context: Context,
                                    plantName: String,
                                    plantId: Int,
                                    isWateringNotification: Boolean): PendingIntent? {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = context.getString(R.string.notification_action_plant_reminder)
            type = "$plantName-$plantId-$isWateringNotification"
            putExtra(EXTRA_NOTIFICATION_PLANT_NAME, plantName)
            putExtra(EXTRA_NOTIFICATION_PLANT_ID, plantId)
            putExtra(EXTRA_NOTIFICATION_IS_FOR_WATERING_BOOL, isWateringNotification)
        }
        return if (isWateringNotification) {
            PendingIntent.getBroadcast(context,plantId,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            PendingIntent.getBroadcast(context,plantId + 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    fun scheduleNotificationAlarm(plantName: String,
                                  plantId: Int,
                                  isWateringNotification: Boolean,
                                  fireDate: Date,
                                  context: Context) {
        val receiver = ComponentName(context, AlarmReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = createPendingIntent(context,plantName,plantId,isWateringNotification)
        alarmManager.set(AlarmManager.RTC_WAKEUP, fireDate.timeInMillis(), alarmIntent)
        println("Scheduling alarm for $plantName")
    }


        //TODO: TEST CANCELING OF ALARM
    fun cancelNotificationAlarm(plantId: Int,
                                plantName: String,
                                isWateringNotification: Boolean,
                                context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = createPendingIntent(context,plantName,plantId,isWateringNotification)
        alarmManager.cancel(alarmIntent)
        println("Canceling alarm for $plantName")
    }
}