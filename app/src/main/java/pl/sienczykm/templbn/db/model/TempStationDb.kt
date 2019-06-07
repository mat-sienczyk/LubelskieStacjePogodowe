package pl.sienczykm.templbn.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class TempStationDb(
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
    val rainToday: Double? = null
//    @Embedded val temperatureChart: List<ChartModelDb>? = null,
//    @Embedded val humidityChart: List<ChartModelDb>? = null,
//    @Embedded val windSpeedChart: List<ChartModelDb>? = null,
//    @Embedded val temperatureWindChart: List<ChartModelDb>? = null,
//    @Embedded val pressureChart: List<ChartModelDb>? = null,
//    @Embedded val rainTodayChart: List<ChartModelDb>? = null
)