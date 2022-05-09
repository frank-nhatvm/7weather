package com.fatherofapps.androidbase.data.models

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
    }

}