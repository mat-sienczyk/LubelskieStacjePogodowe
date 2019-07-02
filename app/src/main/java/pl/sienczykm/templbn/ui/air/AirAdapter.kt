package pl.sienczykm.templbn.ui.air

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import pl.sienczykm.templbn.databinding.RowAirStationBinding
import pl.sienczykm.templbn.ui.common.BaseStationsAdapter
import pl.sienczykm.templbn.ui.common.RecyclerViewClickListener

class AirAdapter(clickListener: RecyclerViewClickListener) :
    BaseStationsAdapter<RowAirStationBinding>(clickListener) {

    override fun getViewDataBinding(parent: ViewGroup, layoutInflater: LayoutInflater): ViewDataBinding {
        return RowAirStationBinding.inflate(layoutInflater, parent, false)
    }
}