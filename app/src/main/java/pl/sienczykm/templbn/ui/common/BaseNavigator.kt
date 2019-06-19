package pl.sienczykm.templbn.ui.common

interface BaseNavigator {
    fun handleError(message: String?)
    fun noConnection()
}