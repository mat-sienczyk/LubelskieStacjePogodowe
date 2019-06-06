package pl.sienczykm.templbn.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TempStationTwo {

    @SerializedName("data")
    @Expose
    var data: String? = null

    @SerializedName("windDir")
    @Expose
    var windDir: Int? = null

    @SerializedName("windSpeed")
    @Expose
    var windSpeed: Double? = null

    @SerializedName("rainT")
    @Expose
    var rainToday: Double? = null

    @SerializedName("T5")
    @Expose
    var temperatureGround: Double? = null

    @SerializedName("temperature")
    @Expose
    var temperatureData: ChartModel? = null

    @SerializedName("humidity")
    @Expose
    var humidityData: ChartModel? = null

    @SerializedName("temperatureInt")
    @Expose
    var temperature: Double? = null

    @SerializedName("humidityInt")
    @Expose
    var humidity: Double? = null

}