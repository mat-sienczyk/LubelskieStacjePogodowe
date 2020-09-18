package pl.sienczykm.templbn.webservice.model.air

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class LookO2Station {
    @SerializedName("Device")
    @Expose
    val device: String? = null

    @SerializedName("PM1")
    @Expose
    val pM1: String? = null

    @SerializedName("PM25")
    @Expose
    val pM25: String? = null

    @SerializedName("PM10")
    @Expose
    val pM10: String? = null

    @SerializedName("Epoch")
    @Expose
    val epoch: String? = null

    @SerializedName("Lat")
    @Expose
    val lat: String? = null

    @SerializedName("Lon")
    @Expose
    val lon: String? = null

    @SerializedName("IJP")
    @Expose
    val iJP: String? = null

    @SerializedName("IJPStringEN")
    @Expose
    val iJPStringEN: String? = null

    @SerializedName("IJPString")
    @Expose
    val iJPString: String? = null

    @SerializedName("IJPDescription")
    @Expose
    val iJPDescription: String? = null

    @SerializedName("IJPDescriptionEN")
    @Expose
    val iJPDescriptionEN: String? = null

    @SerializedName("Color")
    @Expose
    val color: String? = null

    @SerializedName("Temperature")
    @Expose
    val temperature: String? = null

    @SerializedName("Humidity")
    @Expose
    val humidity: String? = null

    @SerializedName("AveragePM1")
    @Expose
    val averagePM1: String? = null

    @SerializedName("AveragePM25")
    @Expose
    val averagePM25: String? = null

    @SerializedName("AveragePM10")
    @Expose
    val averagePM10: String? = null

    @SerializedName("Name")
    @Expose
    val name: String? = null

    @SerializedName("Indoor")
    @Expose
    val indoor: String? = null

    @SerializedName("PreviousIJP")
    @Expose
    val previousIJP: String? = null

    @SerializedName("HCHO")
    @Expose
    val hCHO: String? = null

    @SerializedName("AverageHCHO")
    @Expose
    val averageHCHO: String? = null
}