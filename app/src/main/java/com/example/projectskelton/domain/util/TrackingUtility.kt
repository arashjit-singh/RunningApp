package com.example.projectskelton.domain.util

import android.location.Location
import com.example.projectskelton.presentation.services.Polyline

object TrackingUtility {

    fun calculatePolyLineLength(polyline: Polyline): Float {
        var distance = 0f
        for (i in 0..polyline.size - 2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]

            val resultArray = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                resultArray
            )

            distance += resultArray[0]
        }

        return distance
    }

}