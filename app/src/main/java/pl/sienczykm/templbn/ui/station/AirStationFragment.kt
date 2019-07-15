package pl.sienczykm.templbn.ui.station

import android.os.Bundle
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentAirStationBinding
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.ui.common.BaseStationFragment

class AirStationFragment :
    BaseStationFragment<AirStationModel, AirStationViewModel, FragmentAirStationBinding>() {

    companion object {

        fun newInstance(stationId: Int): AirStationFragment {
            val args = Bundle()
            args.putInt(StationActivity.STATION_ID_KEY, stationId)
            val fragment = AirStationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getViewModel(stationId: Int): AirStationViewModel {
        return ViewModelProviders.of(
            requireActivity(),
            AirStationViewModelFactory(requireActivity().application, stationId)
        ).get(AirStationViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_air_station
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