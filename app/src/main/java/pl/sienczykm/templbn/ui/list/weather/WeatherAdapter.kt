package pl.sienczykm.templbn.ui.list.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import pl.sienczykm.templbn.databinding.RowWeatherStationBinding
import pl.sienczykm.templbn.ui.common.RecyclerViewClickListener
import pl.sienczykm.templbn.ui.list.common.BaseStationsAdapter

class WeatherAdapter(clickListener: RecyclerViewClickListener) : BaseStationsAdapter<RowWeatherStationBinding>(clickListener) {

    override fun getViewDataBinding(parent: ViewGroup, layoutInflater: LayoutInflater): ViewDataBinding {
        return RowWeatherStationBinding.inflate(layoutInflater, parent, false)
    }
}