package com.fatherofapps.androidbase.data.responses
import com.squareup.moshi.Json

data class WeatherInfoResponse(
    val list: List<WeatherInfoJson>?,
    @Json(name = "cod")
    val code: String?,
    val message: String?
)

class WeatherInfoJson(
    @Json(name = "dt")
    val date: Long?,
    val temp: TemperatureJson?,
    val weather: List<WeatherJson>?,
    val pressure: Int?,
    val humidity: Int?
) {
    fun getDescription(): String? {
        return weather?.get(0)?.description
    }
}

class TemperatureJson(
    val min: Float?,
    val max: Float?
) {
    fun getAverageTemp(): Int {
        if (min != null && null != max) {
            val temp = (min + max) / 2
            return temp.toInt()
        }

        return 0
    }
}

data class WeatherJson(
    val description: String?
)