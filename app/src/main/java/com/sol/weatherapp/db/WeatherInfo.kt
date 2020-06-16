package com.sol.weatherapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_info")
data class WeatherInfo(
    @PrimaryKey(autoGenerate = true)
    val currentDate: Int,
    val locationName: String,
    val country: String,
    val temperature: Double,
    val feelLikeTemp: Double,
    val minimumTemp: Double,
    val maximumTemp: Double,
    val tempDescription: String,
    val sunrise: Int,
    val sunset: Int,
    val humidity: Int,
    val pressure: Int,
    val windspeed: Double,
    val winddegree: Int,
    val icon: String
)