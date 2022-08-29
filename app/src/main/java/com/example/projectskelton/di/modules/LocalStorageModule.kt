package com.example.projectskelton.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.example.projectskelton.domain.util.Constants.KEY_IS_FIRST_LOGIN
import com.example.projectskelton.domain.util.Constants.KEY_NAME
import com.example.projectskelton.domain.util.Constants.KEY_WEIGHT
import com.example.projectskelton.domain.util.Constants.SHARED_PREFERENCE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalStorageModule {

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideUserName(sharedPref: SharedPreferences): String {
        return sharedPref.getString(KEY_NAME, "") ?: ""
    }

    @Singleton
    @Provides
    fun provideUserWeight(sharedPref: SharedPreferences): Float {
        return sharedPref.getFloat(KEY_WEIGHT, 80f)
    }

    @Singleton
    @Provides
    fun provideIsUserFirstLogin(sharedPref: SharedPreferences): Boolean {
        return sharedPref.getBoolean(KEY_IS_FIRST_LOGIN, true)
    }
}