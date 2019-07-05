package pl.sienczykm.templbn.utils

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import at.grabner.circleprogress.CircleProgressView
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.ui.common.BaseStationsAdapter

@BindingAdapter("addClickEffect")
fun addClickEffect(view: View, add: Boolean) {
    if (add) view.addClickEffect()
}

@BindingAdapter("adapter")
fun addStations(recyclerView: RecyclerView, stations: List<BaseStationModel>?) {
    val adapter = recyclerView.adapter as BaseStationsAdapter<*>
    adapter.submitList(stations ?: emptyList())
}

@BindingAdapter("airQuality")
fun setAirValueAndQuality(textView: TextView, valueAndAirQuality: Pair<Double, AirStationModel.AirQualityIndex>?) {
    textView.text = valueAndAirQuality?.first?.toString()
    when (valueAndAirQuality?.second) {
        AirStationModel.AirQualityIndex.VERY_GOOD -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_very_good))
        AirStationModel.AirQualityIndex.GOOD -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_good))
        AirStationModel.AirQualityIndex.MODERATE -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_moderate))
        AirStationModel.AirQualityIndex.UNHEALTHY_SENSITIVE -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_unhealthy_sensitive))
        AirStationModel.AirQualityIndex.UNHEALTHY -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_unhealthy))
        AirStationModel.AirQualityIndex.HAZARDOUS -> textView.setTextColor(textView.context.resources.getColor(R.color.quality_hazardous))
    }
}

@BindingAdapter("circleData", "maxValue", "unit")
fun setCircleData(
    circleProgressView: CircleProgressView,
    valueAndAirQuality: Pair<Double, AirStationModel.AirQualityIndex>?,
    sensorType: AirStationModel.AirSensorType,
    unit: String
) {
    circleProgressView.maxValue = sensorType.maxUnhealthy.toFloat()
    circleProgressView.unit = unit
    if (valueAndAirQuality != null) {
        circleProgressView.setValue(valueAndAirQuality.first.toFloat())
        when (valueAndAirQuality.second) {
            AirStationModel.AirQualityIndex.VERY_GOOD -> setColor(
                circleProgressView,
                R.color.quality_very_good
                )
            AirStationModel.AirQualityIndex.GOOD -> setColor(
                circleProgressView,
                R.color.quality_good
                )
            AirStationModel.AirQualityIndex.MODERATE -> setColor(
                circleProgressView,
                R.color.quality_moderate
                )
            AirStationModel.AirQualityIndex.UNHEALTHY_SENSITIVE -> setColor(
                circleProgressView,
                R.color.quality_unhealthy_sensitive
                )
            AirStationModel.AirQualityIndex.UNHEALTHY -> setColor(
                circleProgressView,
                R.color.quality_unhealthy
                )
            AirStationModel.AirQualityIndex.HAZARDOUS -> setColor(
                circleProgressView,
                R.color.quality_hazardous
            )

        }
    }

}

fun setColor(circleProgressView: CircleProgressView, @ColorRes colorId: Int) {
    circleProgressView.setTextColor(circleProgressView.context.resources.getColor(colorId))
}

@BindingAdapter(value = ["windDir", "value", "unit"], requireAll = false)
fun setWeatherData(
    textView: TextView,
    windDir: Double?,
    value: String?,
    unit: String?
) {
    if (windDir != null) {
        textView.text = textView.context.getString(
            R.string.wind_placeholder,
            windIntToDir(textView, windDir),
            value,
            unit ?: ""
        )
    } else {
        textView.text = textView.context.getString(R.string.data_placeholder, value, unit ?: "")
    }
}

fun windIntToDir(textView: TextView, windDirInt: Double?): String? {
    return when {
        // north N
        windDirInt == null -> null
        windDirInt <= 22 || windDirInt >= 338 -> textView.context.getString(R.string.north_wind)
        // north-east NE
        windDirInt <= 67 -> textView.context.getString(R.string.north_east_wind)
        // east E
        windDirInt <= 112 -> textView.context.getString(R.string.east_wind)
        // south-east SE
        windDirInt <= 157 -> textView.context.getString(R.string.south_east_wind)
        // south S
        windDirInt <= 202 -> textView.context.getString(R.string.south_wind)
        // south-west SW
        windDirInt <= 247 -> textView.context.getString(R.string.south_west_wind)
        // west W
        windDirInt <= 292 -> textView.context.getString(R.string.west_wind)
        // north-west NW
        windDirInt <= 337 -> textView.context.getString(R.string.north_west_wind)
        else -> null
    }
}
