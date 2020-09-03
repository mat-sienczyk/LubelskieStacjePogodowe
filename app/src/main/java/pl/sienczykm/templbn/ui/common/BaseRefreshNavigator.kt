package pl.sienczykm.templbn.ui.common

import androidx.annotation.StringRes

interface BaseRefreshNavigator : Navigator {
    fun showInfo(@StringRes message: Int)
    fun handleError(errorMessages: Array<String>?)
    fun noConnection()
}