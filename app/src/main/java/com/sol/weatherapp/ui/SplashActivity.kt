package com.sol.weatherapp.ui

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.sol.weatherapp.R
import com.sol.weatherapp.base.BaseActivity
import com.sol.weatherapp.ui.home.HomeActivity
import com.sol.weatherapp.util.WeatherAppSharedPreference
import com.sol.weatherapp.util.extension.checkLocationPermission
import com.sol.weatherapp.util.extension.isGPSEnabled
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject

const val LOCATION_PERMISSION_REQUEST_CODE = 1000
const val REQUEST_CHECK_SETTINGS = 1001

class SplashActivity : BaseActivity() {
    @Inject
    lateinit var sharedPreferences: WeatherAppSharedPreference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    private var isLocationUpdateRequired = false
    private lateinit var locationCallback: LocationCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initLocationCallbacks()
        checkAllConditionsMet()
    }

    private fun checkAllConditionsMet() {
        if (this.isGPSEnabled() && this.checkLocationPermission()) {
            getLocation()
        } else {
            initViews()
        }
    }

    private fun initLocationCallbacks() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.size > 0) {
                    for (location in locationResult.locations) {
                        val mLastLocation: Location = locationResult.lastLocation
                        storeLocationInPreference(mLastLocation)
                    }
                }
            }
        }
    }

    private fun initViews() {
        txtAgree.visibility = View.VISIBLE
        txtPermissionDes.visibility = View.VISIBLE
        txtAgree.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (this.isGPSEnabled()) {
                    getLocation()
                } else {
                    showLocationEnableDialog()
                }
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun getLocation() {
        if (this.checkLocationPermission())
            progressBar.visibility = View.VISIBLE
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    storeLocationInPreference(location)
                } else {
                    //If last known location is null get location updates
                    startLocationUpdate()
                }

            }
    }

    private fun storeLocationInPreference(location: Location) {
        sharedPreferences.saveLocation(location)
        launchHomeActivity()
    }

    private fun startLocationUpdate() {
        isLocationUpdateRequired = true
        if (this.checkLocationPermission() && this.isGPSEnabled())
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback, Looper.getMainLooper()
            )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    if (this.isGPSEnabled()) {
                        getLocation()
                    } else {
                        showLocationEnableDialog()
                    }
                } else {
                    requestPermissionWithDialog()
                }
                return
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getLocation()
                } else {
                    Toast.makeText(this, getString(R.string.enable_gps), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestPermissionWithDialog() {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_name))
        builder.setMessage(R.string.permission_describtion_short)
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    dialog.dismiss()
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss()
                }
            }
        }
        builder.setPositiveButton(getString(R.string.ok), dialogClickListener)
        builder.setNegativeButton(getString(R.string.cancel_text), dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }

    private fun launchHomeActivity() {
        progressBar.visibility = View.GONE
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLocationEnableDialog() {
        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            getLocation()
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }
}