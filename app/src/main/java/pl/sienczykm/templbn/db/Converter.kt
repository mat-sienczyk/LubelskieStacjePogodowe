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
    fun timestampToDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun stringToType(value: String): WeatherStationModel.Type =
        when (value) {
            WeatherStationModel.Type.UMCS_ONE.name -> WeatherStationModel.Type.UMCS_ONE
            WeatherStationModel.Type.UMCS_TWO.name -> WeatherStationModel.Type.UMCS_TWO
            WeatherStationModel.Type.IMGW_SIMPLE.name -> WeatherStationModel.Type.IMGW_SIMPLE
            WeatherStationModel.Type.IMGW_POGODYNKA.name -> WeatherStationModel.Type.IMGW_POGODYNKA
            WeatherStationModel.Type.SWIDNIK.name -> WeatherStationModel.Type.SWIDNIK
            else -> throw Exception("Add TypeConverter for new WeatherStationModel.Type!")
        }

    @TypeConverter
    fun typeToString(type: WeatherStationModel.Type): String {
        return type.name
    }

    @TypeConverter
    fun chartArrayToString(data: List<ChartDataModel>?): String? {
        return when (data) {
            null -> null
            else -> Gson().toJson(data, dataArrayType)
        }

    }

    @TypeConverter
    fun stringToChartArray(json: String?): List<ChartDataModel>? {
        return when (json) {
            null -> null
            else -> Gson().fromJson(json, dataArrayType)
        }
    }

    @TypeConverter
    fun sensorArrayToString(data: List<AirSensorModel>?): String? {
        return when (data) {
            null -> null
            else -> Gson().toJson(data, sensorArrayType)
        }

    }

    @TypeConverter
    fun stringToSensorArray(json: String?): List<AirSensorModel>? {
        return when (json) {
            null -> null
            else -> Gson().fromJson(json, sensorArrayType)
        }
    }

}