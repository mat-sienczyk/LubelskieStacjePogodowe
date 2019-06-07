package pl.sienczykm.templbn.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import pl.sienczykm.templbn.utils.EmptyStringTypeAdapter

class TempStationOne {

    @SerializedName("data")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var data: String? = null

    @SerializedName("windSpeed")
    @Expose
    var windSpeedData: ChartModel? = null

    @SerializedName("temperature")
    @Expose
    var temperatureData: ChartModel? = null

    @SerializedName("windChill")
    @Expose
    var windChillData: ChartModel? = null

    @SerializedName("humidity")
    @Expose
    var humidityData: ChartModel? = null

    @SerializedName("pressure")
    @Expose
    var pressureData: ChartModel? = null

    @SerializedName("rainCum")
    @Expose
    var rainData: ChartModel? = null

    @SerializedName("windDirInt")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var windDir: Double? = null

    @SerializedName("windSpeedInt")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var windSpeed: Double? = null

    @SerializedName("temperatureInt")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var temperature: Double? = null

    @SerializedName("windChillInt")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var temperatureWindChill: Double? = null

    @SerializedName("humidityInt")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var humidity: Double? = null

    @SerializedName("pressureInt")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var pressure: Double? = null

    @SerializedName("rainCumInt")
    @Expose
    @JsonAdapter(EmptyStringTypeAdapter::class)
    var rainToday: Double? = null



}