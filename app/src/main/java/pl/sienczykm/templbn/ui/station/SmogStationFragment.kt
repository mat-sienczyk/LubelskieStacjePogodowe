package pl.sienczykm.templbn.ui.station

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentSmogStationBinding
import pl.sienczykm.templbn.db.model.SmogStationModel
import pl.sienczykm.templbn.ui.common.BaseStationFragment

class SmogStationFragment :
    BaseStationFragment<SmogStationModel, SmogStationViewModel, FragmentSmogStationBinding>() {

    companion object {

        fun newInstance(stationId: Int): SmogStationFragment {
            val args = Bundle()
            args.putInt(StationActivity.STATION_ID_KEY, stationId)
            val fragment = SmogStationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getViewModel(stationId: Int): SmogStationViewModel {
        return activity?.run {
            ViewModelProviders.of(
                this,
                SmogStationViewModelFactory(application, stationId)
            ).get(SmogStationViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_smog_station
    }

    override fun getSwipeToRefreshLayout(): SwipeRefreshLayout {
        return binding.swipeLayout
    }

    override fun getCoordinatorLayout(): CoordinatorLayout {
        return binding.coordinatorLayout
    }
}