package pl.sienczykm.templbn.db.model

data class SmogSensorModel(
    val sensorId: Int? = null,
    val paramName: String? = null,
    val paramCode: String? = null,
    val data: List<ChartDataModel>? = null
)