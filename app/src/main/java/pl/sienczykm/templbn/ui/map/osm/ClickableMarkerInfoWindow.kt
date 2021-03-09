package pl.sienczykm.templbn.ui.map.osm

import android.view.MotionEvent
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import pl.sienczykm.templbn.R

class ClickableMarkerInfoWindow(
    mapView: MapView,
    closeOnClick: Boolean = true,
    onTouchCallback: () -> Unit,
) :
    MarkerInfoWindow(R.layout.bonuspack_bubble, mapView) {
    init {
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                onTouchCallback()
                if (closeOnClick) close()
            }
            true
        }
    }
}