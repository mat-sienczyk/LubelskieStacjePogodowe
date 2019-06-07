package pl.sienczykm.templbn.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChartModel {

    @SerializedName("data")
    @Expose
    var data: List<List<Double>>? = null

}