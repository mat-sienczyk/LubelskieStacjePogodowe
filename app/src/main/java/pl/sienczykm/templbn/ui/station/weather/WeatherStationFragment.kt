package pl.sienczykm.templbn.ui.station.weather

import android.os.Bundle
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentWeatherStationBinding
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.common.BaseStationFragment
import pl.sienczykm.templbn.ui.station.StationActivity

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
        return ViewModelProviders.of(
            requireActivity(),
            WeatherStationViewModelFactory(
                requireActivity().application,
                stationId
            )
        ).get(WeatherStationViewModel::class.java)
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

    override fun getBottomSheetLayout(): LinearLayout {
        return binding.bottomSheet.bottomSheet
    }
}