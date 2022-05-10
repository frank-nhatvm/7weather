package com.fatherofapps.androidbase.data.apis

import com.fatherofapps.androidbase.data.responses.WeatherInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ForecastApi {
    @GET("data/2.5/forecast/daily")
    suspend fun searchForecastDaily(
        @QueryMap  parameters: Map<String, String> = mapOf()
    ): Response<WeatherInfoResponse>
}