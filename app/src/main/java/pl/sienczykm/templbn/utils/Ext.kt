package pl.sienczykm.templbn.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, this.getText(message), duration).show()
}