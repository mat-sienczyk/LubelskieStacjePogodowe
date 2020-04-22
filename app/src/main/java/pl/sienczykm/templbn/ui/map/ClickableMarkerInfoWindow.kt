package pl.sienczykm.templbn.ui.map

import android.view.MotionEvent
import org.osmdroid.library.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

class ClickableMarkerInfoWindow<T>(
    mapView: MapView,
    objectClicked: T,
    onTouchCallback: (T) -> Unit
) :
    MarkerInfoWindow(R.layout.bonuspack_bubble, mapView) {
    init {
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                onTouchCallback(objectClicked)
                close()
            }
            true
        }
    }
}