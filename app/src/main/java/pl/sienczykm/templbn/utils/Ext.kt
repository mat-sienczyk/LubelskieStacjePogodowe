package pl.sienczykm.templbn.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.clearCache
import pl.sienczykm.templbn.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.*

fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, this.getText(message), duration).show()
}

fun Context.isNetworkAvailable() =
    (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnected == true

fun CoordinatorLayout.snackbarShow(@StringRes message: Int, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}

fun CoordinatorLayout.snackbarShow(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.addClickEffect() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}

/**
 * Calculate distance between two points.
 *
 * @return distance in in km.
 */
fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6372.8 // in kilometers

    val λ1 = Math.toRadians(lat1)
    val λ2 = Math.toRadians(lat2)
    val Δλ = Math.toRadians(lat2 - lat1)
    val Δφ = Math.toRadians(lon2 - lon1)
    return 2 * R * asin(sqrt(sin(Δλ / 2).pow(2.0) + sin(Δφ / 2).pow(2.0) * cos(λ1) * cos(λ2)))
}

/**
 * Calculate absolute pressure from relative (sea level).
 *
 * https://keisan.casio.com/exec/system/1224575267
 * To get absolute pressure from relative, use Pa = Pr / factor
 *
 * @return pressure in hPa.
 */
fun calcAbsolutePressure(pressure: Double?, temp: Double?, altitude: Double) =
    calcPressure(pressure, temp, altitude, Double::div)

/**
 * Calculate relative (sea level) pressure from absolute.
 *
 * https://keisan.casio.com/exec/system/1224575267
 * To get relative pressure from absolute, use Pr = Pa * factor
 *
 * @return pressure in hPa.
 */
fun calcRelativePressure(pressure: Double?, temp: Double?, altitude: Double) =
    calcPressure(pressure, temp, altitude, Double::times)

private fun calcPressure(
    pressure: Double?,
    temp: Double?,
    altitude: Double,
    operation: (Double, Double) -> Double,
): Double? {
    return if (pressure != null && temp != null) {
        val temperature = temp + 273.15f
        val tempGradient = 0.0065f

        val factor = (1 - (tempGradient * altitude) / (temperature + tempGradient * altitude)).pow(
            -5.257
        )

        operation(pressure, factor)
    } else {
        null
    }
}

fun Context.isLocationPermissionGranted() =
    isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

fun Context.isWriteExternalStoragePermissionGranted() =
    isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)

fun Context.isPermissionGranted(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Double?.round(places: Int = 0) = when {
    this == null -> null
    places < 0 -> this
    else -> {
        var bd = BigDecimal(this)
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        bd.toDouble()
    }
}

fun Double?.roundAndGetString(places: Int = 0) = when {
    this == null -> null
    places < 0 -> this.toString()
    places == 0 -> this.round(places)?.toInt()?.toString()
    else -> {
        this.round(places)?.toString()?.replace(".", ",")
    }
}

fun Context.handleNightMode() {
    when (getDarkModeSetting()) {
        1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        3 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}

fun Context.isNightModeActive() =
    when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> false
    }

fun Context.getDrawableWithColor(
    @DrawableRes drawableResId: Int,
    @ColorInt color: Int = Color.WHITE,
): Drawable? =
    ContextCompat.getDrawable(this, drawableResId)?.apply {
        mutate()
        setTint(color)
    }

fun Context.isGooglePlayServicesAvailable() =
    when (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)) {
        ConnectionResult.SUCCESS -> true
        else -> false
    }

fun Context.isGooglePlayServicesAvailableAndEnabled() =
    isGooglePlayServicesAvailable() && useGooglePlayServices()

fun SwipeRefreshLayout.setColors() {
    setProgressBackgroundColorSchemeResource(R.color.refresh_bg)
    setColorSchemeResources(R.color.refresh_yellow, R.color.refresh_red, R.color.refresh_green)
}

fun nowInPoland(): Calendar =
    Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"), Locale("pl", "PL"))

