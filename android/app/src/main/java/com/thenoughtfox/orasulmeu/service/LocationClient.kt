package com.thenoughtfox.orasulmeu.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.thenoughtfox.orasulmeu.utils.isLocationPermissionGranted

class LocationClient(
    private val context: Context,
    private val onSendLocation: (Location) -> Unit
) : LocationCallback() {

    private var locationCallback: LocationCallback? = null

    private val locationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private var locationRequest: LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

    fun startLocationRequest(onSuccess: () -> Unit, onFailure: (ResolvableApiException) -> Unit) {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingClient = LocationServices.getSettingsClient(context)
        val task = settingClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            onSuccess()
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    onFailure(e)
                } catch (_: IntentSender.SendIntentException) {
                    //ignore the error
                }
            }
        }
    }

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
        if (!isLocationPermissionGranted()) {
            // Permission is not granted
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation = locationResult.lastLocation ?: return
                Log.e("LocationClient", "Last location: $lastLocation")
                locationClient.removeLocationUpdates(locationCallback!!)
                onSendLocation(lastLocation)
            }
        }

        locationClient.requestLocationUpdates(
            locationRequest, locationCallback!!, Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    fun startLocationTracking() {
        if (!isLocationPermissionGranted()) {
            // Permission is not granted
            return
        }

        locationClient.requestLocationUpdates(locationRequest, this, Looper.getMainLooper())
    }

    fun stopLocationTracking() {
        locationClient.flushLocations()
        locationClient.removeLocationUpdates(this)
        locationCallback?.let {
            locationClient.removeLocationUpdates(it)
        }
    }

    fun isLocationPermissionGranted(): Boolean = isLocationPermissionGranted(context)

    override fun onLocationResult(location: LocationResult) {
        location.lastLocation?.let {
            onSendLocation(it)
        }
    }

}