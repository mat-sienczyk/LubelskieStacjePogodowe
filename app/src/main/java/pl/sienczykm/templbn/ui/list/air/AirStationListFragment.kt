package pl.sienczykm.templbn.ui.list.air

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentAirListBinding
import pl.sienczykm.templbn.databinding.RowAirStationBinding
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.ui.common.BaseStationListFragment
import pl.sienczykm.templbn.ui.list.common.BaseStationsAdapter

class AirStationListFragment :
    BaseStationListFragment<AirStationModel, AirStationListViewModel, FragmentAirListBinding, RowAirStationBinding>() {

    companion object {
        fun newInstance(): AirStationListFragment {
            return AirStationListFragment()
        }
    }

    override fun getViewModel(): AirStationListViewModel {
        return ViewModelProviders.of(requireActivity()).get(AirStationListViewModel::class.java)
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
        return AirStationListAdapter(this)
    }
}