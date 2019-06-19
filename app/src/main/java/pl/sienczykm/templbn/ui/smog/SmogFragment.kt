package pl.sienczykm.templbn.ui.smog

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentSmogBinding
import pl.sienczykm.templbn.databinding.RowSmogStationBinding
import pl.sienczykm.templbn.db.model.SmogStationModel
import pl.sienczykm.templbn.ui.common.BaseStationListFragment
import pl.sienczykm.templbn.ui.common.BaseStationsAdapter

class SmogFragment :
    BaseStationListFragment<SmogStationModel, SmogViewModel, FragmentSmogBinding, RowSmogStationBinding>() {

    companion object {
        fun newInstance(): SmogFragment {
            return SmogFragment()
        }
    }

    override fun getViewModel(): SmogViewModel {
        return activity?.run {
            ViewModelProviders.of(this).get(SmogViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_smog
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

    override fun getAdapter(): BaseStationsAdapter<RowSmogStationBinding> {
        return SmogAdapter(this)
    }
}