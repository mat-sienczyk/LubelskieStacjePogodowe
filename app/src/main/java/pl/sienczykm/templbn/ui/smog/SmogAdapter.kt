package pl.sienczykm.templbn.ui.smog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import pl.sienczykm.templbn.databinding.RowSmogStationBinding
import pl.sienczykm.templbn.ui.common.RecyclerViewClickListener
import pl.sienczykm.templbn.ui.common.BaseStationsAdapter

class SmogAdapter(clickListener: RecyclerViewClickListener) : BaseStationsAdapter<RowSmogStationBinding>(clickListener) {

    override fun getViewDataBinding(parent: ViewGroup, layoutInflater: LayoutInflater): ViewDataBinding {
        return RowSmogStationBinding.inflate(layoutInflater, parent, false)
    }
}