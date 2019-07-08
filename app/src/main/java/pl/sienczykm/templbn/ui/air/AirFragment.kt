package pl.sienczykm.templbn.ui.air

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentAirListBinding
import pl.sienczykm.templbn.databinding.RowAirStationBinding
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.ui.common.BaseStationListFragment
import pl.sienczykm.templbn.ui.common.BaseStationsAdapter

class AirFragment :
    BaseStationListFragment<AirStationModel, AirViewModel, FragmentAirListBinding, RowAirStationBinding>() {

    companion object {
        fun newInstance(): AirFragment {
            return AirFragment()
        }
    }

    override fun getViewModel(): AirViewModel {
        return activity?.run {
            ViewModelProviders.of(this).get(AirViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_air_list
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

    override fun getAdapter(): BaseStationsAdapter<RowAirStationBinding> {
        return AirAdapter(this)
    }
}