package pl.sienczykm.templbn.ui.map.osm

import android.view.MotionEvent
import org.osmdroid.library.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

class ClickableMarkerInfoWindow(
    mapView: MapView,
    closeOnClick: Boolean = true,
    onTouchCallback: () -> Unit
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