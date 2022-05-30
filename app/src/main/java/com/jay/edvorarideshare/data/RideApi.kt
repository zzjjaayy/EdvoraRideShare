package com.jay.edvorarideshare.data

import com.jay.edvorarideshare.data.models.Ride
import com.jay.edvorarideshare.data.models.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RideApi {
    @GET("user")
    suspend fun getUser() : Response<User>

    @GET("rides")
    suspend fun getRides() : Response<List<Ride>>
}