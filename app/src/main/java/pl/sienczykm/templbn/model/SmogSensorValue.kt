package pl.sienczykm.templbn.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SmogSensorValue {

    @SerializedName("date")
    @Expose
    val date: String? = null

    @SerializedName("value")
    @Expose
    val value: Double? = null

}
