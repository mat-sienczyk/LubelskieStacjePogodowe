package pl.sienczykm.templbn.webservice.model.weather

import org.jsoup.nodes.Document
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.utils.calcAbsolutePressure
import pl.sienczykm.templbn.utils.dateFormat
import java.util.*

class SwidnikWeatherStation(doc: Document?) {

    val temperatureData = arrayListOf<ChartDataModel>()
    val windDirData = arrayListOf<ChartDataModel>()
    val windSpeedData = arrayListOf<ChartDataModel>()
    val windMaxSpeedData = arrayListOf<ChartDataModel>()
    val pressureData = arrayListOf<ChartDataModel>()
    val rainData = arrayListOf<ChartDataModel>()

    init {
        doc?.select("table")?.get(10)?.select("tr")?.takeLast(24)?.forEach { row ->

            val date = parseDate(row?.select("td")?.get(0)?.text())

            val temp = row?.select("td")?.get(1)?.text()?.toDoubleOrNull()

            temperatureData.add( // temp = 12.1
                ChartDataModel(
                    date?.time,
                    temp
                )
            )
            windDirData.add( // windDir = S
                ChartDataModel(
                    date?.time,
                    parseWindStringToDir(row?.select("td")?.get(2)?.text())?.toDouble()
                )
            )
            windSpeedData.add( // windSpeed = 2 km/h
                ChartDataModel(
                    date?.time,
                    parseWindSpeed(row?.select("td")?.get(3)?.text())
                )
            )
            windMaxSpeedData.add( // windMaxSpeed = 7 km/h
                ChartDataModel(
                    date?.time,
                    parseWindSpeed(row?.select("td")?.get(4)?.text())
                )
            )
            rainData.add( // rain mm = 0.0
                ChartDataModel(
                    date?.time,
                    row?.select("td")?.get(5)?.text()?.toDoubleOrNull()
                )
            )
            pressureData.add( // pressure hPa = 1020.2, relative
                ChartDataModel(
                    date?.time,
                    calcAbsolutePressure(
                        row?.select("td")?.get(6)?.text()?.toDoubleOrNull(),
                        temp,
                        225.5
                    )
                )
            )
        }
    }

    private fun parseDate(stringDate: String?): Date? = // dnia 27/04/2020 o godz. 21:55
        stringDate?.let { dateFormat("'dnia 'dd/MM/yyyy' o godz. 'HH:mm").parse(it) }


    private fun parseWindStringToDir(windDirString: String?): Int? = when (windDirString) {
        "N" -> 22
        "NE" -> 67
        "E" -> 112
        "SE" -> 157
        "S" -> 202
        "SW" -> 247
        "W" -> 292
        "NW" -> 337
        else -> null
    }

    private fun parseWindSpeed(windSpeedString: String?): Double? = try {
        windSpeedString?.takeWhile { it.isDigit() }?.toDouble()?.times(5.0 / 18.0)
    } catch (e: Exception) {
        null
    }
}
