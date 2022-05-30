package com.jay.edvorarideshare.data.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("station_code") val stationCode: Int,
    val name: String,
    @SerializedName("url") val imageUrl: String
)