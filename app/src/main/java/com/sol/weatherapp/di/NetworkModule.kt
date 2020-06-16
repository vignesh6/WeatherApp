package com.sol.weatherapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.sol.weatherapp.BuildConfig
import com.sol.weatherapp.WeatherApp
import com.sol.weatherapp.api.WeatherAppApi
import com.sol.weatherapp.util.AppConstant
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
object NetworkModule {
    const val WEATHER_APP = "weather_app"
    private const val BASE_URL = "https://api.openweathermap.org/"

    @JvmStatic
    @Provides
    @Named(WEATHER_APP)
    fun provideBaseUrlString(): String {
        return BASE_URL
    }
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }
    @Provides
    @Singleton
    fun provideSharedPreference(context: Context):SharedPreferences{
        return context.getSharedPreferences(AppConstant.PREFERENCE_NAME,Context.MODE_PRIVATE)
    }
    @Provides
    @Singleton
    fun provideStethoIntercepor():StethoInterceptor{
        return StethoInterceptor()
    }
    @Singleton
    @Provides
    fun provideApiInterface(okhttpClient: OkHttpClient,
                           converterFactory: GsonConverterFactory, @Named(NetworkModule.WEATHER_APP) baseURL: String
    ) = provideService(okhttpClient, converterFactory, WeatherAppApi::class.java,baseURL)
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor, cache: Cache,stetho: StethoInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(stetho)
            .cache(cache)
            .build()
    }

    @Provides
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor().apply { level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideCache(context: Context): Cache {
        val cacheSize = 5 * 1024 * 1024 // 5 MB
        val cacheDir = context.cacheDir
        return Cache(cacheDir, cacheSize.toLong())
    }
    private fun createRetrofit(
        okhttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory, @Named(NetworkModule.WEATHER_APP) baseURL: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .client(okhttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }
    @CoroutineScropeIO
    @Provides
    fun provideCoroutineScopeIO() = CoroutineScope(Dispatchers.IO)
    private fun <T> provideService(okhttpClient: OkHttpClient,
                                   converterFactory: GsonConverterFactory, clazz: Class<T>, @Named(NetworkModule.WEATHER_APP) baseURL: String): T {
        return createRetrofit(okhttpClient, converterFactory,baseURL).create(clazz)
    }
}