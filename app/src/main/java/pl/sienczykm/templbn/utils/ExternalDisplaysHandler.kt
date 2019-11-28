package pl.sienczykm.templbn.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.main.MainActivity
import pl.sienczykm.templbn.widget.OldWeatherWidget

object ExternalDisplaysHandler {

    fun updateExternalDisplays(context: Context) {
        setWeatherNotification(context)
        updateOldWeatherWidget(context)
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

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                context.getString(R.string.show_weather_notification_key),
                context.resources.getBoolean(R.bool.show_weather_notification_default)
            )
        ) {
            val weatherChannelId = "weather_notification"

            CoroutineScope(Dispatchers.IO).launch {
                val weatherStation = AppDb.getDatabase(context).weatherStationDao()
                    .getStationById(getProperStationId(context))

                val temperatureString = weatherStation?.getParsedTemperature()?.let {
                    "$it${context.getString(R.string.celsius_degree)}"
                }

                val hourString = "${weatherStation?.date?.let { dateFormat("HH:mm").format(it) }}"

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java),
                    0
                )

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
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).apply {
                        temperatureString?.let { text ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                setSmallIcon(createIconFromString(text, context))
                            } else {
                                setSmallIcon(R.drawable.ic_temperature)
                            }
                        } ?: setSmallIcon(R.drawable.ic_temperature)
                    }

                with(NotificationManagerCompat.from(context)) {
                    notify(notificationId, builder.build())
                }

                cancel()
            }
        } else {
            with(NotificationManagerCompat.from(context)) {
                cancel(notificationId)
            }
        }
    }

    fun getProperStationId(context: Context): Int {
        val defaultStationId = PreferenceManager.getDefaultSharedPreferences(context).getString(
            context.getString(R.string.widget_station_key),
            context.getString(R.string.widget_station_default)
        )!!.toInt() // default value provided

        val useLocationForWidget =
            PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                context.getString(R.string.widget_location_key),
                context.resources.getBoolean(R.bool.widget_location_default)
            )

        return if (useLocationForWidget) {
            context.getLastKnownLocation()?.let { getNearestStationId(it) } ?: defaultStationId
        } else {
            defaultStationId
        }
    }

    private fun getNearestStationId(location: Location): Int =
        WeatherStationModel.getStations().minWith(Comparator { station1, station2 ->
            station1.distance =
                haversine(
                    location.latitude,
                    location.longitude,
                    station1.latitude,
                    station1.longitude
                )
            station2.distance =
                haversine(
                    location.latitude,
                    location.longitude,
                    station2.latitude,
                    station2.longitude
                )

            if (station1.distance!! > station2.distance!!) 1 else 0
        })!!.stationId // since WeatherStationModel.getStations() is list of static objects, minWith will never returns null
}