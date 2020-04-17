package pl.sienczykm.templbn.ui.map

import android.graphics.Color
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
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pl.sienczykm.templbn.BuildConfig
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentOsmMapBinding
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.utils.getDrawableWithColor
import pl.sienczykm.templbn.utils.isLocationPermissionGranted


class OsmMapFragment : Fragment() {

    companion object {
        fun newInstance(): OsmMapFragment {
            return OsmMapFragment()
        }
    }

    private lateinit var binding: FragmentOsmMapBinding
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_osm_map, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        mapView = binding.mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }

        mapView.controller.apply {
            setZoom(resources.getInteger(R.integer.map_zoom_lvl).toDouble() * 1.3)
            setCenter(
                GeoPoint(
                    WeatherStationModel.PLAC_LITEWSKI.latitude,
                    WeatherStationModel.PLAC_LITEWSKI.longitude
                )
            )
        }

        val overlays = arrayListOf<Overlay>()

        WeatherStationModel.getStations().forEach { weatherStationModel ->
            overlays.add(Marker(mapView).apply {
                position = GeoPoint(weatherStationModel.latitude, weatherStationModel.longitude)
                title = weatherStationModel.getName()
                icon = requireContext().getDrawableWithColor(R.drawable.ic_map_marker, Color.GREEN)
                snippet = getString(R.string.station_title_weather)
            })
        }

        AirStationModel.getStations().forEach { airStationModel ->
            overlays.add(Marker(mapView).apply {
                position = GeoPoint(airStationModel.latitude, airStationModel.longitude)
                title = airStationModel.getName()
                icon = requireContext().getDrawableWithColor(R.drawable.ic_map_marker, Color.BLUE)
                snippet = getString(R.string.station_title_air)
            })
        }

        overlays.add(CompassOverlay(
            requireContext(),
            InternalCompassOrientationProvider(requireContext()),
            mapView
        ).apply {
            enableCompass()
        })

        if (requireContext().isLocationPermissionGranted()) {
            overlays.add(MyLocationNewOverlay(
                GpsMyLocationProvider(requireContext()),
                mapView
            ).apply {
                enableMyLocation()
            })
        }

        mapView.overlays.addAll(overlays)

        return binding.root
    }

//    fun onMarker(marker: Marker) {
//        val intent = Intent(requireContext(), StationActivity::class.java).apply {
//            putExtra(
//                StationActivity.STATION_TYPE_KEY,
//                if (markerMap[marker] is WeatherStationModel) StationActivity.Type.WEATHER else StationActivity.Type.AIR
//            )
//            putExtra(StationActivity.STATION_ID_KEY, markerMap[marker]?.stationId)
//        }
//        startActivity(intent)
//    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}