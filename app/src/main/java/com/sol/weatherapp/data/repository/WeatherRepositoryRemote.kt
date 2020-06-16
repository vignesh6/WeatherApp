package com.sol.weatherapp.data.repository

import com.sol.weatherapp.api.WeatherAppApi
import com.sol.weatherapp.data.model.WeatherData
import com.sol.weatherapp.util.AppConstant
import javax.inject.Inject

class WeatherRepositoryRemote @Inject constructor(private val api: WeatherAppApi) {
    suspend fun getWeather(latitude: String, longitude: String):WeatherData {
        val weatherData = api.getCurrentWeatherData(
            latitude,
            longitude,
            AppConstant.API_KEY,
            AppConstant.CELSIUS_UNIT
        )
        return weatherData
    }
}