package pl.sienczykm.templbn.utils

abstract class Station(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double
) {
    lateinit var url: String

    abstract fun getStationUrl(): String

}