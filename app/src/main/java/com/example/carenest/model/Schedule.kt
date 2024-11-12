package com.example.carenest.model
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "schedule_table")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val babyId: Int,
    val babyName: String,
    val activity: String,
    val hour: Int,
    val minute: Int
)