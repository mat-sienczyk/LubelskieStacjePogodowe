package pl.sienczykm.templbn.db.model

data class SmogSensorDb(
    val sensorId: Int? = null,
    val paramName: String? = null,
    val paramCode: String? = null,
    val data: List<DataModelDb>? = null
)