package pl.sienczykm.templbn.ui.about

interface AboutNavigator {
    fun openGooglePlay(appId: String)
    fun openDialog(dialogType: AboutFragment.DialogType)
    fun openUrl(url: String)
}