package pl.sienczykm.templbn.utils

abstract class Station(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double
) {
    lateinit var url: String

    override fun toString(): String {
        return name
    }

    abstract fun getStationUrl(): String

}