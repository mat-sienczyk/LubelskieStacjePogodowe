package pl.sienczykm.templbn.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SmogStationDb(
    @PrimaryKey
    val stationId: Int? = null,
    val sensors: List<SmogSensorDb>? = null
)