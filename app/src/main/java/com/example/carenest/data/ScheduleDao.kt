package com.example.carenest.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.carenest.model.Schedule

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: Schedule)

    @Query("SELECT * FROM schedule_table WHERE babyName = :babyName ORDER BY hour ASC, minute ASC")
    fun getSchedulesForBaby(babyName: String): LiveData<List<Schedule>>


    @Delete
    suspend fun delete(schedule: Schedule)
}
