package com.example.projectskelton.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.projectskelton.data.convertors.Convertors
import com.example.projectskelton.data.database.dao.RunDao
import com.example.projectskelton.data.repository.Run

@Database(entities = [Run::class], version = 1)
@TypeConverters(Convertors::class)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun provideRunDao(): RunDao
}