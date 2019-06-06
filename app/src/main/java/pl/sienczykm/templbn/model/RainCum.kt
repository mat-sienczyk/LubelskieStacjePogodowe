package pl.sienczykm.templbn.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RainCum {

    @SerializedName("label")
    @Expose
    var label: String? = null
    @SerializedName("color")
    @Expose
    var color: String? = null
    @SerializedName("data")
    @Expose
    var data: List<List<Int>>? = null

}