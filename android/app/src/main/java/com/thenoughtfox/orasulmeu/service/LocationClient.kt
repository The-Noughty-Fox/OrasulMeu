package com.thenoughtfox.orasulmeu.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.thenoughtfox.orasulmeu.utils.isLocationPermissionGranted

class LocationClient(private val context: Context, val onSendLocation: (Location) -> Unit) :
    LocationCallback() {

    private val locationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private var locationRequest: LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

    private var timeInterval = 1000L
    private var minimalDistance = 0f

    private fun createRequest(): LocationRequest =
        // New builder
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setMinUpdateDistanceMeters(minimalDistance)
            setWaitForAccurateLocation(true)
        }.build()

    fun changeRequest(timeInterval: Long, minimalDistance: Float) {
        this.timeInterval = timeInterval
        this.minimalDistance = minimalDistance
        createRequest()
        stopLocationTracking()
        startLocationTracking()
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (!isLocationPermissionGranted(context)) {
            // Permission is not granted
            return
        }

        locationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    onSendLocation(it)
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun startLocationTracking() {
        if (!isLocationPermissionGranted(context)) {
            // Permission is not granted
            return
        }

        locationClient.requestLocationUpdates(
            locationRequest, this, Looper.getMainLooper()
        )
    }

    fun stopLocationTracking() {
        locationClient.flushLocations()
        locationClient.removeLocationUpdates(this)
    }

    override fun onLocationResult(location: LocationResult) {
        location.lastLocation?.let {
            onSendLocation(it)
        }
    }

}