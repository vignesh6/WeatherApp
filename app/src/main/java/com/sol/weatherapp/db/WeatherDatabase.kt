package com.sol.weatherapp.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherInfo::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        const val DATABASE_NAME = "weather.db"
    }
}
