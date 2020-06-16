package com.sol.weatherapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherInfo: WeatherInfo)

    @Query("SELECT * FROM WEATHER_INFO WHERE currentDate = (SELECT MAX(currentDate) FROM weather_info)")
    fun getWeatherInfo():LiveData<WeatherInfo>

    @Query("SELECT COUNT(*) FROM WEATHER_INFO")
    fun getWeatherDataCount():LiveData<Int>

    @Query("SELECT * FROM weather_info")
    suspend fun allWeatherData(): List<WeatherInfo>

    @Query("DELETE FROM weather_info")
    suspend fun clearData()
}