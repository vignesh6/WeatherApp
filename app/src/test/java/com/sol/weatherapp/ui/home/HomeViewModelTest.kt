package com.sol.weatherapp.ui.home

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.android.tvmaze.utils.LiveDataTestUtil
import com.android.tvmaze.utils.MainCoroutineRule
import com.android.tvmaze.utils.TestUtil
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.whenever
import com.sol.weatherapp.data.repository.WeatherRepositoryLocal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class HomeViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var weatherRepositoryLocal: WeatherRepositoryLocal

    @Mock
    private lateinit var context: Context

    @InjectMocks
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        val contextApp = InstrumentationRegistry.getInstrumentation().getTargetContext()
        // context = mock(Context::class.java)
        MockitoAnnotations.initMocks(this)
        val config: Configuration = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(
            contextApp, config
        )

        //homeViewModel = HomeViewModel(weatherRepositoryLocal,context)
    }

    @Test
    fun `load Data Success`() {
        mainCoroutineRule.runBlockingTest {
            whenever(weatherRepositoryLocal.getWeatherDataCount()).thenReturn(TestUtil.getWeatherDataCount())
            whenever(weatherRepositoryLocal.getWeatherInfoFromDb()).thenReturn(TestUtil.getWeatherInfo())
            // Pause coroutine to listen for loading initial state
            mainCoroutineRule.pauseDispatcher()

            homeViewModel.startPeriodicWorkRequest()
            // Check if status is loading
            assertThat(LiveDataTestUtil.getValue(homeViewModel.getHomeViewState())).isEqualTo(
                Loading
            )
            val count = homeViewModel.getWeatherDataCount().value
            val weatherInfo = homeViewModel.getCurrentWeather().value
            // Resume coroutine dispatcher to execute pending coroutine actions
            mainCoroutineRule.resumeDispatcher()
            assertThat(count).isEqualTo(TestUtil.weathetCount)
            assertThat(weatherInfo).isNotNull()
        }
    }
    @Test
    fun `test observer field value changed`() {
        mainCoroutineRule.runBlockingTest {
            whenever(weatherRepositoryLocal.getWeatherDataCount()).thenReturn(TestUtil.getWeatherDataCount())
            whenever(weatherRepositoryLocal.getWeatherInfoFromDb()).thenReturn(TestUtil.getWeatherInfo())
            // Pause coroutine to listen for loading initial state
            mainCoroutineRule.pauseDispatcher()

            homeViewModel.startPeriodicWorkRequest()
            // Check if status is loading
            assertThat(LiveDataTestUtil.getValue(homeViewModel.getHomeViewState())).isEqualTo(
                Loading
            )
            val count = homeViewModel.getWeatherDataCount().value
            val weatherInfo = homeViewModel.getCurrentWeather().value
            homeViewModel.setUIData(weatherInfo!!)
            val currentTemperature = homeViewModel.temperature
            val feelsLikeTemp = homeViewModel.feelsLikeTemp
            val minimumTemperature = homeViewModel.minTemp
            // Resume coroutine dispatcher to execute pending coroutine actions
            mainCoroutineRule.resumeDispatcher()
            assertThat(currentTemperature.get()).isEqualTo(28.69)
            assertThat(feelsLikeTemp.get()).isEqualTo(28.02)
            assertThat(minimumTemperature.get()).isEqualTo(27.78)
        }
    }
    @Test
    fun `test observer field value null `() {
        mainCoroutineRule.runBlockingTest {
            whenever(weatherRepositoryLocal.getWeatherDataCount()).thenReturn(TestUtil.getWeatherDataCount())
            whenever(weatherRepositoryLocal.getWeatherInfoFromDb()).thenReturn(TestUtil.getWeatherInfo())
            // Pause coroutine to listen for loading initial state
            mainCoroutineRule.pauseDispatcher()

            homeViewModel.startPeriodicWorkRequest()
            // Check if status is loading
            assertThat(LiveDataTestUtil.getValue(homeViewModel.getHomeViewState())).isEqualTo(
                Loading
            )
            val humidity = homeViewModel.humidity
            val pressuure = homeViewModel.pressure
            mainCoroutineRule.resumeDispatcher()
            assertThat(humidity.get()).isNull()
            assertThat(pressuure.get()).isNull()
        }
    }
}