package com.sol.weatherapp.ui.home

sealed class HomeViewState
data class NetworkError(val message: String?) : HomeViewState()
object Loading : HomeViewState()