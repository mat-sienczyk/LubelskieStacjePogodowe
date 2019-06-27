package pl.sienczykm.templbn.ui.station

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentWeatherStationBinding
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.common.BaseStationFragment

class WeatherStationFragment :
    BaseStationFragment<WeatherStationModel, WeatherStationViewModel, FragmentWeatherStationBinding>() {

    companion object {

        fun newInstance(stationId: Int): WeatherStationFragment {
            val args = Bundle()
            args.putInt(StationActivity.STATION_ID_KEY, stationId)
            val fragment = WeatherStationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getViewModel(stationId: Int): WeatherStationViewModel {
        return activity?.run {
            ViewModelProviders.of(
                this,
                WeatherStationViewModelFactory(application, stationId)
            ).get(WeatherStationViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_weather_station
    }

    override fun getSwipeToRefreshLayout(): SwipeRefreshLayout {
        return binding.swipeLayout
    }

    override fun getCoordinatorLayout(): CoordinatorLayout {
        return binding.coordinatorLayout
    }
}