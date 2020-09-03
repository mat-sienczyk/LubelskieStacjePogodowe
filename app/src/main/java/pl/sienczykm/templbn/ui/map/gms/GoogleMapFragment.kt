package pl.sienczykm.templbn.ui.map.gms

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources.NotFoundException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentGoogleMapBinding
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.map.BaseMapFragment
import pl.sienczykm.templbn.ui.map.MapNavigator
import pl.sienczykm.templbn.ui.station.StationActivity
import pl.sienczykm.templbn.utils.isLocationPermissionGranted
import pl.sienczykm.templbn.utils.isNightModeActive
import timber.log.Timber


class GoogleMapFragment : BaseMapFragment<FragmentGoogleMapBinding>(),
    GoogleMap.OnInfoWindowClickListener, MapNavigator {

    companion object {
        fun newInstance(): GoogleMapFragment {
            return GoogleMapFragment()
        }
    }

    private var googleMap: GoogleMap? = null
    private val markerMap = hashMapOf<Marker, BaseStationModel>()

    override fun getLayoutId() =
        R.layout.fragment_google_map

    @SuppressLint("MissingPermission")
    override fun configureMap() {
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).apply {
            getMapAsync { googleMap ->
                this@GoogleMapFragment.googleMap = googleMap

                if (requireContext().isNightModeActive()) {
                    try {
                        val success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.night_map)
                        )

                        if (!success) {
                            Timber.e("Style parsing failed.")
                        }
                    } catch (e: NotFoundException) {
                        Timber.e("Can't find style. Error: ${e.localizedMessage}")
                    }
                }

                googleMap.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(
                            LatLng(
                                WeatherStationModel.PLAC_LITEWSKI.latitude,
                                WeatherStationModel.PLAC_LITEWSKI.longitude
                            ),
                            resources.getInteger(R.integer.map_zoom_lvl).toFloat()
                        )
                    )
                )


                googleMap.setOnInfoWindowClickListener(this@GoogleMapFragment)

                if (requireContext().isLocationPermissionGranted()) {
                    googleMap.isMyLocationEnabled = true
                }
                updateMap()
            }
        }
    }

    override fun updateMap() {
        googleMap?.let { googleMap ->
            stations?.let { stations ->
                stations.forEach { stationModel ->

//                    val icon = IconGenerator(requireContext()).makeIcon().apply { setC }

                    val icon =
                        BitmapDescriptorFactory.defaultMarker(if (stationModel is AirStationModel) BitmapDescriptorFactory.HUE_AZURE else BitmapDescriptorFactory.HUE_GREEN)

                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(stationModel.latitude, stationModel.longitude))
                            .title(stationModel.getName())
                            .icon(icon)
                            .snippet(getString(stationModel.getStationSource()))
                    )
                    markerMap[marker] = stationModel
                }
            }
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        val intent = Intent(requireContext(), StationActivity::class.java).apply {
            putExtra(
                StationActivity.STATION_TYPE_KEY,
                if (markerMap[marker] is WeatherStationModel) StationActivity.Type.WEATHER else StationActivity.Type.AIR
            )
            putExtra(StationActivity.STATION_ID_KEY, markerMap[marker]?.stationId)
        }
        startActivity(intent)
    }
}