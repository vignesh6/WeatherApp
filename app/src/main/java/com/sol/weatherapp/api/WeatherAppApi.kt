package com.sol.weatherapp.api

import com.sol.weatherapp.data.model.WeatherData
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WeatherAppApi {
    @POST("data/2.5/weather")
    suspend
    fun getCurrentWeatherData(@Query("lat") lat: String, @Query("lon") lon: String, @Query("appid") app_id: String,@Query("units")unit:String): WeatherData
}