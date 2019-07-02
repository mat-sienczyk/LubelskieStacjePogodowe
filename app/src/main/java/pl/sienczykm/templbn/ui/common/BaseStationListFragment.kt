package pl.sienczykm.templbn.ui.common

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import com.google.android.gms.location.LocationServices
import pl.sienczykm.templbn.BR
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.main.MainActivity
import pl.sienczykm.templbn.ui.station.StationActivity
import pl.sienczykm.templbn.utils.isLocationPermissionGranted
import pl.sienczykm.templbn.utils.snackbarShow
import timber.log.Timber

abstract class BaseStationListFragment<K : BaseStationModel, T : BaseStationListViewModel<K>, N : ViewDataBinding, L : ViewDataBinding> :
    Fragment(), RecyclerViewClickListener, BaseNavigator {

    var coordinates: LatLon? = null

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
            updateCoordinate()
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

        if (savedInstanceState == null) updateCoordinate()
    }

    fun updateCoordinate() {
        if (activity.isLocationPermissionGranted()) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    when {
                        location != null -> stationViewModel.coordinates = LatLon(location.latitude, location.longitude)
                        else -> stationViewModel.coordinates = null
                    }
                }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MainActivity.PERMISSIONS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    updateCoordinate()
                }
                return
            }
        }
    }

    override fun onClickItem(v: View, position: Int) {
        when (val station = stationViewModel.stations.value?.get(position)) {
            is WeatherStationModel -> openStationActivity(StationActivity.Type.WEATHER, station.stationId)
            is AirStationModel -> openStationActivity(StationActivity.Type.AIR, station.stationId)
            else -> throw Exception("Invalid station object")
        }
    }

    override fun onLongClickItem(v: View, position: Int) {
        val station = stationViewModel.stations.value?.get(position)
        val updated = when (station) {
            is WeatherStationModel -> AppDb.getDatabase(activity?.applicationContext!!).weatherStationDao().updateFavorite(
                station.stationId,
                !station.favorite
            )
            is AirStationModel -> AppDb.getDatabase(activity?.applicationContext!!).airStationDao().updateFavorite(
                station.stationId,
                !station.favorite
            )
            else -> throw Exception("Invalid station object")
        }

        if (updated == 1) {
            if (station.favorite) showSnackbar(R.string.removed_from_favorites) else showSnackbar(R.string.added_to_favorites)
            getList().smoothScrollToPosition(0)
        }
    }

    override fun handleError(message: String?) {
        Timber.e(Throwable(message))
        showSnackbar(R.string.error_server)
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
        snackbarShow(getCoordinatorLayout(), message)
    }

}