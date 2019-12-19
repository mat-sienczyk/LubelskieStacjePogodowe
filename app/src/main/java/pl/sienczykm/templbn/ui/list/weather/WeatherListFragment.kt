package pl.sienczykm.templbn.ui.list.weather

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.databinding.RowWeatherStationBinding
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.list.common.BaseStationListFragment
import pl.sienczykm.templbn.ui.list.common.BaseStationsAdapter

class WeatherListFragment :
    BaseStationListFragment<WeatherStationModel, WeatherListViewModel, RowWeatherStationBinding>() {

    companion object {
        fun newInstance(): WeatherListFragment {
            return WeatherListFragment()
        }
    }

    override fun getViewModel(): WeatherListViewModel {
        return ViewModelProvider(requireActivity()).get(WeatherListViewModel::class.java)
    }

    override fun getSwipeToRefreshLayout(): SwipeRefreshLayout {
        return binding.swipeLayout
    }

    override fun getCoordinatorLayout(): CoordinatorLayout {
        return binding.coordinatorLayout
    }

    override fun getList(): RecyclerView {
        return binding.stationList
    }

    override fun getAdapter(): BaseStationsAdapter<RowWeatherStationBinding> {
        return WeatherListAdapter(this)
    }
}