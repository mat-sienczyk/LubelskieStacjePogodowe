package pl.sienczykm.templbn.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AirSensorParam {

    @SerializedName("paramName")
    @Expose
    var paramName: String? = null

    @SerializedName("paramFormula")
    @Expose
    var paramFormula: String? = null

    @SerializedName("paramCode")
    @Expose
    var paramCode: String? = null

    @SerializedName("idParam")
    @Expose
    var idParam: Int? = null

}