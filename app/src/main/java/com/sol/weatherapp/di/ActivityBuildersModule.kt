package com.sol.weatherapp.di



import com.sol.weatherapp.ui.home.HomeActivity
import com.sol.weatherapp.ui.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {
    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity
    @ContributesAndroidInjector
    abstract fun bindHomeActivity(): HomeActivity
}
