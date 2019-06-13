package pl.sienczykm.templbn.utils

object Config {

    //remember to increase value after changes in db model
    const val DB_VERSION = 2

    val DB_NAME = "lsp.db"

    val PREF_NAME = "normi_pref"

    val TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss"

    val BASE_WEATHER_URL = "http://212.182.4.252/"

    val BASE_SMOG_URL = "http://api.gios.gov.pl/pjp-api/rest/"
}