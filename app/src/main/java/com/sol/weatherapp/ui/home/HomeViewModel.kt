package com.sol.weatherapp.ui.home

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Constraints.Builder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sol.weatherapp.data.repository.WeatherRepositoryLocal
import com.sol.weatherapp.db.WeatherInfo
import com.sol.weatherapp.util.TrackWeatherInfoWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val WEATHER_INFO_WORK = "weather_info_work"

class HomeViewModel @Inject constructor(
    private var weatherRepositoryLocal: WeatherRepositoryLocal,
    private val context: Context
) : ViewModel() {
    private val homeViewStateLiveData: MutableLiveData<HomeViewState> = MutableLiveData()
    var temperature: ObservableField<Double> = ObservableField()
    var feelsLikeTemp: ObservableField<Double> = ObservableField()
    var minTemp: ObservableField<Double> = ObservableField()
    var maxTemp: ObservableField<Double> = ObservableField()
    var description: ObservableField<String> = ObservableField()
    val dateInt: ObservableField<Int> = ObservableField()
    val sunrise: ObservableField<Int> = ObservableField()
    val sunset: ObservableField<Int> = ObservableField()
    val humidity: ObservableField<Int> = ObservableField()
    val pressure: ObservableField<Int> = ObservableField()
    val windSpeed: ObservableField<Double> = ObservableField()
    val windDegree: ObservableField<Int> = ObservableField()
    val locationName: ObservableField<String> = ObservableField()
    val countryName: ObservableField<String> = ObservableField()
    val weatherIcon: ObservableField<String> = ObservableField()
    val networkState: ObservableField<Boolean> = ObservableField()
    fun getHomeViewState(): LiveData<HomeViewState> {
        return homeViewStateLiveData
    }

    fun startPeriodicWorkRequest() {
        homeViewStateLiveData.value = Loading
        val constraints = Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val weatherInfoWorker =
            PeriodicWorkRequestBuilder<TrackWeatherInfoWorker>(2, TimeUnit.HOURS).addTag(
                WEATHER_INFO_WORK
            ).setConstraints(constraints).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WEATHER_INFO_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            weatherInfoWorker
        )
    }

    fun getWeatherDataCount(): LiveData<Int> = weatherRepositoryLocal.getWeatherDataCount()
    fun getCurrentWeather(): LiveData<WeatherInfo> {
        return weatherRepositoryLocal.getWeatherInfoFromDb()
    }

    fun setUIData(weatherFromDb: WeatherInfo) {
        temperature.set(weatherFromDb.temperature)
        feelsLikeTemp.set(weatherFromDb.feelLikeTemp)
        minTemp.set(weatherFromDb.minimumTemp)
        maxTemp.set(weatherFromDb.maximumTemp)
        description.set(weatherFromDb.tempDescription)
        weatherIcon.set(weatherFromDb.icon)
        sunrise.set(weatherFromDb.sunrise)
        sunset.set(weatherFromDb.sunset)
        pressure.set(weatherFromDb.pressure)
        humidity.set(weatherFromDb.humidity)
        windDegree.set(weatherFromDb.winddegree)
        windSpeed.set(weatherFromDb.windspeed)
        dateInt.set(weatherFromDb.currentDate)
        locationName.set(weatherFromDb.locationName)
        countryName.set(weatherFromDb.country)
    }

    fun setNetworkState(connectivityState: Boolean) {
        networkState.set(connectivityState)
    }
}