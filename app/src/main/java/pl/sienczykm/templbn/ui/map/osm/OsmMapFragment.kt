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
import pl.sienczykm.templbn.ui.map.BaseMapFragment
import pl.sienczykm.templbn.ui.map.MapNavigator
import pl.sienczykm.templbn.utils.getDrawableWithColor
import pl.sienczykm.templbn.utils.isLocationPermissionGranted
import pl.sienczykm.templbn.utils.isNightModeActive
import pl.sienczykm.templbn.utils.show


class OsmMapFragment : BaseMapFragment<FragmentOsmMapBinding>(), MapNavigator {

    companion object {
        fun newInstance(): OsmMapFragment {
            return OsmMapFragment()
        }
    }

    private var mapView: MapView? = null
    private var selectedStation: BaseStationModel? = null

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
            stations?.let { stations ->
                val infoView = ClickableMarkerInfoWindow(
                    mapView,
                    closeOnClick = false,
                    onTouchCallback = onMarkerWindowTouch()
                )

                stations.forEach { stationModel ->
                    mapView.overlays.add(Marker(mapView).apply {
                        position =
                            GeoPoint(stationModel.latitude, stationModel.longitude)
                        title = stationModel.getName()
                        icon = requireContext().getDrawableWithColor(
                            R.drawable.ic_map_marker,
                            if (stationModel is AirStationModel) Color.BLUE else Color.GREEN
                        )
                        setAnchor(
                            Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM
                        )
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
            }
        }
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
