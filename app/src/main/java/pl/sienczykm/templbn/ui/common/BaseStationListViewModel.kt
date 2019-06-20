package pl.sienczykm.templbn.ui.common

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pl.sienczykm.templbn.background.ProcessingUtils
import pl.sienczykm.templbn.background.StatusReceiver
import java.lang.ref.WeakReference

abstract class BaseStationListViewModel<T>(application: Application) : BaseRefreshViewModel<BaseNavigator>(application) {

    abstract val stations: LiveData<List<T>>

}