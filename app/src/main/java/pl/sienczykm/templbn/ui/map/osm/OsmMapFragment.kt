package pl.sienczykm.templbn.ui.map.osm

import android.graphics.Color
import android.location.Location
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay
import pl.sienczykm.templbn.BuildConfig
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentOsmMapBinding
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.map.AirFilter
import pl.sienczykm.templbn.ui.map.BaseMapFragment
import pl.sienczykm.templbn.ui.map.MapNavigator
import pl.sienczykm.templbn.ui.map.WeatherFilter
import pl.sienczykm.templbn.utils.*


class OsmMapFragment : BaseMapFragment<FragmentOsmMapBinding>(), MapNavigator {

    companion object {
        fun newInstance(): OsmMapFragment {
            return OsmMapFragment()
        }
    }

    private var mapView: MapView? = null
    private var selectedStation: BaseStationModel? = null
    private val markers = arrayListOf<Marker>()

    override fun getLayoutId() =
        R.layout.fragment_osm_map

    override fun configureMap() {
        Configuration.getInstance().apply {
            userAgentValue = BuildConfig.APPLICATION_ID
        }

        mapView = binding.mapView.also { mapView ->
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)

            if (requireContext().isNightModeActive())
                mapView.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)

            mapView.controller.apply {
                setZoom(9.3)
                setCenter(
                    GeoPoint(
                        WeatherStationModel.PLAC_LITEWSKI.latitude,
                        WeatherStationModel.PLAC_LITEWSKI.longitude
                    )
                )
            }

            if (requireContext().isLocationPermissionGranted()) {
                mapView.overlays.add(ObservableLocationNewOverlay(mapView) { location: Location ->
                    binding.myLocationButton.apply {
                        show()
                        setOnClickListener {
                            mapView.controller.animateTo(GeoPoint(location), 16.0, null)
                        }
                    }
                }.apply {
                    enableMyLocation()
                })
            }
            updateMap()
        }
    }

    override fun updateMap() {
        mapView?.let { mapView ->
            mapView.apply {
                overlays.removeAll(markers)
                markers.clear()
                invalidate()
            }
            viewModel.stations.value?.let { stations ->
                val infoView = ClickableMarkerInfoWindow(
                    mapView,
                    closeOnClick = false,
                    onTouchCallback = onMarkerWindowTouch()
                )

                stations.forEach { stationModel ->

                    val icon = when (stationModel) {
                        is AirStationModel -> airLocationMarker()
                        is WeatherStationModel -> weatherLocationMarker()
                        else -> null
                    }

                    val textIcon = when (stationModel) {
                        is AirStationModel -> airStringMarker(stationModel)
                        is WeatherStationModel -> weatherStringMarker(stationModel)
                        else -> null
                    }

                    if (icon == null && textIcon == null) return@forEach

                    markers.add(Marker(mapView).apply {
                        position =
                            GeoPoint(stationModel.latitude, stationModel.longitude)
                        title = stationModel.getName()
                        if (icon != null) {
                            setIcon(icon)
                            setAnchor(
                                Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM
                            )
                        }
                        if (textIcon != null) setTextIcon(textIcon)
                        snippet = getString(stationModel.getStationSource())
                        infoWindow = infoView
                        setOnMarkerClickListener { marker, _ ->
                            marker.showInfoWindow()
                            mapView.controller.animateTo(marker.position)
                            selectedStation = stationModel
                            true
                        }
                    })
                }
                mapView.apply {
                    overlays.addAll(markers)
                    invalidate()
                }
            }
        }
    }

    private fun weatherLocationMarker() =
        when (viewModel.weatherFilter) {
            WeatherFilter.LOCATION -> requireContext().getDrawableWithColor(
                R.drawable.ic_map_marker, Color.GREEN)
            else -> null
        }

    private fun airLocationMarker() =
        when (viewModel.airFilter) {
            AirFilter.LOCATION -> requireContext().getDrawableWithColor(
                R.drawable.ic_map_marker, Color.BLUE)
            else -> null
        }

    private fun weatherStringMarker(stationModel: WeatherStationModel) =
        when (viewModel.weatherFilter) {
            WeatherFilter.NOTHING -> null
            WeatherFilter.TEMPERATURE -> stringMarker(stationModel,
                stationModel.getParsedTemperature(1)
                    ?.plus(getString(R.string.celsius_degree)))
            WeatherFilter.WIND -> stringMarker(stationModel, stationModel.getParsedWind(1)
                ?.plus(if (stationModel.convertWind) getString(R.string.km_per_hour) else getString(
                    R.string.m_per_sec))
            )
            WeatherFilter.HUMIDITY -> stringMarker(stationModel, stationModel.getParsedHumidity(1)
                ?.plus(getString(R.string.percent)))
            WeatherFilter.RAIN_TODAY -> stringMarker(stationModel, stationModel.getParsedRain(1)
                ?.plus(getString(R.string.milliliters)))
            else -> null
        }


    private fun airStringMarker(stationModel: AirStationModel) = when (viewModel.airFilter) {
        AirFilter.NOTHING -> null
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
        else -> null
    }


    private fun stringMarker(
        stationModel: BaseStationModel,
        value: String?,
    ): String? {
        if (stationModel.isDateObsoleteOrNull()) return null
        return value
    }

    private fun onMarkerWindowTouch(): () -> Unit = {
        selectedStation?.let { openStation(it) }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }
}
