package pl.sienczykm.templbn.utils

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.color
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import at.grabner.circleprogress.CircleProgressView
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.clearCache
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

@BindingAdapter("load")
fun loadPicture(photoView: PhotoView, url: String?) {
    url?.let {
        Picasso.get().apply {
            load(it)
                .into(photoView, object : Callback {
                    override fun onSuccess() {
                        if (photoView.context.isNightModeActive()) photoView.invertColors()
                    }

                    override fun onError(e: Exception?) {
                        this@apply.clearCache(url)
                    }
                })
        }
    }
}

@BindingAdapter("valueAndAirQuality", "sensorType")
fun setAirPercent(
    textView: TextView,
    valueAndAirQuality: Pair<Double, AirStationModel.AirQualityIndex>?,
    sensorType: AirStationModel.AirSensorType
) {
    if (valueAndAirQuality != null) {
        val rawPercent = (valueAndAirQuality.first * 100) / sensorType.maxGood
        textView.apply {
            text = SpannableStringBuilder()
                .append(rawPercent.roundAndGetString())
                .append(context.getString(R.string.percent).trim())
            setColor(valueAndAirQuality.second.color)
        }
    }
}

@BindingAdapter("valueAndAirQuality", "sensorType", "unit")
fun setCircleData(
    circleProgressView: CircleProgressView,
    valueAndAirQuality: Pair<Double, AirStationModel.AirQualityIndex>?,
    sensorType: AirStationModel.AirSensorType,
    unit: String
) {
    circleProgressView.maxValue = sensorType.maxUnhealthy.toFloat()
    circleProgressView.unit = unit
    if (valueAndAirQuality != null) {
        circleProgressView.apply {
            setValue(valueAndAirQuality.first.toFloat())
            setTextColor(context.getColorCompact(valueAndAirQuality.second.color))
        }
    }
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
                textView.context.getColorCompact(R.color.base_color),
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
fun setOldDate(textView: TextView, isDateOld: Boolean?) {
    when {
        isDateOld == null -> textView.apply {
            setColor(R.color.colorAccent)
            text = textView.context.getString(R.string.chart_empty)
        }
        isDateOld -> textView.apply {
            setColor(R.color.colorAccent)
            text = textView.context.getString(R.string.station_date_old, text)
        }
        else -> textView.apply {
            setColor(R.color.base_color)
        }
    }
}

@BindingAdapter("airQuality")
fun setAirQualityIndex(textView: TextView, airQualityIndex: AirStationModel.AirQualityIndex?) {
    airQualityIndex?.let {
        textView.apply {
            text = SpannableStringBuilder()
                .append(context.getString(R.string.air_quality))
                .append(" ")
                .color(context.getColorCompact(airQualityIndex.color)) {
                    bold { append(context.getString(airQualityIndex.description)) }
                }
        }
    }
}
