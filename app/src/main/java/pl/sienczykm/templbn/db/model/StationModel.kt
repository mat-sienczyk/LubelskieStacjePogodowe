package pl.sienczykm.templbn.db.model

import androidx.room.PrimaryKey
import java.util.*

abstract class StationModel(
    @PrimaryKey
    open val stationId: Int,
    open val name: String,
    open val latitude: Double,
    open val longitude: Double
) {
    lateinit var url: String
    var date: Date? = null

    abstract fun getStationUrl(): String

}