package com.example.carenest.data
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.carenest.model.Baby

@Dao
interface BabyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(baby: Baby): Long

    @Update
    suspend fun update(baby: Baby)

    @Delete
    suspend fun delete(baby: Baby)


    @Query("SELECT * FROM baby_table ORDER BY id ASC")
    fun getAllBabies(): LiveData<List<Baby>>
}