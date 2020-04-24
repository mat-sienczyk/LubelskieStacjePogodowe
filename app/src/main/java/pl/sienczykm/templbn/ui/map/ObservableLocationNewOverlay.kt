package pl.sienczykm.templbn.ui.map

import android.location.Location
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class ObservableLocationNewOverlay(
    mapView: MapView,
    private val setLocationCallback: (Location) -> Unit
) : MyLocationNewOverlay(mapView) {
    override fun setLocation(location: Location) {
        super.setLocation(location)
        setLocationCallback(location)
    }
}