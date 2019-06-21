package pl.sienczykm.templbn.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, this.getText(message), duration).show()
}

fun snackbarShow(view: CoordinatorLayout, @StringRes message: Int, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}

fun snackbarShow(view: CoordinatorLayout, message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
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

fun Double?.round(places: Int): Double {
    if (places < 0 || this == null) throw IllegalArgumentException()
    var bd = BigDecimal(this)
    bd = bd.setScale(places, RoundingMode.HALF_UP)
    return bd.toDouble()
}