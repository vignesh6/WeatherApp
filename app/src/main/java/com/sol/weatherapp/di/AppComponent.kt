package com.sol.weatherapp.di

import android.app.Application
import com.sol.weatherapp.WeatherApp
import com.sol.weatherapp.db.WeatherDbModule
import com.sol.weatherapp.util.TrackWeatherInfoWorker
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        NetworkModule::class,   ViewModelModule::class,ActivityBuildersModule::class,WeatherDbModule::class]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
    fun inject(worker: TrackWeatherInfoWorker)
    fun inject(application: WeatherApp)
}