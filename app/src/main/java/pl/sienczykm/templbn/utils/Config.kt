package pl.sienczykm.templbn.utils

object Config {

    //remember to increase value after changes in db model
    const val DB_VERSION = 4

    const val DB_NAME = "lsp.db"

    const val PREF_NAME = "lsp_pref"

    const val TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss"

    const val BASE_WEATHER_URL = "http://212.182.4.252/"

    const val BASE_AIR_URL = "http://api.gios.gov.pl/pjp-api/rest/"
}