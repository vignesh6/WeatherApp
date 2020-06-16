package com.sol.weatherapp.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sol.weatherapp.data.model.WeatherData
import com.sol.weatherapp.data.repository.LocationRepository
import com.sol.weatherapp.data.repository.WeatherRepositoryLocal
import com.sol.weatherapp.data.repository.WeatherRepositoryRemote
import com.sol.weatherapp.di.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class TrackWeatherInfoWorker @Inject constructor(context: Context, workParams: WorkerParameters) :
    CoroutineWorker(context, workParams) {
    @Inject
    lateinit var repositoryLocal: WeatherRepositoryLocal

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var sharedPreference: WeatherAppSharedPreference

    @Inject
    lateinit var repository: WeatherRepositoryRemote

    init {
        Provider.appComponent?.inject(this)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        var weatherData: WeatherData? = null
        //fetching location
        val locationSync = async {
            locationRepository.getLocation()
        }
        val locationData = locationSync.await()
        if (locationData.first.isNotEmpty() && locationData.second.isNotEmpty()) {
            //getting data from open weather.org api
            val weatherDataFetch = async {
                repository.getWeather(locationData.first, locationData.second)
            }
            weatherData =  weatherDataFetch.await()
            if (weatherData.cod == 200) {
                //storing weather datd in local repository
                repositoryLocal.insertWeatherData(weatherData = weatherData)
            } else {
                Result.retry()
            }
        }
        Result.success()
    }
}