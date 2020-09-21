package pl.sienczykm.templbn.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.sienczykm.templbn.db.model.AirSensorModel
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import java.util.*


class Converter {

    private val dataArrayType = object : TypeToken<List<ChartDataModel>>() {}.type
    private val sensorArrayType = object : TypeToken<List<AirSensorModel>>() {}.type

    @TypeConverter
    fun timestampToDate(value: Long?) = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?) = date?.time

    @TypeConverter
    fun stringToWeatherType(value: String) = WeatherStationModel.Type.valueOf(value)

    @TypeConverter
    fun weatherTypeToString(type: WeatherStationModel.Type) = type.name

    @TypeConverter
    fun chartArrayToString(data: List<ChartDataModel>?) =
        data?.let { Gson().toJson(data, dataArrayType) }

    @TypeConverter
    fun stringToChartArray(json: String?): List<ChartDataModel>? =
        json?.let { Gson().fromJson(json, dataArrayType) }

    @TypeConverter
    fun sensorArrayToString(data: List<AirSensorModel>?) =
        data?.let { Gson().toJson(data, sensorArrayType) }

    @TypeConverter
    fun stringToSensorArray(json: String?): List<AirSensorModel>? =
        json?.let { Gson().fromJson(json, sensorArrayType) }

}