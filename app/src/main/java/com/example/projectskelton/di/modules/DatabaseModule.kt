package com.example.projectskelton.di.modules

import android.content.Context
import androidx.room.Room
import com.example.projectskelton.data.database.MyRoomDatabase
import com.example.projectskelton.data.database.dao.RunDao
import com.example.projectskelton.domain.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabaseBuilder(@ApplicationContext context: Context): MyRoomDatabase {
        return Room.databaseBuilder(
            context, MyRoomDatabase::class.java, DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideRunDao(db: MyRoomDatabase): RunDao {
        return db.provideRunDao()
    }


}