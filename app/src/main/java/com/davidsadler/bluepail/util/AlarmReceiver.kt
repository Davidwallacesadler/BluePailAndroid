package com.davidsadler.bluepail.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.activities.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        println("BROADCAST RECEIVER ON RECEIVE CALLED")
        if (intent?.action == "android.intent.BOOT_COMPLETED") {
            // RESET THE ALARM HERE
            intent.let {
                val plantName = it.getStringExtra(EXTRA_NOTIFICATION_PLANT_NAME)
                val plantId = it.getIntExtra(EXTRA_NOTIFICATION_PLANT_ID,0)
                val notificationIsForWatering = it.getBooleanExtra(EXTRA_NOTIFICATION_IS_FOR_WATERING_BOOL,true)
                val notificationTitle = when (notificationIsForWatering) {
                    true -> "Time to Water!"
                    false -> "Time to Fertilize!"
                }
                val notificationContent = when (notificationIsForWatering) {
                    true -> "Water your $plantName"
                    false -> "Fertilize your $plantName"
                }
                val notificationIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent = if (notificationIsForWatering) {
                    PendingIntent.getActivity(context,plantId,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                } else {
                    PendingIntent.getActivity(context, plantId + 1000, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                val builder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationContent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setVibrate(longArrayOf(1000,1000))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                with(NotificationManagerCompat.from(context)) {
                    if (notificationIsForWatering) {
                        notify(plantId,builder.build())
                    } else {
                        notify(plantId + 1000,builder.build())
                    }
                }
            }
        } else {
            intent?.let {
                val plantName = it.getStringExtra(EXTRA_NOTIFICATION_PLANT_NAME)
                val plantId = it.getIntExtra(EXTRA_NOTIFICATION_PLANT_ID,0)
                val notificationIsForWatering = it.getBooleanExtra(EXTRA_NOTIFICATION_IS_FOR_WATERING_BOOL,true)
                val notificationTitle = when (notificationIsForWatering) {
                    true -> "Time to Water!"
                    false -> "Time to Fertilize!"
                }
                val notificationContent = when (notificationIsForWatering) {
                    true -> "Water your $plantName"
                    false -> "Fertilize your $plantName"
                }
                val notificationIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent = if (notificationIsForWatering) {
                    PendingIntent.getActivity(context,plantId,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                } else {
                    PendingIntent.getActivity(context, plantId + 1000, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                val builder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationContent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setVibrate(longArrayOf(1000,1000))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                with(NotificationManagerCompat.from(context)) {
                    if (notificationIsForWatering) {
                        notify(plantId,builder.build())
                    } else {
                        notify(plantId + 1000,builder.build())
                    }
                }
            }
        }
    }
}