package pl.sienczykm.templbn.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.main.MainActivity
import pl.sienczykm.templbn.utils.widgetStationId
import java.util.concurrent.TimeUnit

/**
 * Implementation of App Widget functionality.
 */
class SimpleWidget : AppWidgetProvider() {

    companion object {
        const val OLD_KEY = "setOld"
    }

    private var setOld = true
    private val noExists = "-"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val widgetUpdateIntent = Intent(context, SimpleWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            putExtra(OLD_KEY, true)
        }
        val widgetUpdatePendingIntent =
            PendingIntent.getBroadcast(context, 0, widgetUpdateIntent, 0)

        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(30),
            widgetUpdatePendingIntent
        )

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        setOld = intent.getBooleanExtra(OLD_KEY, true)
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        CoroutineScope(Dispatchers.IO).launch {

            // Room suspend functions can be used on Main dispatchers: https://medium.com/androiddevelopers/room-coroutines-422b786dc4c5, https://medium.com/androiddevelopers/coroutines-on-android-part-i-getting-the-background-3e0e54d20bb
            val weatherStation = AppDb.getDatabase(context).weatherStationDao()
                .getStationByIdSuspend(context.widgetStationId())

            // views can be updated from non-UI thread since it's RemoteViews
            val views = RemoteViews(context.packageName, R.layout.simple_widget)

            views.setOnClickPendingIntent(
                R.id.widget,
                PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
            )

            views.setTextViewText(
                R.id.widget_temp,
                (weatherStation?.getParsedTemperature(1) ?: noExists)
                        + context.getString(R.string.celsius_degree)
            )
            views.setTextViewText(
                R.id.widget_humidity,
                (weatherStation?.getParsedHumidity(1)
                    ?: noExists) + context.getString(R.string.percent)
            )
            views.setTextViewText(
                R.id.widget_wind,
                (weatherStation?.getParsedWind(1)
                    ?: noExists) + context.getString(R.string.km_per_hour)
            )

            views.setTextViewText(
                R.id.widget_pressure,
                (weatherStation?.getParsedPressure(1) ?: noExists)
                        + context.getString(R.string.hectopascal)
            )

            val windDir =
                WeatherStationModel.windIntToDir(weatherStation?.windDir, true)
            if (windDir != android.R.id.empty) {
                views.setImageViewResource(R.id.widget_wind_dir, windDir)
                views.setViewVisibility(R.id.widget_wind_dir, View.VISIBLE)
            } else {
                views.setViewVisibility(R.id.widget_wind_dir, View.GONE)
            }

            if (setOld) {
                views.setInt(R.id.widget, "setBackgroundResource", R.drawable.widget_old)
            } else {
                views.setInt(R.id.widget, "setBackgroundResource", R.drawable.widget_fresh)
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)


            cancel()
        }
    }
}