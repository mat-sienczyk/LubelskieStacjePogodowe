package pl.sienczykm.templbn.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class WeatherStationDb(
    @PrimaryKey
    val stationId: Int? = null,
    val date: Date? = null,
    val temperature: Double? = null,
    val temperatureWind: Double? = null,
    val temperatureGround: Double? = null,
    val windSpeed: Double? = null,
    val windDir: Double? = null,
    val humidity: Double? = null,
    val pressure: Double? = null,
    val rainToday: Double? = null,
    val temperatureData: List<DataModelDb>? = null,
    val humidityData: List<DataModelDb>? = null,
    val windSpeedData: List<DataModelDb>? = null,
    val temperatureWindData: List<DataModelDb>? = null,
    val pressureData: List<DataModelDb>? = null,
    val rainTodayData: List<DataModelDb>? = null
)