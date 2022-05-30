package com.jay.edvorarideshare.data.models

data class Ride(
    val id: Int,
    val origin_station_code: Int,
    val station_path: List<Int>,
    val destination_station_code: Int,
    val date: String,
    val map_url: String,
    val city: String,
    val state: String,
    var date_ts: Long = 0,
    var distance: Int = 0
)
