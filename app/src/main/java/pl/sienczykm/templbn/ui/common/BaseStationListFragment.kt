package pl.sienczykm.templbn.ui.common

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.BR
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.SmogStationModel
import pl.sienczykm.templbn.db.model.StationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.station.StationActivity
import pl.sienczykm.templbn.ui.station.StationFragment
import pl.sienczykm.templbn.utils.snackbarShow
import timber.log.Timber

abstract class BaseStationListFragment<K : StationModel, T : BaseStationListViewModel<K>, N : ViewDataBinding, L : ViewDataBinding> :
    Fragment(), RecyclerViewClickListener, BaseNavigator {

    lateinit var stationViewModel: T
    lateinit var binding: N

    abstract fun getViewModel(): T

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getSwipeToRefreshLayout(): SwipeRefreshLayout

    abstract fun getCoordinatorLayout(): CoordinatorLayout

    abstract fun getList(): RecyclerView

    abstract fun getAdapter(): BaseStationsAdapter<L>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.setVariable(BR.viewModel, stationViewModel)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stationViewModel = getViewModel()
        stationViewModel.setNavigator(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSwipeToRefreshLayout().setOnRefreshListener {
            stationViewModel.refresh()
        }

        getSwipeToRefreshLayout().setColorSchemeColors(
            resources.getColor(R.color.main_yellow),
            resources.getColor(R.color.main_red),
            resources.getColor(R.color.main_green)
        )

        val mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.orientation = RecyclerView.VERTICAL
        getList().layoutManager = mLayoutManager
        getList().adapter = getAdapter()
    }

    override fun onClickItem(v: View, position: Int) {
        when (val station = stationViewModel.stations.value?.get(position)) {
            is WeatherStationModel -> openStationActivity(StationFragment.Type.WEATHER, station.stationId)
            is SmogStationModel -> openStationActivity(StationFragment.Type.SMOG, station.stationId)
            else -> throw Exception("Invalid station object")
        }
    }

    override fun onLongClickItem(v: View, position: Int) {
        Timber.e("Long clicked: %s", stationViewModel.stations.value?.get(position)?.stationId)
    }

    override fun handleError(message: String?) {
        Timber.e(Throwable(message))
        showError(R.string.error_server)
    }

    override fun noConnection() {
        showError(R.string.error_no_connection)
    }

    private fun openStationActivity(type: StationFragment.Type, stationId: Int) {
        val intent = Intent(activity, StationActivity::class.java).apply {
            putExtra(StationFragment.STATION_TYPE_KEY, type)
            putExtra(StationFragment.STATION_ID_KEY, stationId)
        }
        startActivity(intent)
    }

    private fun showError(@StringRes message: Int) {
        snackbarShow(getCoordinatorLayout(), message)
    }

}