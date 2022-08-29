package com.example.projectskelton.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.projectskelton.data.repository.Run

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunInDb(run: Run): Long

    @Delete
    suspend fun deleteRun(run: Run)

    //why not suspend fun
    @Query("SELECT * from running_table ORDER BY timeStamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER BY distanceInMetres DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis(): LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER BY caloriesBurnt DESC")
    fun getAllRunsSortedByCaloriesBurnt(): LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillis) from running_table")
    fun getTotalTimeInMilliseconds(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurnt) from running_table")
    fun getTotalTimeCaloriesBurnt(): LiveData<Int>

    @Query("SELECT SUM(distanceInMetres) from running_table")
    fun getTotalTimeDistanceInMetres(): LiveData<Int>

    @Query("SELECT AVG(avgSpeedInKMH) from running_table")
    fun getTotalAvgSpeed(): LiveData<Float>
}