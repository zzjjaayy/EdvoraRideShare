package com.jay.edvorarideshare.data.repository

import android.util.Log
import com.jay.edvorarideshare.data.RetrofitInstance
import com.jay.edvorarideshare.data.RideApi
import com.jay.edvorarideshare.data.models.Ride
import com.jay.edvorarideshare.data.models.User
import com.jay.edvorarideshare.utils.Helpers
import com.jay.edvorarideshare.utils.NetworkResult
import com.jay.edvorarideshare.utils.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response
import java.lang.Exception

class RideRepository private constructor(private val api: RideApi) {

    companion object {
        private var INSTANCE: RideRepository? = null
        fun getInstance(): RideRepository {
            if (INSTANCE == null) {
                INSTANCE = RideRepository(RetrofitInstance.API)
            }
            return INSTANCE!!
        }
    }

    private val _userStateFlow = MutableStateFlow<NetworkResult<User>>(NetworkResult.Empty())
    val userStateFlow: StateFlow<NetworkResult<User>> = _userStateFlow

    private val _allRides = MutableStateFlow<NetworkResult<List<Ride>>>(NetworkResult.Empty())
    val allRides: StateFlow<NetworkResult<List<Ride>>> = _allRides

    private val _nearestRides = MutableStateFlow<NetworkResult<List<Ride>>>(NetworkResult.Empty())
    val nearestRides: StateFlow<NetworkResult<List<Ride>>> = _nearestRides

    private val _upcomingRides = MutableStateFlow<NetworkResult<List<Ride>>>(NetworkResult.Empty())
    val upcomingRides: StateFlow<NetworkResult<List<Ride>>> = _upcomingRides

    private val _pastRides = MutableStateFlow<NetworkResult<List<Ride>>>(NetworkResult.Empty())
    val pastRides: StateFlow<NetworkResult<List<Ride>>> = _pastRides

    private val _states = MutableStateFlow<List<String>>(emptyList())
    val states: StateFlow<List<String>> = _states

    private val _cities = MutableStateFlow<List<String>>(emptyList())
    val cities: StateFlow<List<String>> = _cities

    suspend fun getUser() {
        try {
            _userStateFlow.value = NetworkResult.Loading()
            val response : Response<User> = api.getUser()
            if(response.isSuccessful) {
                val user = response.body() ?: throw Exception("No user found!")
                Log.d(TAG, "getUser: ${user.name}")
                _userStateFlow.value = NetworkResult.Success(user)
            } else throw Exception(response.errorBody().toString())
        } catch (exception: Exception) {
            Log.d(TAG, "getUser: $exception")
            _userStateFlow.value = NetworkResult.Error("Error while getting user!")
        }
    }

    suspend fun getAllRides(userStationCode: Int) {
        try {
            _allRides.value = NetworkResult.Loading()
            val response : Response<List<Ride>> = RetrofitInstance.API.getRides()
            if(response.isSuccessful) {
                val rides = response.body() ?: throw Exception("No user found!")
                rides.forEach { ride ->
                    ride.date_ts = Helpers.stringToDate(ride.date)?.time ?: run {
                        Log.d(TAG, "getAllRides: date ts is 0 for date -> ${ride.date}")
                        0
                    }
                    ride.distance = Helpers.getDistance(userStationCode, ride.station_path)
                }
                Log.d(TAG, "getAllRides: found ${rides.size} rides with last element having ts -> ${rides[0].date_ts}")
                _allRides.value = NetworkResult.Success(rides)
                updateRides(rides)
                updateStates()
                updateCities()
            } else throw Exception(response.errorBody().toString())
        } catch (exception: Exception) {
            Log.d(TAG, "getAllRides: $exception")
            _allRides.value = NetworkResult.Error("Error while getting rides!")
        }
    }

    private fun updateNearestRides(rides: List<Ride>) {
        if(rides.isEmpty()) {
            _nearestRides.value = NetworkResult.Error("No Rides Found!")
            return
        }
        val nearest = rides.sortedBy { it.distance }
        _nearestRides.value = NetworkResult.Success(nearest)
    }

    private fun updateUpcomingRides(rides: List<Ride>) {
        if(rides.isEmpty()) {
            _upcomingRides.value = NetworkResult.Error("No Rides Found!")
            return
        }
        val upcoming = rides.filter {
            it.date_ts > System.currentTimeMillis()
        }.sortedBy { it.date_ts }
        if(upcoming.isEmpty()) _upcomingRides.value = NetworkResult.Empty()
        else _upcomingRides.value = NetworkResult.Success(upcoming)
        Log.d(TAG, "updateUpcomingRides: ${upcoming.size} rides")
    }

    private fun updatePastRides(rides: List<Ride>) {
        if(rides.isEmpty()) {
            _pastRides.value = NetworkResult.Error("No Rides Found!")
            return
        }
        val past = rides.filter {
            it.date_ts < System.currentTimeMillis()
        }.sortedBy { it.date_ts }
        if(past.isEmpty()) _pastRides.value = NetworkResult.Empty()
        else _pastRides.value = NetworkResult.Success(past)
        Log.d(TAG, "updatePastRides: ${past.size} rides")
    }

    fun updateStates() {
        _states.value = _allRides.value.data?.map { it.state }?.toSet()?.toList() ?: emptyList()
    }

    fun updateCities() {
        _cities.value = _allRides.value.data?.map { it.city }?.toSet()?.toList() ?: emptyList()
        Log.d(TAG, "updateCities: ${cities.value.size}")
    }

    fun filterCities(state: String) {
        _cities.value = _allRides.value.data?.filter { it.state == state }?.map { it.city }
            ?.toSet()?.toList() ?: emptyList()
    }

    fun filterByState(state: String) {
        updateRides(_allRides.value.data?.filter { it.state == state } ?: emptyList())
    }

    fun filterByCity(city: String) {
        updateRides(_allRides.value.data?.filter { it.city == city } ?: emptyList())
    }

    fun updateRides(rides: List<Ride>) {
        updateUpcomingRides(rides)
        updateNearestRides(rides)
        updatePastRides(rides)
    }
}