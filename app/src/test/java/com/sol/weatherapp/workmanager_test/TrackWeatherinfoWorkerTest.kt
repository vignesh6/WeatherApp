package com.sol.weatherapp.workmanager_test

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.android.tvmaze.utils.MainCoroutineRule
import com.sol.weatherapp.data.repository.WeatherRepositoryRemote
import com.sol.weatherapp.ui.home.WEATHER_INFO_WORK
import com.sol.weatherapp.util.TrackWeatherInfoWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit


const val latitude = "12.908710"
const val longitude = "77.600620"
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TrackWeatherinfoWorkerTest {
    private lateinit var context: Context
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    lateinit var repository: WeatherRepositoryRemote

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        context = InstrumentationRegistry.getInstrumentation().getTargetContext()
    }

    @Test
    fun testRefreshMainDataWork() {
        // Get the ListenableWorker
        val worker = TestListenableWorkerBuilder<TrackWeatherInfoWorker>(context).build()
        // Start the work synchronously
        val result = worker.startWork().get()
        assertThat(result, `is`(ListenableWorker.Result.success()))
    }
    @Test
    fun testWithConstraints(){
        val config: Configuration = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(
            context, config
        )
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val weatherInfoWorker =
            PeriodicWorkRequestBuilder<TrackWeatherInfoWorker>(15, TimeUnit.MINUTES).addTag(
                WEATHER_INFO_WORK
            ).setConstraints(constraints).build()
        val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniquePeriodicWork(
                WEATHER_INFO_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            weatherInfoWorker).result.get()
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        testDriver!!.setAllConstraintsMet(weatherInfoWorker.id)

        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(weatherInfoWorker.id).get()
        // Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.RUNNING))
    }
    @Test
    fun testPeriodicDelayMet(){
        val config: Configuration = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(
            context, config
        )
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val weatherInfoWorker =
            PeriodicWorkRequestBuilder<TrackWeatherInfoWorker>(15, TimeUnit.MINUTES).addTag(
                WEATHER_INFO_WORK
            ).setConstraints(constraints).build()
        val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniquePeriodicWork(
                WEATHER_INFO_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            weatherInfoWorker).result.get()
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(weatherInfoWorker.id).get()
        // Assert
        testDriver!!.setPeriodDelayMet(weatherInfoWorker.id)
        assertThat(workInfo.state, `is` (WorkInfo.State.ENQUEUED))
    }
}