package pl.sienczykm.templbn.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import pl.sienczykm.templbn.R
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, this.getText(message), duration).show()
}

fun Context.isNetworkAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnected == true
}

fun CoordinatorLayout.snackbarShow(@StringRes message: Int, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun CoordinatorLayout.snackbarShow(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
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

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6372.8 // in kilometers

    val λ1 = Math.toRadians(lat1)
    val λ2 = Math.toRadians(lat2)
    val Δλ = Math.toRadians(lat2 - lat1)
    val Δφ = Math.toRadians(lon2 - lon1)
    return 2 * R * asin(sqrt(sin(Δλ / 2).pow(2.0) + sin(Δφ / 2).pow(2.0) * cos(λ1) * cos(λ2)))
}

fun Context?.isLocationPermissionGranted(): Boolean {
    return if(this == null){
        false
    }else{
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    }
}

fun Double?.round(places: Int = 0): Double? {
    return when {
        this == null -> null
        places < 0 -> this
        else -> {
            var bd = BigDecimal(this)
            bd = bd.setScale(places, RoundingMode.HALF_UP)
            bd.toDouble()
        }
    }
}

fun Double?.roundAndGetString(places: Int = 0): String? {
    return when {
        this == null -> null
        places < 0 -> this.toString()
        places == 0 -> this.round(places)?.toInt()?.toString()
        else -> {
            this.round(places)?.toString()?.replace(".", ",")
        }
    }
}

fun Context.isAutoUpdateEnabled(): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
        getString(R.string.enable_auto_sync_key),
        resources.getBoolean(R.bool.auto_sync_default)
    )
}

fun Context.handleNightMode() {
    when (PreferenceManager.getDefaultSharedPreferences(this).getString(
        getString(R.string.night_mode_key),
        getString(R.string.night_mode_default)
    )!!.toInt()) {
        1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        3 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}

fun Context.isNightModeActive(): Boolean {
    return when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> false
    }
}