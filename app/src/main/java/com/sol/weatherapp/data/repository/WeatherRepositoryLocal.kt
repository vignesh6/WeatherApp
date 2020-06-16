package com.sol.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.sol.weatherapp.data.model.WeatherData
import com.sol.weatherapp.db.WeatherDao
import com.sol.weatherapp.db.WeatherInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryLocal @Inject constructor(private val weatherDao: WeatherDao) {
    fun insertWeatherData(weatherData: WeatherData){
        var describtion = ""
        var weatherIcon = ""
        if(weatherData.weather.size>0) {
            describtion = weatherData.weather[0].description
            weatherIcon = weatherData.weather[0].icon
        }
        val weatherInfo = WeatherInfo(
            locationName = weatherData.name,
            country = weatherData.sys.country,
            temperature = weatherData.main.temp,
            feelLikeTemp = weatherData.main.feels_like,
            minimumTemp = weatherData.main.temp_min,
            maximumTemp = weatherData.main.temp_max,
            currentDate = weatherData.dt,
            tempDescription = describtion,
            sunrise = weatherData.sys.sunrise,
            sunset = weatherData.sys.sunset,
            humidity = weatherData.main.humidity,
            pressure = weatherData.main.pressure,
            windspeed = weatherData.wind.speed,
            winddegree = weatherData.wind.deg,
            icon = weatherIcon
        )
        CoroutineScope(Dispatchers.IO).launch { weatherDao.insert(weatherInfo) }
    }
    fun getWeatherInfoFromDb():LiveData<WeatherInfo>
    {
        return weatherDao.getWeatherInfo()
    }

    fun getWeatherDataCount():LiveData<Int>{
        return weatherDao.getWeatherDataCount()
    }

    fun clearAllWeatherData(){
        CoroutineScope(Dispatchers.IO).launch { weatherDao.clearData()}
    }
    suspend fun allData(): List<WeatherInfo> {
        return weatherDao.allWeatherData()
    }

}