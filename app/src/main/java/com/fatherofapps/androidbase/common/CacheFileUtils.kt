package com.fatherofapps.androidbase.common

import com.fatherofapps.androidbase.data.models.WeatherInfo
import org.json.JSONArray
import java.io.File
import javax.inject.Inject

class CacheFileUtils @Inject constructor(){

    fun getCacheDataFromFile(filePath: String): List<WeatherInfo> {
        val file = File(filePath)
        val content = file.readText()
        val jsonArray = JSONArray(content)
        val listWeatherInfo = mutableListOf<WeatherInfo>()
        val length = jsonArray.length() - 1
        for (index in 0..length) {
            val jsonObject = jsonArray.getJSONObject(index)
            val weatherInfo = WeatherInfo.fromJsonObject(jsonObject)
            listWeatherInfo.add(weatherInfo)
        }
        return listWeatherInfo
    }

    fun writeDataToFile(list: List<WeatherInfo>, cacheFolder: File): String {
        val jsonArray = JSONArray()
        list.forEach { weatherInfo ->
            jsonArray.put(weatherInfo.toJsonObject())
        }
        val cacheFile = File(cacheFolder, getCacheFileName())
        cacheFile.writeText(jsonArray.toString(), Charsets.UTF_8)
        return cacheFile.absolutePath
    }

    private fun getCacheFileName(): String {
        return "${System.currentTimeMillis()}.json"
    }

    fun deleteCacheFile(filePath: String){
        val file = File(filePath)
        file.delete()
    }
}