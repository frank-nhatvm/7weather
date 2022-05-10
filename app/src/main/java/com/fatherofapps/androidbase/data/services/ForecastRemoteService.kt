package com.fatherofapps.androidbase.data.services

import com.fatherofapps.androidbase.BuildConfig
import com.fatherofapps.androidbase.base.network.BaseRemoteService
import com.fatherofapps.androidbase.base.network.NetworkResult
import com.fatherofapps.androidbase.data.apis.ForecastApi
import com.fatherofapps.androidbase.data.requests.TempUnit
import com.fatherofapps.androidbase.data.responses.WeatherInfoResponse
import javax.inject.Inject

class ForecastRemoteService @Inject constructor(private val forecastApi: ForecastApi) :
    BaseRemoteService() {

    suspend fun searchForecastDaily(
        query: String,
        days: Int,
        tempUnit: TempUnit = TempUnit.Metric
    ): NetworkResult<WeatherInfoResponse> {
        val parameters = mutableMapOf<String, String>()
        parameters["q"] = query
        parameters["cnt"] = days.toString()
        parameters["appid"] = BuildConfig.APP_ID
        parameters["units"] = tempUnit.name
        return callApi { forecastApi.searchForecastDaily(parameters = parameters) }
    }

}