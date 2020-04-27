package pl.sienczykm.templbn.webservice.model.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import pl.sienczykm.templbn.utils.EmptyStringTypeAdapter
import pl.sienczykm.templbn.webservice.model.weather.ChartModel

class UmcsWeatherStationTwo {

    @SerializedName("data")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var data: String? = null

    @SerializedName("windDir")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var windDir: Double? = null

    @SerializedName("windSpeed")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var windSpeed: Double? = null

    @SerializedName("rainT")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var rainToday: Double? = null

    @SerializedName("T5")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var temperatureGround: Double? = null

    @SerializedName("temperature")
    @Expose
    var temperatureData: ChartModel? = null

    @SerializedName("humidity")
    @Expose
    var humidityData: ChartModel? = null

    @SerializedName("temperatureInt")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var temperature: Double? = null

    @SerializedName("humidityInt")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var humidity: Double? = null

}