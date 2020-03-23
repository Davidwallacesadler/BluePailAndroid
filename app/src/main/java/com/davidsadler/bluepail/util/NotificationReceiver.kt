package com.davidsadler.bluepail.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.activities.MainActivity

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            println("BROADCAST RECEIVER ON RECEIVE CALLED")
            val plantName = it.getStringExtra(EXTRA_NOTIFICATION_PLANT_NAME)
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
            val pendingIntent = PendingIntent.getActivity(context,0,notificationIntent,0)
            val builder = NotificationCompat.Builder(context!!, "notifyBluePail")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(longArrayOf(1000,1000))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            with(NotificationManagerCompat.from(context)) {
                notify(200,builder.build())
            }
        }
    }
}