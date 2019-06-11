package pl.sienczykm.templbn.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.sienczykm.templbn.db.model.DataModelDb
import pl.sienczykm.templbn.db.model.SmogSensorDb
import java.util.*


class Converter {

    val dataArrayType = object : TypeToken<List<DataModelDb>>() {}.type
    val sensorArrayType = object : TypeToken<List<SmogSensorDb>>() {}.type

    @TypeConverter
    fun timestampToDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun chartArrayToString(data: List<DataModelDb>?): String? {
        return when (data) {
            null -> null
            else -> Gson().toJson(data, dataArrayType)
        }

    }

    @TypeConverter
    fun stringToChartArray(json: String?): List<DataModelDb>? {
        return when (json) {
            null -> null
            else -> Gson().fromJson(json, dataArrayType)
        }
    }

    @TypeConverter
    fun sensorArrayToString(data: List<SmogSensorDb>?): String? {
        return when (data) {
            null -> null
            else -> Gson().toJson(data, sensorArrayType)
        }

    }

    @TypeConverter
    fun stringToSensorArray(json: String?): List<SmogSensorDb>? {
        return when (json) {
            null -> null
            else -> Gson().fromJson(json, sensorArrayType)
        }
    }

}