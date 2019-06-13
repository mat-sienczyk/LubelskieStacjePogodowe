package pl.sienczykm.templbn.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class SmogStationDb(
    @PrimaryKey
    val stationId: Int? = null,
    val sensors: List<SmogSensorDb>? = null
) {
    val date: Date
        get() = Date(sensors?.get(0)?.data?.get(0)?.timestamp!!)
}