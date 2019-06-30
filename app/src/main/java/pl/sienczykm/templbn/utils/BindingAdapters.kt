package pl.sienczykm.templbn.utils

import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import at.grabner.circleprogress.CircleProgressView
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.SmogStationModel
import pl.sienczykm.templbn.ui.common.BaseStationsAdapter

@BindingAdapter("adapter")
fun addStations(recyclerView: RecyclerView, stations: List<BaseStationModel>?) {
    val adapter = recyclerView.adapter as BaseStationsAdapter<*>
    adapter.submitList(stations ?: emptyList())
}

@BindingAdapter("airQuality")
fun setAirValueAndQuality(textView: TextView, valueAndQuality: Pair<Double, SmogStationModel.QualityIndex>?) {
    textView.text = valueAndQuality?.first?.toString()
    when (valueAndQuality?.second) {
        SmogStationModel.QualityIndex.VERY_GOOD -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_very_good))
        SmogStationModel.QualityIndex.GOOD -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_good))
        SmogStationModel.QualityIndex.MODERATE -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_moderate))
        SmogStationModel.QualityIndex.UNHEALTHY_SENSITIVE -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_unhealthy_sensitive))
        SmogStationModel.QualityIndex.UNHEALTHY -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_unhealthy))
        SmogStationModel.QualityIndex.HAZARDOUS -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_hazardous))
    }
}

@BindingAdapter("circleData", "maxValue", "unit")
fun setCircleData(
    circleProgressView: CircleProgressView,
    valueAndQuality: Pair<Double, SmogStationModel.QualityIndex>?,
    sensorType: SmogStationModel.SmogSensorType,
    unit: String
) {
    circleProgressView.maxValue = sensorType.maxUnhealthy.toFloat()
    circleProgressView.unit = unit
    if (valueAndQuality != null) {
        circleProgressView.setValue(valueAndQuality.first.toFloat())
        when (valueAndQuality.second) {
            SmogStationModel.QualityIndex.VERY_GOOD -> setColor(
                circleProgressView,
                R.color.quality_very_good
                )
            SmogStationModel.QualityIndex.GOOD -> setColor(
                circleProgressView,
                R.color.quality_good
                )
            SmogStationModel.QualityIndex.MODERATE -> setColor(
                circleProgressView,
                R.color.quality_moderate
                )
            SmogStationModel.QualityIndex.UNHEALTHY_SENSITIVE -> setColor(
                circleProgressView,
                R.color.quality_unhealthy_sensitive
                )
            SmogStationModel.QualityIndex.UNHEALTHY -> setColor(
                circleProgressView,
                R.color.quality_unhealthy
                )
            SmogStationModel.QualityIndex.HAZARDOUS -> setColor(
                circleProgressView,
                R.color.quality_hazardous
            )

        }
    }

}

fun setColor(circleProgressView: CircleProgressView, @ColorRes colorId: Int) {
    circleProgressView.setTextColor(circleProgressView.context.resources.getColor(colorId))
}
