package com.jay.edvorarideshare.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.edvorarideshare.data.repository.RideRepository
import kotlinx.coroutines.launch

class RidesViewModel: ViewModel() {

    private val repo = RideRepository.getInstance()

    init {
        viewModelScope.launch { repo.getUser() }
    }

    suspend fun getRides(stationCode: Int) = repo.getAllRides(stationCode)

    val user = repo.userStateFlow
    val allRides = repo.allRides
    val nearestRides = repo.nearestRides
    val upcomingRides = repo.upcomingRides
    val pastRides = repo.pastRides

    val states = repo.states
    val cities = repo.cities

    fun filterCities(state: String) = repo.filterCities(state)

    fun filterByState(state: String) = repo.filterByState(state)
    fun filterByCity(city: String) = repo.filterByCity(city)

}