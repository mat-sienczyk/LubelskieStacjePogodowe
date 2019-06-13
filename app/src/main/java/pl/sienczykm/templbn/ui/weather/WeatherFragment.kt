package pl.sienczykm.templbn.ui.weather

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.WeatherFragmentBinding
import pl.sienczykm.templbn.db.model.WeatherStationDb
import pl.sienczykm.templbn.ui.common.BaseStationListFragment
import pl.sienczykm.templbn.utils.UpdateHandler

class WeatherFragment : BaseStationListFragment<WeatherStationDb, WeatherViewModel, WeatherFragmentBinding>() {

    companion object {
        fun newInstance(): WeatherFragment {
            val args = Bundle()
            val fragment = WeatherFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getViewModel(): WeatherViewModel {
        return activity?.run {
            ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun getLayoutId(): Int {
        return R.layout.weather_fragment
    }

    override fun getSwipeToRefreshLayout(): SwipeRefreshLayout {
        return binding.swipe
    }

    override fun updateJob() {
        UpdateHandler.syncNowWeatherStations(activity?.applicationContext!!)
    }
}