package pl.sienczykm.templbn.db.model

import androidx.room.Ignore
import androidx.room.PrimaryKey
import pl.sienczykm.templbn.utils.round
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

abstract class BaseStationModel(
    @PrimaryKey
    open val stationId: Int,
    open val name: String,
    open val latitude: Double,
    open val longitude: Double
) {
    lateinit var url: String
    var favorite: Boolean = false
    var date: Date? = null
    @Ignore
    var distance: Double? = null

    abstract fun getStationUrl(): String

    // need to create own implementation of copy() function instead of Kotlin Data Class because of problem with inheritance
    abstract fun copy(): BaseStationModel

    fun getParsedDate(): String {
        val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("pl", "PL"))
        return outputFormat.format(date)
    }

    fun getParsedDistance(): String? {
        return when {
            distance == null -> null
            distance!! >= 10 -> distance.round(1)?.toString() + " km"
            distance!! < 1 -> (distance.round(3)?.times(1000))?.roundToInt().toString() + " m"
            else -> distance.round(2)?.toString() + " km"
        }
    }

    open fun dataTheSame(other: BaseStationModel?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (favorite != other.favorite) return false
        if (date != other.date) return false
        if (distance != other.distance) return false

        return true
    }
}