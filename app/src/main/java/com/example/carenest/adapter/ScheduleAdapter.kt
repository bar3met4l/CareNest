package com.example.carenest.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.carenest.R
import com.example.carenest.data.BabyDatabase
import com.example.carenest.model.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScheduleAdapter(
    private val context: Context,
    private val scheduleList: List<Schedule>,
    private val database: BabyDatabase
) : BaseAdapter() {

    override fun getCount(): Int = scheduleList.size

    override fun getItem(position: Int): Any = scheduleList[position]

    override fun getItemId(position: Int): Long = scheduleList[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false)

        val schedule = scheduleList[position]
        val activityTextView: TextView = view.findViewById(R.id.activityTextView)
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)

        activityTextView.text = schedule.activity
        timeTextView.text = "Time: ${schedule.hour}:${schedule.minute}"

        deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Schedule")
                .setMessage("Are you sure you want to delete this schedule?")
                .setPositiveButton("Yes") { _, _ ->
                    // Delete schedule from the database
                    (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.IO) {
                        database.scheduleDao().delete(schedule)
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
        return view
    }
}
