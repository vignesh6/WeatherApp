package com.sol.weatherapp.api

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.tvmaze.utils.MainCoroutineRule
import com.android.tvmaze.utils.TestUtil
import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.whenever
import com.sol.weatherapp.data.repository.WeatherRepositoryRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

const val latitude = "12.908710"
const val longitude = "77.600620"
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class WeatherApiTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()
    @Mock
    private lateinit var service: WeatherAppApi
    @Mock
    private lateinit var weatherRepositoryRemote: WeatherRepositoryRemote
    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `load data from api success`(){
        mainCoroutineRule.runBlockingTest {
           whenever(weatherRepositoryRemote.getWeather(latitude, longitude)).thenReturn(TestUtil.weatherData)
            mainCoroutineRule.pauseDispatcher()
            val weatherData = weatherRepositoryRemote.getWeather(latitude, longitude)
            mainCoroutineRule.resumeDispatcher()
            Truth.assertThat(weatherData).isEqualTo(TestUtil.weatherData)
        }
    }
    @Test
    fun `check valid location weather returned`(){
        mainCoroutineRule.runBlockingTest {
            whenever(weatherRepositoryRemote.getWeather(latitude, longitude)).thenReturn(TestUtil.weatherData)
            mainCoroutineRule.pauseDispatcher()
            val weatherData = weatherRepositoryRemote.getWeather(latitude, longitude)
            mainCoroutineRule.resumeDispatcher()
            Truth.assertThat(weatherData.name).isEqualTo("Bengaluru")
            Truth.assertThat(weatherData.sys.country).isEqualTo("IN")
        }
    }
    @Test
    fun `check valid status code`(){
        mainCoroutineRule.runBlockingTest {
            whenever(weatherRepositoryRemote.getWeather(latitude, longitude)).thenReturn(TestUtil.weatherData)
            mainCoroutineRule.pauseDispatcher()
            val weatherData = weatherRepositoryRemote.getWeather(latitude, longitude)
            mainCoroutineRule.resumeDispatcher()
            Truth.assertThat(weatherData.cod).isEqualTo(200)
        }
    }
}