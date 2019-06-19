package pl.sienczykm.templbn.ui.weather

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentWeatherBinding
import pl.sienczykm.templbn.databinding.RowWeatherStationBinding
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.common.BaseStationListFragment
import pl.sienczykm.templbn.ui.common.BaseStationsAdapter

class WeatherFragment :
    BaseStationListFragment<WeatherStationModel, WeatherViewModel, FragmentWeatherBinding, RowWeatherStationBinding>() {

    companion object {
        fun newInstance(): WeatherFragment {
            return WeatherFragment()
        }
    }

    override fun getViewModel(): WeatherViewModel {
        return activity?.run {
            ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_weather
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