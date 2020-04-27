package pl.sienczykm.templbn.webservice.model.air

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AirSensor {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("stationId")
    @Expose
    var stationId: Int? = null

    @SerializedName("param")
    @Expose
    var param: AirSensorParam? = null

}