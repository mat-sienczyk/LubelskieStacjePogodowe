package pl.sienczykm.templbn.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, this.getText(message), duration).show()
}

fun snackbar(view: View, @StringRes message: Int, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}

fun snackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}