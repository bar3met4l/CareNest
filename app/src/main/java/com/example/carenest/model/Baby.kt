package com.example.carenest.model
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "baby_table")
data class Baby(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val gender: String,
    val dateOfBirth: String,
    val healthNote: String,
    val emergencyContact: String,
    val picture: ByteArray
)