fun dateFormat(pattern: String, timeZoneString: String = "Europe/Warsaw") =
    SimpleDateFormat(pattern, Locale("pl", "PL")).apply {
        timeZone = TimeZone.getTimeZone(timeZoneString)
    }

fun Date.isOlderThan(minutes: Long) =
    nowInPoland().timeInMillis - this.time > TimeUnit.MINUTES.toMillis(minutes)

fun TextView.setColor(@ColorRes colorResId: Int) {
    setTextColor(context.getColorCompact(colorResId))
}

fun ImageView.invertColors() {
    colorFilter =
        ColorMatrixColorFilter(
            floatArrayOf(
                -1.0f, 0f, 0f, 0f, 255f, // red
                0f, -1.0f, 0f, 0f, 255f, // green
                0f, 0f, -1.0f, 0f, 255f, // blue
                0f, 0f, 0f, 1.0f, 0f  // alpha
            )
        )
}

fun Context.getColorCompact(@ColorRes colorResId: Int) =
    ContextCompat.getColor(this, colorResId)

fun Context.getColorHex(@ColorRes colorResId: Int) =
    String.format("#%06X", 0xFFFFFF and getColorCompact(colorResId))

@WorkerThread
fun Context.getLastKnownLocation(): Location? {
    return if (isGooglePlayServicesAvailableAndEnabled()) {
        try {
            Tasks.await(LocationServices.getFusedLocationProviderClient(this).lastLocation)
        } catch (e: Exception) {
            null
        }
    } else {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val gpsLocation =
            locationManager.getLastKnownLocationForProvider(LocationManager.GPS_PROVIDER)
        val networkLocation =
            locationManager.getLastKnownLocationForProvider(LocationManager.NETWORK_PROVIDER)

        return when {
            gpsLocation == null -> networkLocation
            networkLocation == null -> gpsLocation
            else -> null
        }
    }
}

fun LocationManager.getLastKnownLocationForProvider(provider: String): Location? {
    return if (isProviderEnabled(provider)) {
        try {
            return getLastKnownLocation(provider)
        } catch (e: SecurityException) {
            null
        }
    } else null
}

fun Context.openUrl(url: String): Boolean {
    return if (Patterns.WEB_URL.matcher(url).matches()) {
        val webPage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webPage)
        if (intent.resolveActivity(packageManager) != null) {
            CustomTabsIntent.Builder().build().launchUrl(this, webPage)
            true
        } else {
            false
        }
    } else false
}

fun drawTextOnBitmap(
    context: Context,
    width: Int,
    height: Int,
    textXScale: Float,
    textSize: Int,
    scalable: Boolean,
    text: String,
): Bitmap {
    val scale = if (scalable) context.resources.displayMetrics.density else 1.0f
    val bitmap = Bitmap.createBitmap(
        (width * scale).toInt(),
        (height * scale).toInt(),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textScaleX = textXScale
        this.textSize = textSize * scale
    }
    val bounds = Rect()
    paint.getTextBounds(text, 0, text.length, bounds)
    val x = (bitmap.width - bounds.width()) / 2
    val y = (bitmap.height + bounds.height()) / 2
    canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
    return bitmap
}

fun ImageView.downloadImage(url: String) {
    Picasso.get().apply {
        load(url)
            .into(this@downloadImage, object : Callback {
                override fun onSuccess() {
                    if (this@downloadImage.context.isNightModeActive()) this@downloadImage.invertColors()
                }

                override fun onError(e: Exception?) {
                    this@apply.clearCache(url)
                }
            })
    }
}

fun CharSequence.plusDrawable(context: Context, @DrawableRes drawableResId: Int): CharSequence {

    if (drawableResId == android.R.id.empty) return this

    return SpannableStringBuilder(this).apply {
        append(" ")
        setSpan(
            ImageSpan(context, drawableResId, DynamicDrawableSpan.ALIGN_CENTER),
            this.length - 1,
            this.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
    }
}
