package com.example.projectskelton.di.modules

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.projectskelton.R
import com.example.projectskelton.domain.util.Constants
import com.example.projectskelton.presentation.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext app: Context) =
        FusedLocationProviderClient(app)


    //If you would like the notification to be non-dismissable by the user,
    // pass true into the setOngoing() method when you create your notification using Notification.Builder.
    @SuppressLint("NewApi")
    @Provides
    @ServiceScoped
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ): Notification.Builder {
        return Notification.Builder(app, Constants.NOTIFICATION_CHANNEL_ID.toString()).apply {
            setAutoCancel(false)
            setOngoing(true)
            setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            setContentTitle("Running App")
            setContentText("00:00:00")
            setContentIntent(pendingIntent)
        }
    }

    @ServiceScoped
    @Provides
    fun ProvideMainActivityPendingIntent(@ApplicationContext app: Context) =
        Intent(app, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        }.let { notificationIntent ->
            PendingIntent.getActivity(
                app, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
}