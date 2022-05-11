package com.fatherofapps.androidbase.data.repositories

import androidx.annotation.VisibleForTesting
import com.fatherofapps.androidbase.base.network.BaseNetworkException
import com.fatherofapps.androidbase.base.network.NetworkResult
import com.fatherofapps.androidbase.common.Utils
import com.fatherofapps.androidbase.data.mappers.toWeatherInfo
import com.fatherofapps.androidbase.data.models.WeatherInfo
import com.fatherofapps.androidbase.data.responses.WeatherInfoResponse
import com.fatherofapps.androidbase.data.services.ForecastLocalService
import com.fatherofapps.androidbase.data.services.ForecastRemoteService
import com.fatherofapps.androidbase.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import java.io.File

import javax.inject.Inject

class ForecastRepository @Inject constructor(
    private val forecastRemoteService: ForecastRemoteService,
    private val forecastLocalService: ForecastLocalService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {


    suspend fun search(query: String, days: Int, cacheFolder: File,queryDate: OffsetDateTime) = withContext(dispatcher) {
        val queryHash = Utils.md5("$query$days")
        forecastLocalService.getCacheData(queryHash = queryHash)
            ?: getRemoteData(query, days, cacheFolder, queryHash,queryDate)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun getRemoteData(
        query: String,
        days: Int,
        cacheFolder: File,
        queryHash: String,
        queryDate: OffsetDateTime
    ): List<WeatherInfo>? {
        val listWeatherInfo = callApiToSearch(query = query, days = days)
        if (listWeatherInfo?.isNotEmpty() == true) {
            forecastLocalService.saveData(
                queryHash = queryHash,
                query = query,
                list = listWeatherInfo,
                cacheFolder = cacheFolder,
                createdAt = queryDate
            )
        }
        return listWeatherInfo
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun callApiToSearch(query: String, days: Int): List<WeatherInfo>? {
        return when (val result =
            forecastRemoteService.searchForecastDaily(query = query, days = days)) {
            is NetworkResult.Success -> {
                result.data.list?.map { it.toWeatherInfo() }
            }
            is NetworkResult.Error -> {
                throw  result.exception
            }
        }
    }

    suspend fun cleanup() = withContext(dispatcher){
        forecastLocalService.cleanup()
    }

}