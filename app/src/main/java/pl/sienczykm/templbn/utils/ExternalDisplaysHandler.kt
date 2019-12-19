package pl.sienczykm.templbn.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.main.MainActivity
import pl.sienczykm.templbn.widget.OldWeatherWidget

// TODO refactor this
object ExternalDisplaysHandler {

    fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }

    private fun cancelNotification(
        context: Context,
        notificationId: Int
    ) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    private fun sendNotification(
        context: Context,
        notificationId: Int,
        notification: Notification
    ) {
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun updateExternalDisplays(context: Context) {
        setWeatherNotification(context)
        updateOldWeatherWidget(context)
    }

    fun checkAirQuality(context: Context) {

        val notificationId = 70

        if (context.showAirQualityNotification()) {
            CoroutineScope(Dispatchers.IO).launch {
                getNearestAirStationId(context)?.let {

                    val airStation = AppDb.getDatabase(context).airStationDao().getStationById(it)

                    if (airStation?.airQualityIndex != null && airStation.airQualityIndex!! >= context.getAirQualityLevel()) {

                        val airChannelId = "air_quality_notification"

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            val channel = NotificationChannel(
                                airChannelId,
                                context.getString(R.string.air_index_notification_channel_name),
                                NotificationManager.IMPORTANCE_DEFAULT
                            ).apply {
                                description =
                                    context.getString(R.string.air_index_notification_channel_description)
                            }

                            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                                .createNotificationChannel(channel)

                        }

                        val builder = NotificationCompat.Builder(context, airChannelId)
                            .setContentTitle(airStation.getFullStationName())
                            .setContentText(context.getString(R.string.air_index_notification_content))
                            .setContentIntent(MainActivity.openAirPendingIntent(context))
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_app_icon)

                        sendNotification(context, notificationId, builder.build())
                    } else {
                        cancelNotification(context, notificationId)
                    }
                }

                cancel()
            }
        }
    }

    fun updateOldWeatherWidget(context: Context) {
        val widgetUpdateIntent = Intent(context, OldWeatherWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_IDS,
                AppWidgetManager.getInstance(context).getAppWidgetIds(
                    ComponentName(
                        context,
                        OldWeatherWidget::class.java
                    )
                )
            )
            putExtra(OldWeatherWidget.OLD_KEY, false)
        }
        context.sendBroadcast(widgetUpdateIntent)
    }

    fun setWeatherNotification(context: Context) {

        val notificationId = 69

        if (context.showWeatherNotification()) {
            CoroutineScope(Dispatchers.IO).launch {
                val weatherStation = AppDb.getDatabase(context).weatherStationDao()
                    .getStationById(getProperWeatherStationId(context))

                val temperatureString = weatherStation?.getParsedTemperature()?.let {
                    "$it${context.getString(R.string.celsius_degree)}"
                }

                val hourString = "${weatherStation?.date?.let { dateFormat("HH:mm").format(it) }}"

                val weatherChannelId = "weather_notification"

                val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val channel = NotificationChannel(
                        weatherChannelId,
                        context.getString(R.string.weather_notification_channel_name),
                        NotificationManager.IMPORTANCE_LOW
                    ).apply {
                        description =
                            context.getString(R.string.weather_notification_channel_description)
                    }

                    (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                        .createNotificationChannel(channel)

                    Notification.Builder(context, weatherChannelId)
                } else {
                    // NotificationCompact do not allow pass Icon via setSmallIcon()
                    Notification.Builder(context)
                }
                    .setContentTitle("$temperatureString - $hourString")
                    .setContentText(weatherStation?.getFullStationName())
                    .setContentIntent(MainActivity.openWeatherPendingIntent(context))
                    .setOngoing(true)
                    .apply {
                        temperatureString?.let { text ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.createIconForNotification()) {
                                setSmallIcon(getNotificationIcon(text, context))
                            } else {
                                setSmallIcon(R.drawable.ic_temperature)
                            }
                        } ?: setSmallIcon(R.drawable.ic_temperature)
                    }

                sendNotification(context, notificationId, builder.build())

                cancel()
            }
        } else {
            cancelNotification(context, notificationId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getNotificationIcon(text: String, context: Context): Icon = Icon.createWithBitmap(
        drawTextOnBitmap(
            context = context,
            width = 28,
            height = 28,
            textXScale = 1.03f - (text.length * 0.1f),
            textSize = 22,
            scalable = true,
            text = text
        )
    )

    fun getProperWeatherStationId(context: Context): Int {
        val defaultStationId = context.getWeatherStationId()

        return if (context.useLocationToUpdateWeather()) {
            context.getLastKnownLocation()?.let { getNearestWeatherStationId(it) }
                ?: defaultStationId
        } else {
            defaultStationId
        }
    }

    private fun getNearestAirStationId(context: Context): Int? {
        val nearestStation = context.getLastKnownLocation()?.let {
            AirStationModel.getStations()
                .minWith(distanceComparator(it)) // since AirStationModel.getStations() is list of static objects, minWith will never returns null
        } ?: return null

        // distance is not null if we run distanceComparator()
        return if (nearestStation.distance!! <= 10) { // TODO: move this 10 km into options perhaps?
            nearestStation.stationId
        } else null

    }

    private fun getNearestWeatherStationId(location: Location): Int =
        WeatherStationModel.getStations().minWith(distanceComparator(location))!!.stationId // since WeatherStationModel.getStations() is list of static objects, minWith will never returns null

    private fun distanceComparator(it: Location): Comparator<BaseStationModel> {
        return Comparator { station1, station2 ->
            station1.distance =
                haversine(
                    it.latitude,
                    it.longitude,
                    station1.latitude,
                    station1.longitude
                )
            station2.distance =
                haversine(
                    it.latitude,
                    it.longitude,
                    station2.latitude,
                    station2.longitude
                )

            if (station1.distance!! > station2.distance!!) 1 else 0
        }
    }
}