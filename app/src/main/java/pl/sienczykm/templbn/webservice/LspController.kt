package pl.sienczykm.templbn.webservice

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import pl.sienczykm.templbn.BuildConfig
import pl.sienczykm.templbn.utils.Config
import pl.sienczykm.templbn.webservice.model.air.AirIndexQuality
import pl.sienczykm.templbn.webservice.model.air.AirSensor
import pl.sienczykm.templbn.webservice.model.air.AirSensorData
import pl.sienczykm.templbn.webservice.model.weather.PogodynkaWeatherStation
import pl.sienczykm.templbn.webservice.model.weather.UmcsWeatherStationOne
import pl.sienczykm.templbn.webservice.model.weather.UmcsWeatherStationTwo
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object LspController {

    @WorkerThread
    fun getUmcsWeatherStationOne(stationId: Int): Response<UmcsWeatherStationOne> {
        return getUmcsWeatherService().getUmcsStationOne(stationId).execute()
    }

    @WorkerThread
    fun getUmcsWeatherStationTwo(stationId: Int): Response<UmcsWeatherStationTwo> {
        return getUmcsWeatherService().getUmcsStationTwo(stationId).execute()
    }

    @WorkerThread
    fun getImgwWeatherStation(stationId: Int): Response<ResponseBody> {
        return getImgwWeatherService().getImgwStation(stationId).execute()
    }

    @WorkerThread
    fun getPogodynkaWeatherStation(stationId: Int): Response<PogodynkaWeatherStation> {
        return getPogodynkaWeatherService().getPogodynkaStation(stationId).execute()
    }

    @WorkerThread
    fun getAirSensors(stationId: Int): Response<List<AirSensor>> {
        return getAirService().getSensorsForStation(stationId).execute()
    }

    @WorkerThread
    fun getAirSensorData(sensorId: Int): Response<AirSensorData> {
        return getAirService().getDataForSensor(sensorId).execute()
    }

    @WorkerThread
    fun getAirQualityIndex(stationId: Int): Response<AirIndexQuality> {
        return getAirService().getAirQualityIndex(stationId).execute()
    }

    private fun getUmcsWeatherService(): WeatherService {
        return getRetrofit(Config.UMCS_BASE_WEATHER_URL).create(WeatherService::class.java)
    }

    private fun getImgwWeatherService(): WeatherService {
        return getRetrofit(Config.IMGW_BASE_WEATHER_URL).create(WeatherService::class.java)
    }

    private fun getPogodynkaWeatherService(): WeatherService {
        return getRetrofit(Config.POGODYNKA_BASE_WEATHER_URL).create(WeatherService::class.java)
    }

    private fun getAirService(): AirService {
        return getRetrofit(Config.GIOS_BASE_AIR_URL).create(AirService::class.java)
    }

    private fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .build()
    }

    //TODO create one instance of this
    private fun getClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
//            .connectionSpecs(
//                listOf(
//                    ConnectionSpec.CLEARTEXT,
//                    ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
//                        .allEnabledTlsVersions()
//                        .allEnabledCipherSuites()
//                        .build()
//                )
//            )
            .addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }

    //TODO create one instance of this
    private fun getGson(): Gson {
        return GsonBuilder().apply {
            setLenient()
        }.create()
    }
}