package pl.sienczykm.templbn.webservice

import androidx.annotation.WorkerThread
import com.google.gson.GsonBuilder
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import pl.sienczykm.templbn.BuildConfig
import pl.sienczykm.templbn.utils.Config
import pl.sienczykm.templbn.webservice.model.air.AirIndexQuality
import pl.sienczykm.templbn.webservice.model.air.AirSensor
import pl.sienczykm.templbn.webservice.model.air.AirSensorData
import pl.sienczykm.templbn.webservice.model.weather.PogodynkaWeatherStation
import pl.sienczykm.templbn.webservice.model.weather.SwidnikWeatherStation
import pl.sienczykm.templbn.webservice.model.weather.UmcsWeatherStationOne
import pl.sienczykm.templbn.webservice.model.weather.UmcsWeatherStationTwo
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LspController {

    @WorkerThread
    fun getUmcsWeatherStationOne(stationId: Int): Response<UmcsWeatherStationOne> =
        RetrofitService.get()
            .getUmcsStationOne(Config.UMCS_BASE_WEATHER_URL + "data.php", stationId)
            .execute()

    @WorkerThread
    fun getUmcsWeatherStationTwo(stationId: Int): Response<UmcsWeatherStationTwo> =
        RetrofitService.get()
            .getUmcsStationTwo(Config.UMCS_BASE_WEATHER_URL + "data2.php", stationId)
            .execute()

    @WorkerThread
    fun getImgwWeatherStation(stationId: Int): Response<ResponseBody> =
        RetrofitService.get()
            .getImgwStation(Config.IMGW_BASE_WEATHER_URL + stationId)
            .execute()

    @WorkerThread
    fun getPogodynkaWeatherStation(stationId: Int): Response<PogodynkaWeatherStation> =
        RetrofitService.get()
            .getPogodynkaStation(Config.POGODYNKA_BASE_WEATHER_URL + "api/station/meteo", stationId)
            .execute()

    @WorkerThread
    fun getAirSensors(stationId: Int): Response<List<AirSensor>> =
        RetrofitService.get()
            .getSensorsForStation(Config.GIOS_BASE_AIR_URL + "station/sensors/$stationId")
            .execute()

    @WorkerThread
    fun getAirSensorData(sensorId: Int): Response<AirSensorData> =
        RetrofitService.get()
            .getDataForSensor(Config.GIOS_BASE_AIR_URL + "data/getData/$sensorId")
            .execute()

    @WorkerThread
    fun getAirQualityIndex(stationId: Int): Response<AirIndexQuality> =
        RetrofitService.get()
            .getAirQualityIndex(Config.GIOS_BASE_AIR_URL + "aqindex/getIndex/$stationId")
            .execute()

    @WorkerThread
    fun getSwidnikWeatherStation(): SwidnikWeatherStation =
        SwidnikWeatherStation(Jsoup.connect(Config.SWIDNIK_BASE_WEATHER_URL + "index_1.php").get())

    private object RetrofitService {
        private val service = Retrofit.Builder()
            .baseUrl("http://example.com/") // baseUrl is required, even if we use @Url in calls
            .client(HttpClient.get())
            .addConverterFactory(ConverterFactory.get())
            .build()
            .create(Service::class.java)

        fun get(): Service = service
    }

    private object HttpClient {
        private val client = OkHttpClient().newBuilder()
            .connectionSpecs(
                listOf(
                    ConnectionSpec.CLEARTEXT,
                    ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                        .allEnabledTlsVersions()
                        .allEnabledCipherSuites()
                        .build()
                )
            )
            .addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BASIC
            }).build()

        fun get(): OkHttpClient = client
    }

    private object ConverterFactory {
        private val converterFactory = GsonConverterFactory.create(
            GsonBuilder().apply {
                setLenient()
            }.create()
        )

        fun get(): GsonConverterFactory = converterFactory
    }
}