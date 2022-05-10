package com.fatherofapps.androidbase.data.services

import com.fatherofapps.androidbase.BuildConfig
import com.fatherofapps.androidbase.MainCoroutineRule
import com.fatherofapps.androidbase.TestNetworkUtils
import com.fatherofapps.androidbase.base.network.BaseNetworkException
import com.fatherofapps.androidbase.base.network.NetworkResult
import com.fatherofapps.androidbase.data.apis.ForecastApi
import com.fatherofapps.androidbase.data.requests.TempUnit
import com.fatherofapps.androidbase.data.responses.TemperatureJson
import com.fatherofapps.androidbase.data.responses.WeatherInfoJson
import com.fatherofapps.androidbase.data.responses.WeatherInfoResponse
import com.fatherofapps.androidbase.data.responses.WeatherJson
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response


@ExperimentalCoroutinesApi
class ForecastRemoteServiceTest {

    @get:Rule
    var mainCoroutinesApi = MainCoroutineRule()

    private lateinit var forecastApi: ForecastApi
    private lateinit var forecastRemoteService: ForecastRemoteService

    @Before
    fun init() {
        forecastApi = Mockito.mock(ForecastApi::class.java)
        forecastRemoteService = ForecastRemoteService(forecastApi)
    }

    @Test
    fun searchForecastDaily_callToSearchForecastDailyOfForecastApi() =
        mainCoroutinesApi.dispatcher.runBlockingTest {
            val query = "ha noi"
            val days = 7
            val tempUnit = TempUnit.Metric

            val parameters = mutableMapOf<String, String>()
            parameters["q"] = query
            parameters["cnt"] = days.toString()
            parameters["appid"] = BuildConfig.APP_ID
            parameters["units"] = tempUnit.name

            Mockito.`when`(forecastApi.searchForecastDaily(parameters))
                .thenReturn(Response.success(fakeWeatherInfoResponse()))
            forecastRemoteService.searchForecastDaily(query, days, tempUnit)

            Mockito.verify(forecastApi, Mockito.times(1)).searchForecastDaily(parameters)
        }

    @Test
    fun searchForecastDaily_withKeywordIsInvalidCity_returnResponseCode404AndNetworkResultErrorWithAMessage()
    = mainCoroutinesApi.dispatcher.runBlockingTest {
        val query = "hac noi"
        val days = 7
        val tempUnit = TempUnit.Metric

        val parameters = mutableMapOf<String, String>()
        parameters["q"] = query
        parameters["cnt"] = days.toString()
        parameters["appid"] = BuildConfig.APP_ID
        parameters["units"] = tempUnit.name
        val errorMessage = "city not found"
        val responseError = Response.error<WeatherInfoResponse>(404,fakeResponseError(404,errorMessage))
        Mockito.`when`(forecastApi.searchForecastDaily(parameters)).thenReturn(responseError)

        val result = forecastRemoteService.searchForecastDaily(query, days, tempUnit)
        Truth.assertThat(result is NetworkResult.Error).isTrue()
        result as NetworkResult.Error
        val exception = result.exception
        Truth.assertThat(exception is BaseNetworkException).isTrue()
        exception as BaseNetworkException
        Truth.assertThat(exception.responseCode).isEqualTo(404)
        Truth.assertThat(exception.mainMessage).isEqualTo(errorMessage)
    }

    @Test
    fun searchForecastDaily_withKeywordIsValidCity_returnNetworkResultSuccessWithAWeatherInfoResponse() = mainCoroutinesApi.dispatcher.runBlockingTest {
        val query = "ha noi"
        val days = 7
        val tempUnit = TempUnit.Metric

        val parameters = mutableMapOf<String, String>()
        parameters["q"] = query
        parameters["cnt"] = days.toString()
        parameters["appid"] = BuildConfig.APP_ID
        parameters["units"] = tempUnit.name

        val weatherInfoResponse = fakeWeatherInfoResponse()
        val weatherInfoJson = weatherInfoResponse.list?.firstOrNull()
        Mockito.`when`(forecastApi.searchForecastDaily(parameters)).thenReturn(Response.success(weatherInfoResponse))

        val result = forecastRemoteService.searchForecastDaily(query, days, tempUnit)
        Truth.assertThat(result is NetworkResult.Success).isTrue()
        result as NetworkResult.Success
        val resultWeatherInfoResponse  = result.data
        Truth.assertThat(resultWeatherInfoResponse).isNotNull()
        Truth.assertThat(resultWeatherInfoResponse.list).isNotNull()
        val firstItem = resultWeatherInfoResponse.list?.firstOrNull()
        Truth.assertThat(firstItem).isNotNull()
        Truth.assertThat(firstItem?.date).isEqualTo(weatherInfoJson?.date)
        Truth.assertThat(firstItem?.pressure).isEqualTo(weatherInfoJson?.pressure)
        Truth.assertThat(firstItem?.humidity).isEqualTo(weatherInfoJson?.humidity)
        val tempJson = firstItem?.temp
        Truth.assertThat(tempJson).isNotNull()
        Truth.assertThat(tempJson?.getAverageTemp()).isEqualTo(weatherInfoJson?.temp?.getAverageTemp())
        Truth.assertThat(firstItem?.weather).isNotNull()
        Truth.assertThat(firstItem?.weather?.size).isEqualTo(1)
        val weatherJson = firstItem?.weather?.firstOrNull()
        Truth.assertThat(weatherJson).isNotNull()
        Truth.assertThat(firstItem?.getDescription()).isEqualTo(weatherInfoJson?.getDescription())
    }

    private fun fakeResponseError(code: Int, message: String?): ResponseBody {
        val json = JSONObject()
        json.put("cod", code.toString())
        json.put("message", message)
        return TestNetworkUtils.createBody(code,json.toString())!!
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

}