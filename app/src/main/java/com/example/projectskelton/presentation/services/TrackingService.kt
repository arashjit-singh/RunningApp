package com.example.projectskelton.presentation.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.projectskelton.R
import com.example.projectskelton.domain.util.Constants.ACTION_PAUSE_SERVICE
import com.example.projectskelton.domain.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.projectskelton.domain.util.Constants.ACTION_STOP_SERVICE
import com.example.projectskelton.domain.util.Constants.LOCATION_FASTEST_INTERVALE
import com.example.projectskelton.domain.util.Constants.LOCATION_PERMISSIONS
import com.example.projectskelton.domain.util.Constants.LOCATION_UPDATE_INTERVAL
import com.example.projectskelton.domain.util.Constants.NOTIFICATION_CHANNEL_DESCRIPTION
import com.example.projectskelton.domain.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.projectskelton.domain.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.projectskelton.domain.util.Constants.NOTIFICATION_ID
import com.example.projectskelton.domain.util.Constants.TIMER_UPDATE_INTERVAL
import com.example.projectskelton.domain.util.TimeUtility
import com.example.projectskelton.domain.util.TrackingPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias PolyLines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    var isFirstRun = true
    var serviceKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val timeRunInSeconds = MutableLiveData<Long>()

    @Inject
    lateinit var baseNotificationBuilder: Notification.Builder

    lateinit var currNotificationBuilder: Notification.Builder


    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<PolyLines>()
        val timeRunInMillis = MutableLiveData<Long>()
    }

    override fun onCreate() {
        super.onCreate()
        currNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this) {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        }
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        //we dont't have coordinates in beginning
        pathPoints.postValue(mutableListOf())
        timeRunInMillis.postValue(0L)
        timeRunInSeconds.postValue(0L)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService()
                        }
                        isFirstRun = false
                    } else {
                        startTimer()
                        Timber.d("Resuming Service..")
                    }


                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                    Timber.d("ACTION_PAUSE_SERVICE")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("ACTION_STOP_SERVICE")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false

    //time of current lap between start and pause
    private var lapTime = 0L

    //total time of run
    private var timeRun = 0L

    //time stamp when we started timer
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                //time difference b/w now and time started
                lapTime = System.currentTimeMillis() - timeStarted
                //post new lap time
                timeRunInMillis.postValue(timeRun + lapTime)
                //timeRunINmillis = 1550
                //lastSecondTimeStamp = 1000 (second)
                if (timeRunInMillis.value!! >= lastSecondTimeStamp + 1000) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimeStamp += 1000
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }

    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationtext = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        currNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currNotificationBuilder, ArrayList<Notification.Action>())
        }

        if (!serviceKilled) {
            currNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24dp, notificationtext, pendingIntent)

            notificationManager.notify(NOTIFICATION_ID, currNotificationBuilder.build())
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingPermission.hasPermissions(this, LOCATION_PERMISSIONS)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = LOCATION_FASTEST_INTERVALE
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoints(location)
                        Timber.d("NEW LOCATION ${location.latitude} AND ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun addPathPoints(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        //add empty polyline
        //the case when user stops tracking
        //to avoid having a complete polyline and
        //not connect points where user had stopped tracking
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))
    //add first empty polyline when pathpoints.value is null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)

        val notificationManager =
            application.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        //we will create notification channel for phones running O and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        /* //If you would like the notification to be non-dismissable by the user,
         // pass true into the setOngoing() method when you create your notification using Notification.Builder.
         val notification: Notification =
             Notification.Builder(this, NOTIFICATION_CHANNEL_ID.toString()).apply {
                 setAutoCancel(false)
                 setOngoing(true)
                 setSmallIcon(R.drawable.ic_directions_run_black_24dp)
                 setContentTitle("Running App")
                 setContentText("00:00:00")
                 setContentIntent(getMainActivityPendingIntent(applicationContext))
             }.build()*/

        // Notification ID cannot be 0.
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeRunInSeconds.observe(this) {

            //to prevent observer called one more time and show notification
            if (!serviceKilled) {
                val notification = currNotificationBuilder
                    .setContentText(TimeUtility.getFormattedTime(it * 1000))

                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        }
    }

    /*private fun getMainActivityPendingIntent() =
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).also {
                it.action = ACTION_SHOW_TRACKING_FRAGMENT
            },
            FLAG_UPDATE_CURRENT
        )*/

    // If the notification supports a direct reply action, use
    // PendingIntent.FLAG_MUTABLE instead.
    /* private fun getMainActivityPendingIntent(context: Context) =
         Intent(context, MainActivity::class.java).also {
             it.action = ACTION_SHOW_TRACKING_FRAGMENT
         }.let { notificationIntent ->
             PendingIntent.getActivity(
                 this, 0, notificationIntent,
                 PendingIntent.FLAG_IMMUTABLE
             )
         }*/


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = NOTIFICATION_CHANNEL_NAME
        val channelDescriptionText = NOTIFICATION_CHANNEL_DESCRIPTION

        // don't put importance above low
        //doing with for every notification phone will ring

        val channelImportance = NotificationManager.IMPORTANCE_DEFAULT

        val mChannel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID.toString(),
                channelName,
                channelImportance
            ).apply {
                description = channelDescriptionText
            }

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(mChannel)

    }

}