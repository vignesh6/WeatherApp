package com.sol.weatherapp

import android.os.Build
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.sol.weatherapp.di.AppComponent
import com.sol.weatherapp.di.AppInjector
import com.sol.weatherapp.di.DaggerAppComponent
import com.sol.weatherapp.di.Provider
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class WeatherApp : MultiDexApplication(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>
    private lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        appComponent.inject(this)
        Provider.appComponent = appComponent
        AppInjector.init(this)
        if (!isRoboUnitTest()) {
            Stetho.initializeWithDefaults(this);
        }
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    override fun androidInjector(): AndroidInjector<Any>? = dispatchingAndroidInjector
    fun isRoboUnitTest(): Boolean {
        return "robolectric" == Build.FINGERPRINT
    }
}