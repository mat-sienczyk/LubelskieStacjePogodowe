package pl.sienczykm.templbn.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TempStationOne {

    @SerializedName("data")
    @Expose
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
    var windDir: Int? = null

    @SerializedName("windSpeedInt")
    @Expose
    var windSpeed: Double? = null

    @SerializedName("temperatureInt")
    @Expose
    var temperature: Double? = null

    @SerializedName("windChillInt")
    @Expose
    var temperatureWindChill: Double? = null

    @SerializedName("humidityInt")
    @Expose
    var humidity: Double? = null

    @SerializedName("pressureInt")
    @Expose
    var pressure: Double? = null

    @SerializedName("rainCumInt")
    @Expose
    var rainToday: Double? = null



}