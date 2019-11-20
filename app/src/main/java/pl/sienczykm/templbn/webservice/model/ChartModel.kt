package pl.sienczykm.templbn.webservice.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChartModel {

    @SerializedName("data")
    @Expose
    var data: List<List<Double>>? = null

}