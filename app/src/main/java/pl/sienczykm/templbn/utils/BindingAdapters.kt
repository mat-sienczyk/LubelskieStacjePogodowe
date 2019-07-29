package pl.sienczykm.templbn.utils

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.scale
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import at.grabner.circleprogress.CircleProgressView
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Picasso
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.list.common.BaseStationsAdapter


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

@BindingAdapter("stationName")
fun setStationName(textView: TextView, station: BaseStationModel) {

    val proportion = 1.2f

    textView.text = station.location?.let {
        SpannableStringBuilder()
            .bold { scale(proportion) { append(station.city + " ") } }
            .append(station.location)
    } ?: SpannableStringBuilder()
        .bold { scale(proportion) { append(station.city) } }
}

@BindingAdapter("load")
fun loadPicture(photoView: PhotoView, url: String?) {
    url?.let {
        Picasso.get()
            .load(it)
            .into(photoView)
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
            AirStationModel.AirQualityIndex.VERY_GOOD -> setCircleProgressTextColor(
                circleProgressView,
                R.color.quality_very_good
                )
            AirStationModel.AirQualityIndex.GOOD -> setCircleProgressTextColor(
                circleProgressView,
                R.color.quality_good
                )
            AirStationModel.AirQualityIndex.MODERATE -> setCircleProgressTextColor(
                circleProgressView,
                R.color.quality_moderate
                )
            AirStationModel.AirQualityIndex.UNHEALTHY_SENSITIVE -> setCircleProgressTextColor(
                circleProgressView,
                R.color.quality_unhealthy_sensitive
                )
            AirStationModel.AirQualityIndex.UNHEALTHY -> setCircleProgressTextColor(
                circleProgressView,
                R.color.quality_unhealthy
                )
            AirStationModel.AirQualityIndex.HAZARDOUS -> setCircleProgressTextColor(
                circleProgressView,
                R.color.quality_hazardous
            )
        }
    }

}

fun setCircleProgressTextColor(circleProgressView: CircleProgressView, @ColorRes colorId: Int) {
    circleProgressView.setTextColor(ContextCompat.getColor(circleProgressView.context, colorId))
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
                ContextCompat.getColor(textView.context, R.color.base_color),
                PorterDuff.Mode.SRC_IN
            )
        }
    }
}

@BindingAdapter("imageResource")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("oldDate")
fun setOldDate(textView: TextView, isDateOld: Boolean) {
    if (isDateOld) textView.apply {
        setTextColor(
            ContextCompat.getColor(
                textView.context,
                R.color.colorAccent
            )
        )
        text = textView.context.getString(R.string.station_date_old, text)
    } else textView.apply {
        setTextColor(
            ContextCompat.getColor(
                textView.context,
                R.color.base_color
            )
        )
    }
}
