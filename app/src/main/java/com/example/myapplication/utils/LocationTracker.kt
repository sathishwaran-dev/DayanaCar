package com.example.myapplication.utils

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LocationData(
    val currentLat: Double = 0.0,
    val currentLng: Double = 0.0,
    val currentSpeedKmh: Double = 0.0,
    val totalDistanceKm: Double = 0.0,
    val maxSpeedKmh: Double = 0.0,
    val avgSpeedKmh: Double = 0.0,
    val isTracking: Boolean = false,
    val startTime: Long = 0,
    val elapsedTimeSeconds: Long = 0
)

class LocationTracker(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _locationData = MutableStateFlow(LocationData())
    val locationData: StateFlow<LocationData> = _locationData.asStateFlow()

    private var lastLocation: Location? = null
    private var totalDistanceMeters: Float = 0f
    private var maxSpeedMps: Float = 0f
    private var speedCount: Int = 0
    private var speedSumMps: Float = 0f
    private var startTimeMs: Long = 0

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            for (location in result.locations) {
                updateLocation(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        lastLocation = null
        totalDistanceMeters = 0f
        maxSpeedMps = 0f
        speedCount = 0
        speedSumMps = 0f
        startTimeMs = System.currentTimeMillis()

        _locationData.value = LocationData(
            isTracking = true,
            startTime = startTimeMs
        )

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            3000L
        ).setMinUpdateIntervalMillis(1500L)
            .setGranularity(Granularity.GRANULARITY_FINE)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateLocation(location: Location) {
        val lastLoc = lastLocation
        if (lastLoc != null) {
            val distance = lastLoc.distanceTo(location)
            if (distance > 2.0) { // filter noise
                totalDistanceMeters += distance
            }
        }
        lastLocation = location

        val speedMps = if (location.hasSpeed()) location.speed else 0f
        if (speedMps > maxSpeedMps) {
            maxSpeedMps = speedMps
        }
        speedSumMps += speedMps
        speedCount++

        val currentSpeedKmh = (speedMps * 3.6).coerceAtLeast(0.0)
        val maxSpeedKmh = (maxSpeedMps * 3.6).coerceAtLeast(0.0)
        val avgSpeedMps = if (speedCount > 0) speedSumMps / speedCount else 0f
        val avgSpeedKmh = (avgSpeedMps * 3.6).coerceAtLeast(0.0)
        val totalDistanceKm = totalDistanceMeters / 1000.0
        val elapsedSec = (System.currentTimeMillis() - startTimeMs) / 1000

        _locationData.value = LocationData(
            currentLat = location.latitude,
            currentLng = location.longitude,
            currentSpeedKmh = currentSpeedKmh,
            totalDistanceKm = totalDistanceKm,
            maxSpeedKmh = maxSpeedKmh,
            avgSpeedKmh = avgSpeedKmh,
            isTracking = true,
            startTime = startTimeMs,
            elapsedTimeSeconds = elapsedSec
        )
    }

    fun stopTracking(): LocationData {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val finalData = _locationData.value.copy(isTracking = false)
        _locationData.value = LocationData(isTracking = false)
        return finalData
    }
}
