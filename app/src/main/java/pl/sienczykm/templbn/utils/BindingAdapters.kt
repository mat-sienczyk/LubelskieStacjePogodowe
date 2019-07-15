package pl.sienczykm.templbn.utils

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import at.grabner.circleprogress.CircleProgressView
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.common.BaseStationsAdapter


@BindingAdapter("addClickEffect")
fun addClickEffect(view: View, add: Boolean) {
    if (add) view.addClickEffect()
}

@BindingAdapter("adapter")
fun addStations(recyclerView: RecyclerView, stations: List<BaseStationModel>?) {
    val adapter = recyclerView.adapter as BaseStationsAdapter<*>
    // need to create deep copy of station list because of later problems with ListAdapter.DiffUtil
    adapter.submitList(stations?.map { stationModel -> stationModel.copy() } ?: emptyList())
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
    textView.text = textView.context.getString(R.string.data_placeholder, value, unit ?: "")
    windDir?.let {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            WeatherStationModel.windIntToDir(
                windDir,
                true
            ), 0, 0, 0
        )
        for (drawable: Drawable? in textView.compoundDrawables) {
            drawable?.colorFilter = PorterDuffColorFilter(
                textView.context.resources.getColor(R.color.grey),
                PorterDuff.Mode.SRC_IN
            )
        }
    }
}

@BindingAdapter("imageResource")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}
