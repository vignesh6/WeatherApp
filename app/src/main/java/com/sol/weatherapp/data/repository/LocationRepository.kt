package com.sol.weatherapp.data.repository

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.sol.weatherapp.util.WeatherAppSharedPreference
import com.sol.weatherapp.util.extension.checkLocationPermission
import com.sol.weatherapp.util.extension.isGPSEnabled
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
        private val application: Application,
        private val sharedPreference: WeatherAppSharedPreference) {

    @SuppressLint("MissingPermission")
    fun getLocation():Pair<String,String> {
        if (application.isGPSEnabled() && application.checkLocationPermission()) {
            LocationServices.getFusedLocationProviderClient(application)
                    ?.lastLocation
                    ?.addOnSuccessListener { location: Location? ->
                        if (location != null)
                            saveLocation(location)
                    }
        }
        return getSavedLocation()
    }

    private fun saveLocation(location: Location) {
        sharedPreference.saveLocation(location)
    }

    private fun getSavedLocation():Pair<String,String> {
        return sharedPreference.getLocation()
    }
}