package pl.sienczykm.templbn.ui.common

import androidx.annotation.StringRes

interface BaseNavigator {
    fun showInfo(@StringRes message: Int)
    fun handleError(message: String?)
    fun noConnection()
}