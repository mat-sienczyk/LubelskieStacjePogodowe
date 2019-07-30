package pl.sienczykm.templbn.db.model

import androidx.room.Ignore
import androidx.room.PrimaryKey
import pl.sienczykm.templbn.utils.dateFormat
import pl.sienczykm.templbn.utils.isOlderThan
import pl.sienczykm.templbn.utils.round
import java.util.*
import kotlin.math.roundToInt

abstract class BaseStationModel(
    @PrimaryKey
    open val stationId: Int,
    open val latitude: Double,
    open val longitude: Double,
    open val city: String,
    open val location: String?
) {
    lateinit var url: String
    var favorite: Boolean = false
    var date: Date? = null
    @Ignore
    var distance: Double? = null

    abstract fun getStationUrl(): String
    abstract fun getOldDateTimeInMinutes(): Long

    // need to create own implementation of copy() function instead of Kotlin Data Class because of problem with inheritance
    abstract fun copy(): BaseStationModel

    fun getParsedDate(): String? = date?.let { dateFormat("dd.MM.yyyy HH:mm").format(it) }

    fun getParsedDistance(): String? {
        return when {
            distance == null -> null
            distance!! >= 10 -> distance.round(1)?.toString() + " km"
            distance!! < 1 -> (distance.round(3)?.times(1000))?.roundToInt().toString() + " m"
            else -> distance.round(2)?.toString() + " km"
        }
    }

    fun getName(): String {
        location?.let { return "$city - $location" } ?: return city
    }

    fun isDateObsolete(): Boolean {
        return date.isOlderThan(getOldDateTimeInMinutes())
    }

    open fun isContentTheSame(other: BaseStationModel?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (favorite != other.favorite) return false
        if (date != other.date) return false
        if (distance != other.distance) return false

        return true
    }
}