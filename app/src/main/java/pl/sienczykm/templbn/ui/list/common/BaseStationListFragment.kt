package pl.sienczykm.templbn.ui.list.common

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentListBinding
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.common.RecyclerViewClickListener
import pl.sienczykm.templbn.ui.station.StationActivity
import pl.sienczykm.templbn.utils.LocationUpdates
import pl.sienczykm.templbn.utils.isLocationPermissionGranted
import pl.sienczykm.templbn.utils.setColors
import pl.sienczykm.templbn.utils.snackbarShow
import timber.log.Timber

abstract class BaseStationListFragment<K : BaseStationModel, T : BaseStationListViewModel<K>, L : ViewDataBinding> :
    Fragment(), RecyclerViewClickListener, BaseRefreshListNavigator {

    private lateinit var stationViewModel: T
    lateinit var binding: FragmentListBinding

    private var locationUpdates: LocationUpdates? = null

    abstract fun getViewModel(): T

    abstract fun getSwipeToRefreshLayout(): SwipeRefreshLayout

    abstract fun getCoordinatorLayout(): CoordinatorLayout

    abstract fun getList(): RecyclerView

    abstract fun getAdapter(): BaseStationsAdapter<L>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        binding.viewModel = stationViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stationViewModel = getViewModel()
        stationViewModel.setNavigator(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSwipeToRefreshLayout().apply {
            setOnRefreshListener {
                stationViewModel.refresh()
            }
            setColors()
        }

        getList().layoutManager =
            LinearLayoutManager(requireContext()).apply { orientation = RecyclerView.VERTICAL }
        getList().adapter = getAdapter()
    }

    override fun onResume() {
        super.onResume()
        if (requireContext().isLocationPermissionGranted()) {
            locationUpdates = LocationUpdates(requireContext()) { location ->
                updateLocation(location)
            }
        }
    }

    override fun onPause() {
        locationUpdates?.stop()
        super.onPause()
    }

    private fun updateLocation(location: Location?) {
        stationViewModel.coordinates = location
    }

    override fun scrollToTop() {
        lifecycleScope.launch {
            delay(500)
            getList().smoothScrollToPosition(0)
        }
    }

    override fun onClickItem(v: View, position: Int) {
        when (val station = stationViewModel.stations.value?.get(position)) {
            is WeatherStationModel -> openStationActivity(
                StationActivity.Type.WEATHER,
                station.stationId
            )
            is AirStationModel -> openStationActivity(StationActivity.Type.AIR, station.stationId)
            else -> throw Exception("Invalid station object")
        }
    }

    override fun onLongClickItem(v: View, position: Int) {
        getViewModel().handleFavorite(position)
    }

    private fun getSmoothScrollerToTop(position: Int = 0): LinearSmoothScroller =
        object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }.apply { targetPosition = position }


    override fun handleError(errorMessages: Array<String>?) {
        errorMessages?.forEach { Timber.e(Throwable(it)) }
        showSnackbar(R.string.error_server_list)
    }

    override fun showInfo(message: Int) {
        showSnackbar(message)
    }

    override fun noConnection() {
        showSnackbar(R.string.error_no_connection)
    }

    private fun openStationActivity(type: StationActivity.Type, stationId: Int) {
        val intent = Intent(activity, StationActivity::class.java).apply {
            putExtra(StationActivity.STATION_TYPE_KEY, type)
            putExtra(StationActivity.STATION_ID_KEY, stationId)
        }
        startActivity(intent)
    }

    private fun showSnackbar(@StringRes message: Int) {
        getCoordinatorLayout().snackbarShow(message)
    }

}