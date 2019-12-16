package pl.sienczykm.templbn.webservice.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class AirIndexQuality {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("stCalcDate")
    @Expose
    var stCalcDate: String? = null
    @SerializedName("stIndexLevel")
    @Expose
    var stIndexLevel: StIndexLevel? = null
    @SerializedName("stSourceDataDate")
    @Expose
    var stSourceDataDate: String? = null
    @SerializedName("so2CalcDate")
    @Expose
    var so2CalcDate: String? = null
    @SerializedName("so2IndexLevel")
    @Expose
    var so2IndexLevel: So2IndexLevel? = null
    @SerializedName("so2SourceDataDate")
    @Expose
    var so2SourceDataDate: String? = null
    @SerializedName("no2CalcDate")
    @Expose
    var no2CalcDate: String? = null
    @SerializedName("no2IndexLevel")
    @Expose
    var no2IndexLevel: No2IndexLevel? = null
    @SerializedName("no2SourceDataDate")
    @Expose
    var no2SourceDataDate: String? = null
    @SerializedName("coCalcDate")
    @Expose
    var coCalcDate: String? = null
    @SerializedName("coIndexLevel")
    @Expose
    var coIndexLevel: CoIndexLevel? = null
    @SerializedName("coSourceDataDate")
    @Expose
    var coSourceDataDate: String? = null
    @SerializedName("pm10CalcDate")
    @Expose
    var pm10CalcDate: String? = null
    @SerializedName("pm10IndexLevel")
    @Expose
    var pm10IndexLevel: Pm10IndexLevel? = null
    @SerializedName("pm10SourceDataDate")
    @Expose
    var pm10SourceDataDate: String? = null
    @SerializedName("pm25CalcDate")
    @Expose
    var pm25CalcDate: String? = null
    @SerializedName("pm25IndexLevel")
    @Expose
    var pm25IndexLevel: Pm25IndexLevel? = null
    @SerializedName("pm25SourceDataDate")
    @Expose
    var pm25SourceDataDate: String? = null
    @SerializedName("o3CalcDate")
    @Expose
    var o3CalcDate: String? = null
    @SerializedName("o3IndexLevel")
    @Expose
    var o3IndexLevel: O3IndexLevel? = null
    @SerializedName("o3SourceDataDate")
    @Expose
    var o3SourceDataDate: String? = null
    @SerializedName("c6h6CalcDate")
    @Expose
    var c6h6CalcDate: String? = null
    @SerializedName("c6h6IndexLevel")
    @Expose
    var c6h6IndexLevel: C6h6IndexLevel? = null
    @SerializedName("c6h6SourceDataDate")
    @Expose
    var c6h6SourceDataDate: String? = null
    @SerializedName("stIndexStatus")
    @Expose
    var stIndexStatus: Boolean? = null
    @SerializedName("stIndexCrParam")
    @Expose
    var stIndexCrParam: String? = null
}

class C6h6IndexLevel {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("indexLevelName")
    @Expose
    var indexLevelName: String? = null
}

class CoIndexLevel {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("indexLevelName")
    @Expose
    var indexLevelName: String? = null
}

class No2IndexLevel {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("indexLevelName")
    @Expose
    var indexLevelName: String? = null
}

class Pm10IndexLevel {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("indexLevelName")
    @Expose
    var indexLevelName: String? = null
}

class Pm25IndexLevel {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("indexLevelName")
    @Expose
    var indexLevelName: String? = null
}

class So2IndexLevel {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("indexLevelName")
    @Expose
    var indexLevelName: String? = null
}

class StIndexLevel {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("indexLevelName")
    @Expose
    var indexLevelName: String? = null
}

class O3IndexLevel {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("indexLevelName")
    @Expose
    var indexLevelName: String? = null
}