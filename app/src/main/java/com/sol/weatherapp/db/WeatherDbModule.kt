package com.sol.weatherapp.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides

import javax.inject.Singleton

@Module
object WeatherDbModule {
    @JvmStatic
    @Singleton
    @Provides
    fun provideWeatherDatabase(context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java, WeatherDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideShowDao(weatherDb: WeatherDatabase): WeatherDao {
        return weatherDb.weatherDao()
    }
}
