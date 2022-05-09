package com.fatherofapps.androidbase.data.models

import com.google.common.truth.Truth
import org.json.JSONObject
import org.junit.Test


class WeatherInfoTest{

    @Test
    fun toJsonObject_aWeatherInfo_returnAJsonObject(){
        val weatherInfo = WeatherInfo(
            date = 999877899,
            averageTemp = 23,
            pressure = 1021,
            humidity = 12,
            description = "rain"
        )
        val json = weatherInfo.toJsonObject()
        Truth.assertThat(json.getLong("date")).isEqualTo(weatherInfo.date)
        Truth.assertThat(json.getInt("average_temp")).isEqualTo(weatherInfo.averageTemp)
        Truth.assertThat(json.getInt("pressure")).isEqualTo(weatherInfo.pressure)
        Truth.assertThat(json.getInt("humidity")).isEqualTo(weatherInfo.humidity)
        Truth.assertThat(json.getString("description")).isEqualTo(weatherInfo.description)
    }

    @Test
    fun fromJsonObject_aJsonObjectFromString_returnAWeatherInfo() {
        val jsonString = "{\n" +
                "\"date\":999877899,\n" +
                "\"average_temp\": 23,\n" +
                "\"pressure\":1234,\n" +
                "\"humidity\":33,\n" +
                "\"description\":\"rain\"\n" +
                "}"
        val json = JSONObject(jsonString)
        val weatherInfo = WeatherInfo.fromJsonObject(json)
        Truth.assertThat(weatherInfo.date).isEqualTo(999877899)
        Truth.assertThat(weatherInfo.averageTemp).isEqualTo(23)
        Truth.assertThat(weatherInfo.pressure).isEqualTo(1234)
        Truth.assertThat(weatherInfo.humidity).isEqualTo(33)
        Truth.assertThat(weatherInfo.description).isEqualTo("rain")
    }

}