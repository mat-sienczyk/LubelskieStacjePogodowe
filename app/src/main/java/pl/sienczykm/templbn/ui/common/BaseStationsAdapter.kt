package pl.sienczykm.templbn.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import pl.sienczykm.templbn.db.model.BaseStationModel

abstract class BaseStationsAdapter<N : ViewDataBinding>(val clickListener: RecyclerViewClickListener) :
    RecyclerView.Adapter<BaseStationsAdapter<N>.StationsViewHolder>() {

    lateinit var stations: List<BaseStationModel>

    fun updateStations(stations: List<BaseStationModel>) {
        this.stations = stations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return StationsViewHolder(getViewDataBinding(parent, layoutInflater))
    }

    override fun getItemCount(): Int {
        return stations.size
    }

    override fun onBindViewHolder(holder: StationsViewHolder, position: Int) {
        holder.bind(stations[position])
    }

    inner class StationsViewHolder(stationBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(stationBinding.root), View.OnClickListener, View.OnLongClickListener {

        private val binding = stationBinding

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener.onClickItem(v!!, layoutPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            clickListener.onLongClickItem(v!!, layoutPosition)
            return true
        }

        fun bind(station: BaseStationModel) {
            binding.setVariable(BR.station, station)
            binding.executePendingBindings()
        }

    }

    abstract fun getViewDataBinding(parent: ViewGroup, layoutInflater: LayoutInflater): ViewDataBinding

}
