package pl.sienczykm.templbn.ui.map.gms

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class StationMarker(
    private val position: LatLng,
    private val title: String,
    private val snippet: String,
    val icon: BitmapDescriptor,
) : ClusterItem {

    override fun getPosition() = position

    override fun getTitle() = title

    override fun getSnippet() = snippet
}