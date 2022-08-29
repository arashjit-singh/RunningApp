package com.example.projectskelton.domain.util

import android.Manifest
import com.example.projectskelton.R

object Constants {
    val API_BASE_PATH = ""
    val DATABASE_NAME = "dbName.db"
    val TAG = "ChuckNorrisApp"
    val REQUEST_CODE_LOCATION_PERMISSION = 1

    //service constants
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    //foreground service constant
    const val NOTIFICATION_CHANNEL_NAME = "TrackingChannel"
    const val NOTIFICATION_CHANNEL_DESCRIPTION = "Channel For Tracking user run"
    const val NOTIFICATION_CHANNEL_ID = 1
    const val NOTIFICATION_ID = 2

    //constants/action for fragment redirection
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    var LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    //intevals for getting location updates
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val LOCATION_FASTEST_INTERVALE = 2000L

    const val POLYLINE_COLOR = R.color.errorColor
    const val POLYLINE_WIDTH = 20.0f
    const val MAP_ZOOM = 15f

    const val TIMER_UPDATE_INTERVAL = 50L

    //shared preference
    const val SHARED_PREFERENCE_NAME = "runningApp"
    const val KEY_NAME = "name"
    const val KEY_WEIGHT = "weight"
    const val KEY_IS_FIRST_LOGIN = "KEY_IS_FIRST_LOGIN"


}

