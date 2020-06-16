package com.android.tvmaze.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sol.weatherapp.data.model.*
import com.sol.weatherapp.db.WeatherInfo


object TestUtil {
    val weathetCount = 1
    val weatherData =  WeatherData(base="stations", clouds= Clouds(all=40), cod=200, coord= Coord(lat=12.92, lon=77.61), dt=1592228692, id=1277333, main= Main(feels_like=29.55, humidity=61, pressure=1009, temp=28.58, temp_max=30.0, temp_min=27.0), name="Bengaluru", sys= Sys(country="IN", id=9205, sunrise=1592180621, sunset=1592226993, type=1), timezone=19800, visibility=6000, weather= listOf(Weather(description="scattered clouds", icon="03n", id=802, main="Clouds")), wind=Wind(deg=290, speed=4.1))
    val weatherDataError =  WeatherData(base="stations", clouds= Clouds(all=40), cod=400, coord= Coord(lat=12.92, lon=77.61), dt=1592228692, id=1277333, main= Main(feels_like=29.55, humidity=61, pressure=1009, temp=28.58, temp_max=30.0, temp_min=27.0), name="Bengaluru", sys= Sys(country="IN", id=9205, sunrise=1592180621, sunset=1592226993, type=1), timezone=19800, visibility=6000, weather= listOf(Weather(description="scattered clouds", icon="03n", id=802, main="Clouds")), wind=Wind(deg=290, speed=4.1))
    val _weatherInfo = WeatherInfo(currentDate=1592212177, locationName="Bengaluru", country="IN", temperature=28.69, feelLikeTemp=28.02, minimumTemp=27.78, maximumTemp=29.44, tempDescription="broken clouds", sunrise=1592180621, sunset=1592226993, humidity=62, pressure=1009, windspeed=6.7, winddegree=290, icon="04d")
    private var count:MutableLiveData<Int> = MutableLiveData()
    private var weatherInfo:MutableLiveData<WeatherInfo> = MutableLiveData()
    fun getWeatherDataCount():LiveData<Int>{
        count.value = 1
        return count
    }

    fun getWeatherInfo(): LiveData<WeatherInfo>? {
        weatherInfo.value = _weatherInfo
        return weatherInfo
    }
}