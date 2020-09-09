package pl.sienczykm.templbn.ui.map.gms

import android.annotation.SuppressLint
import android.content.res.Resources.NotFoundException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ui.IconGenerator
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentGoogleMapBinding
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.map.AirFilter
import pl.sienczykm.templbn.ui.map.BaseMapFragment
import pl.sienczykm.templbn.ui.map.MapNavigator
import pl.sienczykm.templbn.ui.map.WeatherFilter
import pl.sienczykm.templbn.utils.isLocationPermissionGranted
import pl.sienczykm.templbn.utils.isNightModeActive
import pl.sienczykm.templbn.utils.plusDrawable
import pl.sienczykm.templbn.utils.roundAndGetString
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

    private var clusterManager: ClusterManager<StationMarker>? = null

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

                googleMap.uiSettings.isMapToolbarEnabled = false

                if (requireContext().isLocationPermissionGranted()) {
                    googleMap.isMyLocationEnabled = true
                }

                clusterManager = ClusterManager<StationMarker>(requireContext(), googleMap).apply {
                    renderer = MarkerRenderer(requireContext(), googleMap, this)
                    googleMap.setOnCameraIdleListener(this)
                }

                updateMap()
            }
        }
    }

    override fun updateMap() {
        googleMap?.let { googleMap ->
            clusterManager?.clearItems()
            viewModel.stations.value?.let { stations ->
                stations.forEach { stationModel ->

                    val icon = when (stationModel) {
                        is AirStationModel -> airMarker(stationModel)
                        is WeatherStationModel -> weatherMarker(stationModel)
                        else -> null
                    } ?: return@forEach


//                    val marker = clusterManager!!.markerCollection.addMarker {
//                        position(LatLng(stationModel.latitude, stationModel.longitude))
//                        title(stationModel.getName())
//                        icon(icon)
//                        snippet(getString(stationModel.getStationSource()))
//                    }
//                    markerMap[marker] = stationModel

                    clusterManager?.addItem(
                        StationMarker(
                            LatLng(stationModel.latitude, stationModel.longitude),
                            stationModel.getName(),
                            getString(stationModel.getStationSource()),
                            icon,
                        ),
                    )
                }
            }
        }
    }

    private fun weatherMarker(stationModel: WeatherStationModel) =
        when (viewModel.weatherFilter) {
            WeatherFilter.NOTHING -> null
            WeatherFilter.LOCATION -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            WeatherFilter.TEMPERATURE -> stringMarker(stationModel,
                stationModel.getParsedTemperature(1)
                    ?.plus(getString(R.string.celsius_degree)))
            WeatherFilter.WIND -> stringMarker(stationModel, stationModel.getParsedWind(1)
                ?.plus(if (stationModel.convertWind) getString(R.string.km_per_hour) else getString(
                    R.string.m_per_sec))
                ?.plusDrawable(requireContext(), stationModel.getWindDir(true))
            )
            WeatherFilter.HUMIDITY -> stringMarker(stationModel, stationModel.getParsedHumidity(1)
                ?.plus(getString(R.string.percent)))
            WeatherFilter.RAIN_TODAY -> stringMarker(stationModel, stationModel.getParsedRain(1)
                ?.plus(getString(R.string.milliliters)))
        }

    private fun airMarker(stationModel: AirStationModel) =
        when (viewModel.airFilter) {
            AirFilter.NOTHING -> null
            AirFilter.LOCATION -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            AirFilter.PM10 -> stringMarker(stationModel,
                stationModel.getValue(AirStationModel.AirSensorType.PM10)
                    ?.roundAndGetString()?.plus(getString(R.string.microgram_per_cubic_metre)))
            AirFilter.PM25 -> stringMarker(stationModel,
                stationModel.getValue(AirStationModel.AirSensorType.PM25)
                    ?.roundAndGetString()?.plus(getString(R.string.microgram_per_cubic_metre)))
            AirFilter.O3 -> stringMarker(stationModel,
                stationModel.getValue(AirStationModel.AirSensorType.O3)
                    ?.roundAndGetString()?.plus(getString(R.string.microgram_per_cubic_metre)))
            AirFilter.NO2 -> stringMarker(stationModel,
                stationModel.getValue(AirStationModel.AirSensorType.NO2)
                    ?.roundAndGetString()?.plus(getString(R.string.microgram_per_cubic_metre)))
            AirFilter.SO2 -> stringMarker(stationModel,
                stationModel.getValue(AirStationModel.AirSensorType.SO2)
                    ?.roundAndGetString()?.plus(getString(R.string.microgram_per_cubic_metre)))
            AirFilter.C6H6 -> stringMarker(stationModel,
                stationModel.getValue(AirStationModel.AirSensorType.C6H6)
                    ?.roundAndGetString()?.plus(getString(R.string.microgram_per_cubic_metre)))
            AirFilter.CO -> stringMarker(stationModel,
                stationModel.getValue(AirStationModel.AirSensorType.CO)
                    ?.roundAndGetString()?.plus(getString(R.string.milligram_per_cubic_metre)))
        }

    private fun stringMarker(
        stationModel: BaseStationModel,
        value: CharSequence?,
    ): BitmapDescriptor? {

        if (stationModel.isDateObsoleteOrNull()) return null

        return value?.let {
            BitmapDescriptorFactory.fromBitmap(IconGenerator(requireContext()).run {
                // TODO style marker here?
                makeIcon(it)
            })
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        markerMap[marker]?.let { openStation(it) }
    }
}