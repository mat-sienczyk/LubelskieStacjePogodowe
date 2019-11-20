package pl.sienczykm.templbn.ui.common

import android.view.View

interface RecyclerViewClickListener {

    fun onClickItem(v: View, position: Int)

    fun onLongClickItem(v: View, position: Int)
}
