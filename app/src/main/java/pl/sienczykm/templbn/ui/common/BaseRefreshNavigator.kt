package pl.sienczykm.templbn.ui.common

import androidx.annotation.StringRes

interface BaseRefreshNavigator {
    fun showInfo(@StringRes message: Int)
    fun handleError(errorMessages: Array<String>?)
    fun noConnection()
}