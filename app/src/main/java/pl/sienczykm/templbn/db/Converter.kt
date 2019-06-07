package pl.sienczykm.templbn.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.sienczykm.templbn.db.model.ChartModelDb
import java.util.*


class Converter {

    val chartArrayType = object : TypeToken<List<ChartModelDb>>() {}.type

    @TypeConverter
    fun timestampToDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun chartArrayToString(chartData: List<ChartModelDb>?): String? {
        return when (chartData) {
            null -> null
            else -> Gson().toJson(chartData, chartArrayType)
        }

    }

    @TypeConverter
    fun stringToChartArray(json: String?): List<ChartModelDb>? {
        return when (json) {
            null -> null
            else -> Gson().fromJson(json, chartArrayType)
        }
    }

}