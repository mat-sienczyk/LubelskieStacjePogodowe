package pl.sienczykm.templbn.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SmogSensor {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("stationId")
    @Expose
    var stationId: Int? = null

    @SerializedName("param")
    @Expose
    var param: SmogSensorParam? = null

}