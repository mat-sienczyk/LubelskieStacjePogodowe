package pl.sienczykm.templbn.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SmogSensorData {

    @SerializedName("key")
    @Expose
    val key: String? = null

    @SerializedName("values")
    @Expose
    val values: List<SmogSensorValue>? = null

}