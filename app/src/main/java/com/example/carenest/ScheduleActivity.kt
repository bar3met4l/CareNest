package com.example.carenest

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.carenest.adapter.ScheduleAdapter
import com.example.carenest.data.BabyDatabase
import com.example.carenest.model.Schedule
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleActivity : AppCompatActivity() {
    private lateinit var scheduleListView: ListView
    private lateinit var fab: FloatingActionButton
    private lateinit var database: BabyDatabase
    private var babyId: Int = -1
    private lateinit var babyName: String
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_schedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createNotificationChannel()
        database=
            Room.databaseBuilder(applicationContext, BabyDatabase::class.java, "baby_database").build()
        fab = findViewById(R.id.fab)
        scheduleListView = findViewById(R.id.scheduleListView)
        babyId = intent.getIntExtra("baby_id", -1)
        babyName = intent.getStringExtra("baby_name") ?: "Unknown"
        loadSchedules()

        fab.setOnClickListener {
            showAddScheduleDialog()
        }


    }
    private fun loadSchedules() {
        database.scheduleDao().getSchedulesForBaby(babyName).observe(this) { schedules ->
            val adapter = ScheduleAdapter(this, schedules, database)
            scheduleListView.adapter = adapter
        }
    }
    private fun showAddScheduleDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_schedule)

        val activityEditText: EditText = dialog.findViewById(R.id.activityEditText)
        val timePicker: TimePicker = dialog.findViewById(R.id.timePicker)
        val saveButton: Button = dialog.findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val activityName = activityEditText.text.toString()
            val hour = timePicker.hour
            val minute = timePicker.minute

            if (activityName.isNotBlank()) {
                val schedule = Schedule(
                    babyId = babyId,
                    babyName = babyName,
                    activity = activityName,
                    hour = hour,
                    minute = minute
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    database.scheduleDao().insert(schedule)
                }

                // Schedule notification
                scheduleNotification(activityName, hour, minute)

                dialog.dismiss()
            } else {
                // Show an error message if activity name is blank
            }
        }

        dialog.show()
    }

    private fun scheduleNotification(activityName: String, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("activity_name", activityName)
            putExtra("baby_name", babyName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, (babyId * 1000 + hour * 60 + minute)
                ,intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d(TAG, "scheduleNotification: ${calendar.timeInMillis}")
        val triggerTime = calendar.timeInMillis
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Schedule Channel"
            val descriptionText = "Channel for baby schedule notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("schedule_channel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}