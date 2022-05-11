package com.fatherofapps.androidbase.data.models

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class WeatherInfo(
    val date: Long?,
    val averageTemp: Int?,
    val pressure: Int?,
    val humidity:Int?,
    val description: String?
){

    fun getFormatDate(): String{

        date?.let {
            val inputDate = Date(date*1000L)
            val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            return outputFormat.format(inputDate)
        }

        return ""
    }

    fun getFormatTemp(): String{
        averageTemp?.let {
            return "$averageTemp°C"
        }
        return ""
    }

    fun getFormatHumidity(): String{
        humidity?.let {
            return "$humidity%"
        }
        return ""
    }

    fun toJsonObject(): JSONObject {
        val json = JSONObject()
        json.put("date",date)
        json.put("average_temp",averageTemp)
        json.put("pressure",pressure)
        json.put("humidity",humidity)
        json.put("description",description)
        return  json
    }

    companion object {
        fun fromJsonObject(json: JSONObject): WeatherInfo {
            val date = json.getLong("date")
            val averageTemp = json.getInt("average_temp")
            val pressure = json.getInt("pressure")
            val humidity = json.getInt("humidity")
            val description = json.getString("description")
            return WeatherInfo(
                date = date,
                averageTemp = averageTemp,
                pressure = pressure,
                humidity = humidity,
                description = description
            )
        }
    }
}