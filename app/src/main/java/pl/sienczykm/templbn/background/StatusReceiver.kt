package pl.sienczykm.templbn.background

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver

class StatusReceiver(handler: Handler, private val receiver: Receiver?) : ResultReceiver(handler) {

    companion object {
        const val STATUS_IDLE = 0
        const val STATUS_RUNNING = 1
        const val STATUS_ERROR = 2
        const val STATUS_NO_CONNECTION = 3
    }

    interface Receiver {
        fun onReceiveResult(resultCode: Int, resultData: Bundle)
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        receiver?.onReceiveResult(resultCode, resultData)
    }
}