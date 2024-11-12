package com.example.carenest.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import com.example.carenest.R
import com.example.carenest.model.Baby
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BabyAdapter(private val context: Context, private val babyList: List<Baby>) : BaseAdapter() {

    override fun getCount(): Int = babyList.size

    override fun getItem(position: Int): Any = babyList[position]

    override fun getItemId(position: Int): Long = babyList[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_baby_card, parent, false)

        val baby = babyList[position]

        // Access views in the layout
        val babyImageView: ImageView = view.findViewById(R.id.babyImageView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val dobTextView: TextView = view.findViewById(R.id.dobTextView)
        val genderTextView: TextView = view.findViewById(R.id.genderTextView)
        val healthNoteTextView: TextView = view.findViewById(R.id.healthNoteTextView)
        val emergencyContactTextView: TextView = view.findViewById(R.id.emergencyContactTextView)
        val ageTextView: TextView = view.findViewById(R.id.ageTextView)
        // Set the text views
        nameTextView.text = "Name: ${baby.name}"
        dobTextView.text = "DOB: ${baby.dateOfBirth}"
        genderTextView.text = "Gender: ${baby.gender}"
        healthNoteTextView.text = "Health Note: ${baby.healthNote}"
        emergencyContactTextView.text = "Emergency Contact: ${baby.emergencyContact}"
        ageTextView.text = "Age: ${calculateAge(baby.dateOfBirth)}"
        // Set the image view if a picture URI is provided
        babyImageView.setImageBitmap(BitmapFactory.decodeByteArray(baby.picture,0,baby.picture.size))

        return view
    }
    private fun calculateAge(dob: String): Int {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val dobDate = sdf.parse(dob)
        val today = Calendar.getInstance()

        val dobCalendar = Calendar.getInstance()
        dobCalendar.time = dobDate

        var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }
}
