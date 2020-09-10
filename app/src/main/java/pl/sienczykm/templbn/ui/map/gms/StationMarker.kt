package pl.sienczykm.templbn.ui.map.gms

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import pl.sienczykm.templbn.db.model.BaseStationModel

class StationMarker(
    val stationModel: BaseStationModel,
    private val snippet: String,
    val icon: BitmapDescriptor,
) : ClusterItem {

    override fun getPosition() = LatLng(stationModel.latitude, stationModel.longitude)

    override fun getTitle() = stationModel.getName()

    override fun getSnippet() = snippet
}