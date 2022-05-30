package com.jay.edvorarideshare.utils

import java.text.SimpleDateFormat
import java.util.*

object Helpers {

    fun stringToDate(sDate: String) : Date? =
        SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.ENGLISH)
            .parse(sDate)

    fun Date.formatDate() : String? =
        SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH).format(this)

    // calculate distance from each point in station path and get the min distance
    fun getDistance(userStationId: Int, stationPath: List<Int>) : Int =
        stationPath.minOf { kotlin.math.abs(it - userStationId) }
}