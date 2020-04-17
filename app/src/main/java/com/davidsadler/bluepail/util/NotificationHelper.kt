package com.davidsadler.bluepail.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.activities.MainActivity

object NotificationHelper {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NOTIFICATION_CHANNEL_NAME
            val descriptionText = NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(context: Context,
                           plantName: String,
                           plantId: Int,
                           isWateringNotification: Boolean) {
        val notificationTitle = when (isWateringNotification) {
            true -> "Time to Water!"
            false -> "Time to Fertilize!"
        }
        val notificationContent = when (isWateringNotification) {
            true -> "Water your $plantName"
            false -> "Fertilize your $plantName"
        }
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = if (isWateringNotification) {
            PendingIntent.getActivity(context,plantId,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            PendingIntent.getActivity(context, plantId + 1000, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val iconColor = if (Build.VERSION.SDK_INT >= 23) {
            context.getColor(R.color.colorPrimary)
        } else {
            Color.BLUE
        }
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setColorized(true)
            .setColor(iconColor)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setGroup(NOTIFICATION_GROUP_REMINDERS)
            .setVibrate(longArrayOf(1000,1000))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            if (isWateringNotification) {
                notify(plantId,builder.build())
            } else {
                notify(plantId + 1000,builder.build())
            }
        }
    }
}