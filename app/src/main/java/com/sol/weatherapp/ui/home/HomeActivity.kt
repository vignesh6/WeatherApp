package com.sol.weatherapp.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.sol.weatherapp.R
import com.sol.weatherapp.base.BaseActivity
import com.sol.weatherapp.databinding.ActivityHomeBinding
import com.sol.weatherapp.util.WeatherAppSharedPreference
import com.yariksoffice.connectivityplayground.connectivity.base.ConnectivityProvider
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import javax.inject.Inject

class HomeActivity : BaseActivity(), ConnectivityProvider.ConnectivityStateListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val provider: ConnectivityProvider by lazy { ConnectivityProvider.createProvider(this) }

    @Inject
    lateinit var sharedPreferences: WeatherAppSharedPreference
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        initHome()
    }

    private fun initHome() {
        cvMain.visibility = View.INVISIBLE
        cvTodaysDetail.visibility = View.INVISIBLE
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        binding.viewmodel = homeViewModel
        val hasInternet = provider.getNetworkState().hasInternet()
        homeViewModel.startPeriodicWorkRequest()
        homeViewModel.setNetworkState(hasInternet)
        //Getting status of work manager
        WorkManager.getInstance(this).getWorkInfosByTagLiveData(WEATHER_INFO_WORK).observe(this,
            Observer {
                if (it.isNotEmpty()) {
                    val status = it[0].state.name
                    Timber.d("Work Manager Status: $status")
                }
            })
        homeViewModel.getHomeViewState().observe(this, Observer {
            handleHomeState(it)
        })
        //observe on weather data count from local database
        homeViewModel.getWeatherDataCount().observe(this, Observer {
            if (it > 0) {
                observerWeatherData()
            }
        })
    }

    //observe weather data from local database
    private fun observerWeatherData() {
        homeViewModel.getCurrentWeather().observe(this, Observer {
            if (it != null) {
                cvMain.visibility = View.VISIBLE
                cvTodaysDetail.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                homeViewModel.setUIData(it)
            }
        })
    }

    private fun handleHomeState(it: HomeViewState) {
        when (it) {
            is Loading -> {
                progressBar.visibility = View.VISIBLE
            }
            is NetworkError -> {
                if (it != null)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        provider.addListener(this)
    }

    override fun onStop() {
        super.onStop()
        provider.removeListener(this)
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        if (::homeViewModel.isInitialized) {
            val hasInternet = state.hasInternet()
            homeViewModel.setNetworkState(hasInternet)
        }
    }

    private fun ConnectivityProvider.NetworkState.hasInternet(): Boolean {
        return (this as? ConnectivityProvider.NetworkState.ConnectedState)?.hasInternet == true
    }
}