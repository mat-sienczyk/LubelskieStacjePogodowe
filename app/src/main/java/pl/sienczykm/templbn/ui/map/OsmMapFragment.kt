package pl.sienczykm.templbn.ui.map

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pl.sienczykm.templbn.BuildConfig
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentOsmMapBinding
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.station.StationActivity
import pl.sienczykm.templbn.utils.getDrawableWithColor
import pl.sienczykm.templbn.utils.isLocationPermissionGranted
import pl.sienczykm.templbn.utils.isNightModeActive
import pl.sienczykm.templbn.utils.show
import java.io.File


class OsmMapFragment : Fragment() {

    companion object {
        fun newInstance(): OsmMapFragment {
            return OsmMapFragment()
        }
    }

    private lateinit var binding: FragmentOsmMapBinding
    private lateinit var mapView: MapView

    //TODO move to viewModel?
    private var selectedStation: BaseStationModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_osm_map, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        Configuration.getInstance().apply {
            userAgentValue = BuildConfig.APPLICATION_ID
            // workaround for API 29
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                osmdroidBasePath = File(requireContext().filesDir, "osmdroid").apply {
                    mkdirs()
                }
                osmdroidTileCache = File(osmdroidBasePath, "tiles").apply {
                    mkdirs()
                }
            }
        }

        mapView = binding.mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }

        if (requireContext().isNightModeActive())
            mapView.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)

        mapView.controller.apply {
            setZoom(resources.getInteger(R.integer.map_zoom_lvl).toDouble() * 1.35)
            setCenter(
                GeoPoint(
                    WeatherStationModel.PLAC_LITEWSKI.latitude,
                    WeatherStationModel.PLAC_LITEWSKI.longitude
                )
            )
        }

        val overlays = arrayListOf<Overlay>()

        val infoView = ClickableMarkerInfoWindow(
            mapView,
            closeOnClick = false,
            onTouchCallback = onMarkerWindowTouch()
        )

        WeatherStationModel.getStations().forEach { weatherStationModel ->
            overlays.add(Marker(mapView).apply {
                position = GeoPoint(weatherStationModel.latitude, weatherStationModel.longitude)
                title = weatherStationModel.getName()
                icon = requireContext().getDrawableWithColor(R.drawable.ic_map_marker, Color.GREEN)
                setAnchor(
                    Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM
                )
                snippet = getString(R.string.station_title_weather)
                infoWindow = infoView
                setOnMarkerClickListener { marker, _ ->
                    marker.showInfoWindow()
                    mapView.controller.animateTo(marker.position)
                    selectedStation = weatherStationModel
                    true
                }
            })
        }

        AirStationModel.getStations().forEach { airStationModel ->
            overlays.add(Marker(mapView).apply {
                position = GeoPoint(airStationModel.latitude, airStationModel.longitude)
                title = airStationModel.getName()
                icon = requireContext().getDrawableWithColor(R.drawable.ic_map_marker, Color.BLUE)
                setAnchor(
                    Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM
                )
                snippet = getString(R.string.station_title_air)
                infoWindow = infoView
                setOnMarkerClickListener { marker, _ ->
                    marker.showInfoWindow()
                    mapView.controller.animateTo(marker.position)
                    selectedStation = airStationModel
                    true
                }
            })
        }

//        overlays.add(CompassOverlay(
//            requireContext(),
//            InternalCompassOrientationProvider(requireContext()),
//            mapView
//        ).apply {
//            enableCompass()
//        })

        if (requireContext().isLocationPermissionGranted()) {
            overlays.add(MyLocationNewOverlay(
                GpsMyLocationProvider(requireContext()),
                mapView
            ).apply {
                enableMyLocation()
                binding.myLocationButton.apply {
                    show()
                    setOnClickListener {
                        mapView.controller.animateTo(myLocation, 16.0, null)
                    }
                }
            })
        }

        mapView.overlays.addAll(overlays)

        return binding.root
    }

    private fun onMarkerWindowTouch(): () -> Unit = {
        selectedStation?.let { station: BaseStationModel ->
            startActivity(Intent(requireContext(), StationActivity::class.java).apply {
                putExtra(
                    StationActivity.STATION_TYPE_KEY,
                    if (station is WeatherStationModel) StationActivity.Type.WEATHER else StationActivity.Type.AIR
                )
                putExtra(StationActivity.STATION_ID_KEY, station.stationId)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}