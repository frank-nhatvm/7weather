package com.fatherofapps.androidbase.data.mappers

import com.fatherofapps.androidbase.data.models.WeatherInfo
import com.fatherofapps.androidbase.data.responses.WeatherInfoJson

fun WeatherInfoJson.toWeatherInfo() : WeatherInfo{

    return  WeatherInfo(
        date = date,
        averageTemp = temp?.getAverageTemp(),
        pressure = pressure,
        humidity = humidity,
        description = getDescription()
    )
}