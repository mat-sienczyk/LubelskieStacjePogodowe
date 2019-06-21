package pl.sienczykm.templbn.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.SmogStationModel
import pl.sienczykm.templbn.db.model.StationModel
import pl.sienczykm.templbn.ui.common.BaseStationsAdapter

@BindingAdapter("adapter")
fun addStations(recyclerView: RecyclerView, stations: List<StationModel>?) {
    val adapter = recyclerView.adapter as BaseStationsAdapter<*>
    adapter.updateStations(stations ?: emptyList())
}

@BindingAdapter("airQuality")
fun setAirQualityColor(textView: TextView, qualityIndex: SmogStationModel.QualityIndex?) {
    when (qualityIndex) {
        SmogStationModel.QualityIndex.VERY_GOOD -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_very_good))
        SmogStationModel.QualityIndex.GOOD -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_good))
        SmogStationModel.QualityIndex.MODERATE -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_moderate))
        SmogStationModel.QualityIndex.UNHEALTHY_SENSITIVE -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_unhealthy_sensitive))
        SmogStationModel.QualityIndex.UNHEALTHY -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_unhealthy))
        SmogStationModel.QualityIndex.HAZARDOUS -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_hazardous))
    }
}