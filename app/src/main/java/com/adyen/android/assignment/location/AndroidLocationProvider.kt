package com.adyen.android.assignment.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.adyen.android.assignment.domain.model.Location
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit

class AndroidLocationProvider(private val context: Context) {
    private val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun fetchUpdates(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(UPDATE_INTERVAL_SECS)
            fastestInterval = TimeUnit.SECONDS.toMillis(FASTEST_UPDATE_INTERVAL_SECS)
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val callBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation ?: return
                val userLocation = Location(
                    latitude = location.latitude,
                    longitude = location.longitude,
                )
                trySend(userLocation)
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                Log.d(TAG, "is location available ${locationAvailability.isLocationAvailable}")
                if (!locationAvailability.isLocationAvailable)
                    Toast.makeText(
                        context,
                        "Location Service is not available now or disabled",
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            //client.requestLocationUpdates(locationRequest, callBack, Looper.getMainLooper())
        }.addOnFailureListener {
            Log.d(TAG, "Exception : ${it.message}")
            Toast.makeText(
                context,
                "Location Service is not available now or disabled",
                Toast.LENGTH_LONG
            ).show()
        }
        client.lastLocation.addOnSuccessListener {
            it?.let {
                val userLocation = Location(
                    latitude = it.latitude,
                    longitude = it.longitude,
                )
                trySend(userLocation)
            }
        }.addOnFailureListener {
            Log.d(TAG, "Error in retrieving last location: ${it.message}")
        }
        client.requestLocationUpdates(locationRequest, callBack, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callBack) }
    }

    companion object {
        private const val UPDATE_INTERVAL_SECS = 10L
        private const val FASTEST_UPDATE_INTERVAL_SECS = 2L
        private const val TAG = "LocationProvider"
    }
}