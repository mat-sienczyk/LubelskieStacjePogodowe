package pl.sienczykm.templbn.ui.map.gms

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class MarkerRenderer(
    context: Context,
    googleMap: GoogleMap,
    clusterManager: ClusterManager<StationMarker>,
) : DefaultClusterRenderer<StationMarker>(context, googleMap, clusterManager) {
    override fun onBeforeClusterItemRendered(item: StationMarker, markerOptions: MarkerOptions) {
        markerOptions.apply {
            position(item.position)
            title(item.title)
            icon(item.icon)
            snippet(item.snippet)
        }
        super.onBeforeClusterItemRendered(item, markerOptions)
    }

}