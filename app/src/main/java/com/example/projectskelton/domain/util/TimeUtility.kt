package com.example.projectskelton.domain.util

import java.util.concurrent.TimeUnit

object TimeUtility {
    fun getFormattedTime(ms: Long, includeMillis: Boolean = false): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        if (!includeMillis) {
            return "${if (hours <= 9) "0" else ""}$hours:" +
                    "${if (minutes <= 9) "0" else ""}$minutes:" +
                    "${if (seconds <= 9) "0" else ""}$seconds"

        }
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 10
        return "${if (hours <= 9) "0" else ""}$hours:" +
                "${if (minutes <= 9) "0" else ""}$minutes:" +
                "${if (seconds <= 9) "0" else ""}$seconds:" +
                "${if (milliseconds <= 9) "0" else ""}$milliseconds"

    }
}