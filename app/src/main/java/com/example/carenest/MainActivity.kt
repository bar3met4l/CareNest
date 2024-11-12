package com.example.carenest

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.carenest.adapter.BabyAdapter
import com.example.carenest.data.BabyDatabase
import com.example.carenest.model.Baby
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var babyDatabase: BabyDatabase
    lateinit var selectedBitmap: Bitmap
    private lateinit var dialog: Dialog
    private lateinit var babyListView: ListView
    private val PICK_IMAGE_REQUEST = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_KidsCareApp)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        babyListView = findViewById(R.id.babyListView)
        babyDatabase = Room.databaseBuilder(applicationContext, BabyDatabase::class.java, "baby_database").build()
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        loadBabyData()
        fab.setOnClickListener {
            showAddBabyDialog()
        }
        babyListView.setOnItemClickListener { parent, view, position, id ->
            val clickedBaby = parent.getItemAtPosition(position) as Baby
            val intent = Intent(this, ScheduleActivity::class.java)
            intent.putExtra("baby_id", clickedBaby.id)
            intent.putExtra("baby_name", clickedBaby.name)
            startActivity(intent)
        }
        babyListView.setOnItemLongClickListener { parent, view, position, id ->
          dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_edit_baby)

            val selectImageButton = dialog.findViewById<Button>(R.id.selectImageButton)
            val dateOfBirthEditText = dialog.findViewById<EditText>(R.id.dateOfBirthEditText)
            val genderMale = dialog.findViewById<RadioButton>(R.id.genderMale)
            val genderFemale = dialog.findViewById<RadioButton>(R.id.genderFemale)
            val nameEditText = dialog.findViewById<EditText>(R.id.nameEditText)
            val healthNoteEditText = dialog.findViewById<EditText>(R.id.healthNoteEditText)
            val emergencyContactEditText = dialog.findViewById<EditText>(R.id.emergencyContactEditText)
            val updateButton = dialog.findViewById<Button>(R.id.updateButton)
            val deleteButton = dialog.findViewById<Button>(R.id.deleteButton)

            val baby = parent.getItemAtPosition(position) as Baby
            selectedBitmap= baby.picture?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }!!
            nameEditText.setText(baby.name)
            dateOfBirthEditText.setText(baby.dateOfBirth)
            healthNoteEditText.setText(baby.healthNote)
            emergencyContactEditText.setText(baby.emergencyContact)
            dialog.findViewById<ImageView>(R.id.babyImageView).setImageBitmap(selectedBitmap)
            if(baby.gender == "Male"){
                genderMale.isChecked = true
            }
            else{
                genderFemale.isChecked = true
            }
            selectImageButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
            dateOfBirthEditText.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    val date = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                    dateOfBirthEditText.setText(date)
                }, year, month, day)
                datePickerDialog.show()
            }
            updateButton.setOnClickListener {

                    val name = nameEditText.text.toString()
                    val dateOfBirth = dateOfBirthEditText.text.toString()
                    val gender = if (genderMale.isChecked) "Male" else "Female"
                    val healthNote = healthNoteEditText.text.toString()
                    val emergencyContact = emergencyContactEditText.text.toString()
                    val picture = bitmapToByteArray(selectedBitmap)
                    when {
                        name.isBlank() -> {
                            Toast.makeText(this, "Please enter the baby's name", Toast.LENGTH_SHORT).show()
                        }
                        dateOfBirth.isBlank() -> {
                            Toast.makeText(
                                this,
                                "Please enter the baby's date of birth",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        healthNote.isBlank() -> {
                            Toast.makeText(this, "Please enter a health note", Toast.LENGTH_SHORT).show()
                        }
                        emergencyContact.isBlank() -> {
                            Toast.makeText(this, "Please enter an emergency contact", Toast.LENGTH_SHORT)
                                .show()
                        }
                        picture == null -> {
                            Toast.makeText(this, "Please select a picture", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val baby1 = Baby(
                                name = name,
                                gender = gender,
                                dateOfBirth = dateOfBirth,
                                healthNote = healthNote,
                                emergencyContact = emergencyContact,
                                picture = picture
                            )
                            lifecycleScope.launch(Dispatchers.IO) {
                                babyDatabase.babyDao().insert(baby1)
                                babyDatabase.babyDao().delete(baby)

                            }
                            Toast.makeText(this, "Baby details saved!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            loadBabyData()
                        }
                    }
                }
            deleteButton.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    babyDatabase.babyDao().delete(baby)
                }
                Toast.makeText(this, "Baby deleted!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                loadBabyData()
            }
            dialog.show()
            true
        }
    }
    private fun showAddBabyDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_baby)
        val saveButton = dialog.findViewById<Button>(R.id.saveButton)
        val selectImageButton = dialog.findViewById<Button>(R.id.selectImageButton)
        val dateOfBirthEditText = dialog.findViewById<EditText>(R.id.dateOfBirthEditText)
        val genderMale = dialog.findViewById<RadioButton>(R.id.genderMale)
        val nameEditText = dialog.findViewById<EditText>(R.id.nameEditText)
        val healthNoteEditText = dialog.findViewById<EditText>(R.id.healthNoteEditText)
        val emergencyContactEditText = dialog.findViewById<EditText>(R.id.emergencyContactEditText)
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
             }
       dateOfBirthEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                dateOfBirthEditText.setText(date)
            }, year, month, day)
            datePickerDialog.show()
        }
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val dateOfBirth = dateOfBirthEditText.text.toString()
            val gender = if (genderMale.isChecked) "Male" else "Female"
            val healthNote = healthNoteEditText.text.toString()
            val emergencyContact = emergencyContactEditText.text.toString()
            val picture = bitmapToByteArray(selectedBitmap)
            when {
                name.isBlank() -> {
                    Toast.makeText(this, "Please enter the baby's name", Toast.LENGTH_SHORT).show()
                }
                dateOfBirth.isBlank() -> {
                    Toast.makeText(
                        this,
                        "Please enter the baby's date of birth",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                healthNote.isBlank() -> {
                    Toast.makeText(this, "Please enter a health note", Toast.LENGTH_SHORT).show()
                }
                emergencyContact.isBlank() -> {
                    Toast.makeText(this, "Please enter an emergency contact", Toast.LENGTH_SHORT)
                        .show()
                }
                picture == null -> {
                    Toast.makeText(this, "Please select a picture", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val baby = Baby(
                        name = name,
                        gender = gender,
                        dateOfBirth = dateOfBirth,
                        healthNote = healthNote,
                        emergencyContact = emergencyContact,
                        picture = picture
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        babyDatabase.babyDao().insert(baby)
                    }
                    Toast.makeText(this, "Baby details saved!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    loadBabyData()
                }
            }
        }
        dialog.show()
    }
    private fun loadBabyData() {
        babyDatabase.babyDao().getAllBabies().observe(this) { babyList ->
            val adapter = BabyAdapter(this, babyList)
            babyListView.adapter = adapter
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                selectedBitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, imageUri)
                    ImageDecoder.decodeBitmap(source)
                }
            }
            dialog.findViewById<ImageView>(R.id.babyImageView).setImageBitmap(selectedBitmap)
        }
    }
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}
