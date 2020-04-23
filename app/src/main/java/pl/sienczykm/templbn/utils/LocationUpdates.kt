package pl.sienczykm.templbn.utils

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class LocationUpdates(
    private val context: Context,
    locationCallback: (Location?) -> Unit
) {

    private val locationHandlerImpl: LocationHandler =
        if (context.isGooglePlayServicesAvailableAndEnabled()) {
            GoogleLocationUpdateCallbackImpl(locationCallback)
        } else {
            AndroidLocationUpdateCallbackImpl(locationCallback)
        }

    fun stop() {
        locationHandlerImpl.stop()
    }


    private inner class GoogleLocationUpdateCallbackImpl(private val locationCallback: (Location?) -> Unit) :
        LocationHandler, LocationCallback() {

        val locationProviderClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        init {
            locationProviderClient
                .requestLocationUpdates(
                    LocationRequest.create()?.apply {
                        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                    },
                    this,
                    null
                )
        }

        override fun stop() {
            locationProviderClient.removeLocationUpdates(this)
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
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }

    }
}

interface LocationHandler {
    fun stop()
}
