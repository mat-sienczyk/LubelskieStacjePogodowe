package pl.sienczykm.templbn.webservice.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AirSensorValue {

    @SerializedName("date")
    @Expose
    val date: String? = null

    @SerializedName("value")
    @Expose
    val value: Double? = null

}
