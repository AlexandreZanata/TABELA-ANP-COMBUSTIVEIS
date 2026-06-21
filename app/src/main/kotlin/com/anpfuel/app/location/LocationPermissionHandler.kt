package com.anpfuel.app.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.anpfuel.domain.valueobject.DeviceLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * One-shot device location access for UC-012 onboarding (coordinates are not persisted).
 */
@Singleton
class LocationPermissionHandler @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(): DeviceLocation? {
        if (!hasLocationPermission()) {
            return null
        }

        val locationManager = context.getSystemService(LocationManager::class.java)
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: return null

        return DeviceLocation.of(location.latitude, location.longitude)
    }
}
