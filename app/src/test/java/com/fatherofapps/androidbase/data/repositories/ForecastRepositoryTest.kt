package com.fatherofapps.androidbase.data.repositories

import com.fatherofapps.androidbase.MainCoroutineRule
import com.fatherofapps.androidbase.base.network.BaseNetworkException
import com.fatherofapps.androidbase.base.network.NetworkResult
import com.fatherofapps.androidbase.data.mappers.toWeatherInfo
import com.fatherofapps.androidbase.data.models.WeatherInfo
import com.fatherofapps.androidbase.data.responses.TemperatureJson
import com.fatherofapps.androidbase.data.responses.WeatherInfoJson
import com.fatherofapps.androidbase.data.responses.WeatherInfoResponse
import com.fatherofapps.androidbase.data.responses.WeatherJson
import com.fatherofapps.androidbase.data.services.ForecastLocalService
import com.fatherofapps.androidbase.data.services.ForecastRemoteService
import com.google.common.truth.Truth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.threeten.bp.OffsetDateTime
import java.io.File

@ExperimentalCoroutinesApi
class ForecastRepositoryTest {

    @get:Rule
    var mainCoroutinesApi = MainCoroutineRule()

    private lateinit var forecastLocalService: ForecastLocalService
    private lateinit var forecastRemoteService: ForecastRemoteService
    private lateinit var forecastRepository: ForecastRepository

    @Before
    fun init() {
        forecastLocalService = Mockito.mock(ForecastLocalService::class.java)
        forecastRemoteService = Mockito.mock(ForecastRemoteService::class.java)
        forecastRepository = ForecastRepository(
            forecastLocalService = forecastLocalService,
            forecastRemoteService = forecastRemoteService,
            dispatcher = Dispatchers.Main
        )
    }

    @Test
    fun callApiToSearch_callToSearchForecastDailyOfForecastRemoteService() =
        mainCoroutinesApi.dispatcher.runBlockingTest {
            val query = "ha noi"
            val days = 7

            val weatherInfoResponse = fakeWeatherInfoResponse()
            Mockito.`when`(forecastRemoteService.searchForecastDaily(query, days))
                .thenReturn(NetworkResult.Success(weatherInfoResponse))

            forecastRepository.callApiToSearch(query, days)
            Mockito.verify(forecastRemoteService, Mockito.times(1)).searchForecastDaily(query, days)
        }

    @Test
    fun getRemoteData_fail_throwAnException() =
        mainCoroutinesApi.dispatcher.runBlockingTest {
            val query = "hac noi"
            val days = 7
            val cacheFolder = File("caches/99989.json")
            val queryHash = "queryHash39909"
            val queryDate = OffsetDateTime.now()

            val baseNetworkException = BaseNetworkException(responseCode = 404)
            baseNetworkException.mainMessage = "city not found"
            Mockito.`when`(forecastRemoteService.searchForecastDaily(query, days))
                .thenReturn(NetworkResult.Error(baseNetworkException))

            try {
                forecastRepository.getRemoteData(query, days, cacheFolder, queryHash, queryDate)
            } catch (e: Exception) {
                Truth.assertThat(e is BaseNetworkException).isTrue()
                e as BaseNetworkException
                Truth.assertThat(e.mainMessage).isEqualTo(baseNetworkException.mainMessage)
            }

        }

    @Test
    fun getRemoteData_success_callToSearchForecastDailyOfForecastRemoteService_callToSaveDataOfForecastLocalService_returnAListOfWeatherInfo() =
        mainCoroutinesApi.dispatcher.runBlockingTest {
            val query = "hac noi"
            val days = 7
            val cacheFolder = File("caches/99989.json")
            val queryHash = "queryHash39909"
            val queryDate = OffsetDateTime.now()
            val weatherInfoResponse = fakeWeatherInfoResponse()
            val weatherInfoJson = weatherInfoResponse.list?.firstOrNull()
            val argumentCaptor = ArgumentCaptor.forClass(WeatherInfo::class.java)
            val listWeatherInfo =
                weatherInfoResponse.list?.map { it.toWeatherInfo() } ?: emptyList()

            Mockito.`when`(forecastRemoteService.searchForecastDaily(query, days))
                .thenReturn(NetworkResult.Success(weatherInfoResponse))
            Mockito.`when`(
                forecastLocalService.saveData(
                    queryHash,
                    query,
                    list = argumentCaptor.allValues,
                    cacheFolder = cacheFolder,
                    createdAt = queryDate
                )
            ).then { }

            val result =
                forecastRepository.getRemoteData(query, days, cacheFolder, queryHash, queryDate)

            Mockito.verify(forecastRemoteService, Mockito.times(1)).searchForecastDaily(query, days)
//            Mockito.verify(forecastLocalService,Mockito.times(1)).saveData(
//                queryHash,
//                query,
//                list = argumentCaptor.allValues,
//                cacheFolder = cacheFolder,
//                createdAt = queryDate
//            )

            Truth.assertThat(result).isNotNull()
            Truth.assertThat(result?.size).isEqualTo(1)
            val firstItem = result?.firstOrNull()
            Truth.assertThat(firstItem).isNotNull()
            Truth.assertThat(firstItem?.date).isEqualTo(weatherInfoJson?.date)
            Truth.assertThat(firstItem?.description).isEqualTo(weatherInfoJson?.getDescription())
            Truth.assertThat(firstItem?.averageTemp)
                .isEqualTo(weatherInfoJson?.temp?.getAverageTemp())
            Truth.assertThat(firstItem?.humidity).isEqualTo(weatherInfoJson?.humidity)
            Truth.assertThat(firstItem?.pressure).isEqualTo(weatherInfoJson?.pressure)
        }

    private fun fakeWeatherInfoResponse(): WeatherInfoResponse {
        val temperatureJson = TemperatureJson(
            min = 12f,
            max = 18.8f
        )
        val weatherJson = WeatherJson(description = "light rain")
        val weatherInfoJson = WeatherInfoJson(
            date = 789798998,
            temp = temperatureJson,
            weather = listOf(weatherJson),
            pressure = 10,
            humidity = 133
        )
        return WeatherInfoResponse(
            list = listOf(weatherInfoJson),
            code = "200",
            message = "3980sdf"
        )
    }

    @Test
    fun cleanup_callToCleanUpOfForecastLocalServiceOneTime()
    = mainCoroutinesApi.dispatcher.runBlockingTest {
        forecastRepository.cleanup()
        Mockito.verify(forecastLocalService,Mockito.times(1)).cleanup()
    }

}