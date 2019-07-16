package pl.sienczykm.templbn.ui.list.weather

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.databinding.RowWeatherStationBinding
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.list.common.BaseStationListFragment
import pl.sienczykm.templbn.ui.list.common.BaseStationsAdapter

class WeatherFragment :
    BaseStationListFragment<WeatherStationModel, WeatherViewModel, RowWeatherStationBinding>() {

    companion object {
        fun newInstance(): WeatherFragment {
            return WeatherFragment()
        }
    }

    override fun getViewModel(): WeatherViewModel {
        return ViewModelProviders.of(requireActivity()).get(WeatherViewModel::class.java)
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
        return WeatherAdapter(this)
    }
}