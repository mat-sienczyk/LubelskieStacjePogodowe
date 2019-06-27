package pl.sienczykm.templbn.ui.map

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentMapBinding
import pl.sienczykm.templbn.db.model.SmogStationModel
import pl.sienczykm.templbn.db.model.StationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.main.MainActivity
import pl.sienczykm.templbn.ui.station.StationActivity
import pl.sienczykm.templbn.utils.isLocationPermissionGranted

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }

    //TODO przenieść do viewModelu?
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var binding: FragmentMapBinding
    //TODO przenieść do viewModelu?
    private val markerMap = hashMapOf<Marker, StationModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
//        binding.viewModelWeather = viewModelWeather
        binding.lifecycleOwner = this

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //TODO przenieść do viewModelu?
        map = googleMap

        val markerBoundsBuilder = LatLngBounds.Builder()

        WeatherStationModel.getStations().forEach { weatherStationModel ->
            val position = LatLng(weatherStationModel.latitude, weatherStationModel.longitude)
            val marker: Marker

            marker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(weatherStationModel.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .snippet(getString(R.string.station_title_weather))
            )

            markerMap[marker] = weatherStationModel
            markerBoundsBuilder.include(position)
        }

        SmogStationModel.getStations().forEach { smogStationModel ->
            val position = LatLng(smogStationModel.latitude, smogStationModel.longitude)
            val marker: Marker

            marker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(smogStationModel.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(getString(R.string.station_title_weather))
            )

            markerMap[marker] = smogStationModel
            markerBoundsBuilder.include(position)
        }

        map.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(
                    markerBoundsBuilder.build().center,
                    resources.getInteger(R.integer.map_zoom_lvl).toFloat()
                )
            )
        )

        map.setOnInfoWindowClickListener(this)

        if (activity.isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MainActivity.PERMISSIONS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    map.isMyLocationEnabled = true
                }
                return
            }
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        val intent = Intent(activity, StationActivity::class.java).apply {
            putExtra(
                StationActivity.STATION_TYPE_KEY,
                if (markerMap[marker] is WeatherStationModel) StationActivity.Type.WEATHER else StationActivity.Type.SMOG
            )
            putExtra(StationActivity.STATION_ID_KEY, markerMap[marker]?.stationId)
        }
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}