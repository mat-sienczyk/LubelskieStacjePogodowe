package pl.sienczykm.templbn.webservice.model.weather

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class PogodynkaWeatherStation {

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("status")
    @Expose
    var status: Status? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("connections")
    @Expose
    var connections: Connections? = null

    @SerializedName("hourlyPrecipRecords")
    @Expose
    var hourlyPrecipRecords: List<ExtendedRecord>? = null

    @SerializedName("dailyPrecipRecords")
    @Expose
    var dailyPrecipRecords: List<ExtendedRecord>? = null

    @SerializedName("tenMinutesPrecipRecords")
    @Expose
    var tenMinutesPrecipRecords: List<ExtendedRecord>? = null

    @SerializedName("temperatureAutoRecords")
    @Expose
    var temperatureAutoRecords: List<ExtendedRecord>? = null

    @SerializedName("temperatureObsRecords")
    @Expose
    var temperatureObsRecords: List<Record>? = null

    @SerializedName("windDirectionTelRecords")
    @Expose
    var windDirectionTelRecords: List<Record>? = null

    @SerializedName("windDirectionObsRecords")
    @Expose
    var windDirectionObsRecords: List<Record>? = null

    @SerializedName("windVelocityTelRecords")
    @Expose
    var windVelocityTelRecords: List<Record>? = null

    @SerializedName("windVelocityObsRecords")
    @Expose
    var windVelocityObsRecords: List<Record>? = null

    @SerializedName("windMaxVelocityRecords")
    @Expose
    var windMaxVelocityRecords: List<Record>? = null

    @SerializedName("dataClause")
    @Expose
    var dataClause: String? = null

}

class Status {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("river")
    @Expose
    var river: String? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("precip")
    @Expose
    var precip: ExtendedRecord? = null

    @SerializedName("precipDaily")
    @Expose
    var precipDaily: ExtendedRecord? = null

    @SerializedName("precip3HoursSum")
    @Expose
    var precip3HoursSum: Long? = null

    @SerializedName("precip6HoursSum")
    @Expose
    var precip6HoursSum: Long? = null

    @SerializedName("precip12HoursSum")
    @Expose
    var precip12HoursSum: Long? = null

    @SerializedName("precip24HoursSum")
    @Expose
    var precip24HoursSum: Long? = null

    @SerializedName("maxDailyPrecipValue")
    @Expose
    var maxDailyPrecipValue: Double? = null

    @SerializedName("maxDailyPrecipDate")
    @Expose
    var maxDailyPrecipDate: String? = null

    @SerializedName("maxHourPrecipValue")
    @Expose
    var maxHourPrecipValue: Double? = null

    @SerializedName("province")
    @Expose
    var province: String? = null

    @SerializedName("note")
    @Expose
    var note: String? = null
}

class Connections {
    @SerializedName("hydroUp")
    @Expose
    var hydroUp: Items? = null

    @SerializedName("hydroDown")
    @Expose
    var hydroDown: Items? = null

    @SerializedName("meteo")
    @Expose
    var meteo: List<Items>? = null
}

open class Record {
    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("value")
    @Expose
    var value: Double? = null
}

class ExtendedRecord : Record() {
    @SerializedName("dreId")
    @Expose
    var dreId: Long? = null

    @SerializedName("operationId")
    @Expose
    var operationId: String? = null

    @SerializedName("parameterId")
    @Expose
    var parameterId: String? = null

    @SerializedName("versionId")
    @Expose
    var versionId: Long? = null

    @SerializedName("id")
    @Expose
    var id: Long? = null
}

class Items {
    @SerializedName("item1")
    @Expose
    var item1: String? = null

    @SerializedName("item2")
    @Expose
    var item2: String? = null

    @SerializedName("item3")
    @Expose
    var item3: String? = null

    @SerializedName("item4")
    @Expose
    var item4: String? = null
}