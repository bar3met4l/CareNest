package com.example.carenest

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val activityName = intent.getStringExtra("activity_name")
        val babyName = intent.getStringExtra("baby_name")
        // Custom sound for notification
        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, "schedule_channel")
            .setSmallIcon(R.drawable.baseline_alarm_24) // Replace with your notification icon
            .setContentTitle("Schedule Reminder")
            .setContentText("$activityName for $babyName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)){
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
               ActivityCompat.requestPermissions(context as MainActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
            notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        }

    }
}
