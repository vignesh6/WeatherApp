package com.sol.weatherapp.db

import android.content.Context
import android.os.Build
import androidx.room.Room
import com.android.tvmaze.utils.MainCoroutineRule
import com.android.tvmaze.utils.TestUtil
import com.google.common.truth.Truth.assertThat
import com.sol.weatherapp.data.repository.WeatherRepositoryLocal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,sdk = [Build.VERSION_CODES.P])
class WeatherRepositoryLocalTest {
    @Mock
    private lateinit var context: Context
    private lateinit var repository: WeatherRepositoryLocal
    private lateinit var weatherDb: WeatherDatabase
    private lateinit var weatherDao: WeatherDao
    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)
    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        weatherDb = Room.inMemoryDatabaseBuilder(
            context, WeatherDatabase::class.java
        ).build()
        weatherDao = weatherDb.weatherDao()
        repository = WeatherRepositoryLocal(weatherDao)
    }
    @Test
    fun testShowInsertionInDb() {
        repository.insertWeatherData(TestUtil.weatherData)
        mainCoroutineRule.runBlockingTest {
            testScope.launch {
                val count = repository.getWeatherDataCount().value
                assertThat(count).isEqualTo(1)
            }
        }
    }
    @Test
    fun `test inserted data is valid`(){
        repository.insertWeatherData(TestUtil.weatherData)
        mainCoroutineRule.runBlockingTest {
            testScope.launch {
                val weatherInfo = repository.getWeatherInfoFromDb().value
                assertThat(weatherInfo!!.country).isEqualTo("IN")
            }
        }
    }
    @Test
    fun `remove all data from db`(){
        runBlocking {
            repository.clearAllWeatherData()
            assertThat(repository.allData().isEmpty()).isTrue()
        }
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        weatherDb.close()
    }
}