package com.sol.weatherapp.util

import android.content.SharedPreferences
import android.location.Location
import javax.inject.Inject

class WeatherAppSharedPreference @Inject constructor(private val sharedPreference: SharedPreferences) {
    val key_latitude = "latitude"
    val key_longitude = "longitude"
    fun getStringData(key: String) = sharedPreference.getString(key,"")
    fun saveLocation(location: Location){
        sharedPreference.edit().putString(key_latitude,location.latitude.toString()).apply()
        sharedPreference.edit().putString(key_longitude,location.longitude.toString()).apply()
    }
    fun getLocation():Pair<String,String>{
        val latitude = getStringData(key_latitude)
        val longitude = getStringData(key_longitude)
        return Pair(latitude!!,longitude!!)
    }
}