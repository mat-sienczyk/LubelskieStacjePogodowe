package pl.sienczykm.templbn.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.sienczykm.templbn.db.model.StationModel
import pl.sienczykm.templbn.ui.common.BaseStationsAdapter

@BindingAdapter("adapter")
fun addStations(recyclerView: RecyclerView, stations: List<StationModel>?) {
    val adapter = recyclerView.adapter as BaseStationsAdapter<*>
    adapter.updateStations(stations ?: emptyList())
}