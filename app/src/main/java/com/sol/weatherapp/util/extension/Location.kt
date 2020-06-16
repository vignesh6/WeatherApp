package com.sol.weatherapp.util.extension

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager


fun Context.isGPSEnabled() = (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)

fun Context.checkLocationPermission(): Boolean =
        this.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
