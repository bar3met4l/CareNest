package com.example.carenest.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.carenest.model.Baby
import com.example.carenest.model.Schedule

@Database(entities = [Baby::class, Schedule::class], version = 1)
abstract class BabyDatabase : RoomDatabase() {

    abstract fun babyDao(): BabyDao
    abstract fun scheduleDao(): ScheduleDao
}