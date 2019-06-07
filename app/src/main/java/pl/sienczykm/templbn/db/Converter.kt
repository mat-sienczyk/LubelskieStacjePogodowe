package pl.sienczykm.templbn.db

import androidx.room.TypeConverter
import java.util.*

class Converter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromArray(strings: List<String>?): String? {
        return when (strings) {
            null -> null
            else -> strings.joinToString()
        }

    }

    @TypeConverter
    fun toArray(concatenatedStrings: String?): List<String>? {
        return when (concatenatedStrings) {
            null -> null
            else -> listOf()
        }

//
//        return if (concatenatedStrings.isEmpty()) ArrayList(emptyList()) else ArrayList(
//            Arrays.asList(
//                *concatenatedStrings.split(
//                    ",".toRegex()
//                ).dropLastWhile { it.isEmpty() }.toTypedArray()
//            )
//        )
    }

}