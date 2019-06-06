package pl.sienczykm.templbn.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TempStation {

    @SerializedName("data")
    @Expose
    var data: String? = null

    @SerializedName("windSpeed")
    @Expose
    var windSpeedData: WindSpeed? = null

    @SerializedName("temperature")
    @Expose
    var temperatureData: Temperature? = null

    @SerializedName("windChill")
    @Expose
    var windChillData: WindChill? = null

    @SerializedName("humidity")
    @Expose
    var humidityData: Humidity? = null

    @SerializedName("pressure")
    @Expose
    var pressureData: Pressure? = null

    @SerializedName("rainCum")
    @Expose
    var rainData: RainCum? = null

    @SerializedName(value = "windDirInt", alternate = ["windDir"])
    @Expose
    var windDir: Int? = null

    @SerializedName(value = "windSpeedInt")//, alternate = ["windSpeed"])
    @Expose
    var windSpeed: Double? = null

    @SerializedName("temperatureInt")
    @Expose
    var temperature: Double? = null

    @SerializedName("windChillInt")
    @Expose
    var temperatureWindChill: Double? = null

    @SerializedName("T5")
    @Expose
    var temperatureGround: Double? = null

    @SerializedName("humidityInt")
    @Expose
    var humidity: Double? = null

    @SerializedName("pressureInt")
    @Expose
    var pressure: Double? = null

    @SerializedName(value = "rainCumInt", alternate = ["rainT"])
    @Expose
    var rainToday: Int? = null

    override fun toString(): String {
        return "TempStation(data=$data, windDir=$windDir, windSpeed=$windSpeed, temperature=$temperature, temperatureWindChill=$temperatureWindChill, temperatureGround=$temperatureGround, humidity=$humidity, pressure=$pressure, rainToday=$rainToday)"
    }


}