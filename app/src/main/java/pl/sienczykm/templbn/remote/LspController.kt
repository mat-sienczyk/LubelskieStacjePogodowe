package pl.sienczykm.templbn.remote

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.sienczykm.templbn.remote.model.AirSensor
import pl.sienczykm.templbn.remote.model.AirSensorData
import pl.sienczykm.templbn.remote.model.WeatherStationOne
import pl.sienczykm.templbn.remote.model.WeatherStationTwo
import pl.sienczykm.templbn.utils.Config
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object LspController {

    @WorkerThread
    fun getWeatherStationOne(stationId: Int): Response<WeatherStationOne> {
        return getWeatherService().getStationOne(stationId).execute()
    }

    @WorkerThread
    fun getWeatherStationTwo(stationId: Int): Response<WeatherStationTwo> {
        return getWeatherService().getStationTwo(stationId).execute()
    }

    @WorkerThread
    fun getAirSensors(stationId: Int): Response<List<AirSensor>> {
        return getAirService().getSensorsForStation(stationId).execute()
    }

    @WorkerThread
    fun getAirSensorData(sensorId: Int): Response<AirSensorData> {
        return getAirService().getDataForSensor(sensorId).execute()
    }

    private fun getWeatherService(): WeatherService {
        return getRetrofit(Config.BASE_WEATHER_URL).create(WeatherService::class.java)
    }

    private fun getAirService(): AirService {
        return getRetrofit(Config.BASE_AIR_URL).create(AirService::class.java)
    }

    private fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .build()
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }

    private fun getGson(): Gson {
        return GsonBuilder().apply {
            setLenient()
        }.create()
    }
}