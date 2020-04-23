package pl.sienczykm.templbn.utils

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LocationUpdates(
    private val context: Context,
    locationCallback: (Location?) -> Unit
) {

    private val locationHandlerImpl: LocationHandler =
        if (context.isGooglePlayServicesAvailable()) {
            GoogleLocationUpdateCallbackImpl(locationCallback)
        } else {
            AndroidLocationUpdateCallbackImpl(locationCallback)
        }

    fun stop() {
        locationHandlerImpl.stop()
    }


    private inner class GoogleLocationUpdateCallbackImpl(private val locationCallback: (Location?) -> Unit) :
        LocationHandler, LocationCallback() {

        init {
            LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(
                    LocationRequest.create()?.apply {
                        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                    },
                    this,
                    null
                )
        }

        override fun stop() {
            LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this)
        }

        override fun onLocationResult(locationResult: LocationResult?) {
            locationCallback(locationResult?.lastLocation)
        }
    }

    private inner class AndroidLocationUpdateCallbackImpl(private val locationCallback: (Location?) -> Unit) :
        LocationHandler, LocationListener {

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        init {
            val locationSources = hashSetOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER
            )

            locationManager.getProviders(true).forEach { provider: String ->
                if (locationSources.contains(provider)) {
                    try {
                        locationManager.requestLocationUpdates(
                            provider,
                            TimeUnit.SECONDS.toMillis(10),
                            15f,
                            this
                        )
                    } catch (ex: SecurityException) {
                    }
                }
            }
        }

        override fun stop() {
            locationManager.removeUpdates(this)
        }

        override fun onLocationChanged(location: Location?) {
            locationCallback(location)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            Timber.d("onStatusChanged")
        }

        override fun onProviderEnabled(provider: String?) {
            Timber.d("onProviderEnabled")
        }

        override fun onProviderDisabled(provider: String?) {
            Timber.d("onProviderDisabled")
        }

    }
}

interface LocationHandler {
    fun stop()
}
