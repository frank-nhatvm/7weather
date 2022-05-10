package com.fatherofapps.androidbase.data.apis

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fatherofapps.androidbase.MainCoroutineRule
import com.google.common.truth.Truth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ForecastApiTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var mockServer: MockWebServer

    private lateinit var forecastApi: ForecastApi

    @Before
    fun setup() {
        mockServer = MockWebServer()
        mockServer.start()
        val url = mockServer.url("/")
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val moshiConverter = MoshiConverterFactory.create(moshi)

        val retrofit = Retrofit.Builder().addConverterFactory(moshiConverter)
            .baseUrl(url)
            .build()

        forecastApi = retrofit.create(ForecastApi::class.java)
    }

    @After
    fun stopService() {
        mockServer.shutdown()
    }


    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap(), responseCode: Int = 200) {

        val inputStream = javaClass.classLoader!!
            .getResourceAsStream(fileName)

        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(responseCode)
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }

    @Test
    fun searchForecastDaily_withWrongApiKey_returnResponseCode401AndErrorMessage()
    = runBlocking {
        enqueueResponse("forecastapis/searchForecastDaily_wrong_api_key.json", responseCode = 401)
        val parameters = mutableMapOf<String, String>()
        parameters["q"] = "query"
        parameters["cnt"] = "7"
        parameters["appid"] = "wrong_key"
        val response = forecastApi.searchForecastDaily()
        Truth.assertThat(response.code()).isEqualTo(401)

        val data = response.errorBody()?.string() ?: ""
        Truth.assertThat(data).isNotNull()
        val jsonError = JSONObject(data)
        Truth.assertThat(jsonError.has("cod")).isTrue()
        Truth.assertThat(jsonError.getString("cod")).isEqualTo("401")
        Truth.assertThat(jsonError.getString("message")).isEqualTo("Invalid API key. Please see http://openweathermap.org/faq#error401 for more info.")

    }

    @Test
    fun searchForecastDaily_withKeywordIsWrongCity_returnAResponseCode404AndWeatherInfoResponseWithErrorMessage()
    = runBlocking {
        enqueueResponse("forecastapis/searchForecastDaily_wrong_city.json", responseCode = 404)
        val parameters = mutableMapOf<String, String>()
        parameters["q"] = "wrong_query"
        parameters["cnt"] = "7"
        parameters["appid"] = "60c6fbeb4b93ac653c492ba806fc346d"
        val response = forecastApi.searchForecastDaily()
        Truth.assertThat(response.code()).isEqualTo(404)

        val data = response.errorBody()?.string() ?: ""
        Truth.assertThat(data).isNotNull()
        val jsonError = JSONObject(data)
        Truth.assertThat(jsonError.has("cod")).isTrue()
        Truth.assertThat(jsonError.getString("cod")).isEqualTo("404")
        Truth.assertThat(jsonError.getString("message")).isEqualTo("city not found")

    }

    @Test
    fun searchForecastDaily_withKeywordIsValidCity_returnAResponseCode200AndWeatherInfoResponseWithAListOfWeatherInfoJson() =
        runBlocking{
        enqueueResponse("forecastapis/searchForecastDaily_success.json", responseCode = 200)
        val parameters = mutableMapOf<String, String>()
        parameters["q"] = "saigon"
        parameters["cnt"] = "7"
        parameters["appid"] = "60c6fbeb4b93ac653c492ba806fc346d"
        val response = forecastApi.searchForecastDaily()
        Truth.assertThat(response.code()).isEqualTo(200)
        val weatherInfoResponse =  response.body()
        Truth.assertThat(weatherInfoResponse).isNotNull()
        val list = weatherInfoResponse?.list
        Truth.assertThat(list).isNotNull()
        Truth.assertThat(list?.size).isEqualTo(7)
        val firstItem = list?.firstOrNull()
        Truth.assertThat(firstItem).isNotNull()
        Truth.assertThat(firstItem?.date).isEqualTo(1652155200)
        val tempJson = firstItem?.temp
        Truth.assertThat(tempJson).isNotNull()
        Truth.assertThat(tempJson?.min).isEqualTo(299.59f)
        Truth.assertThat(tempJson?.max).isEqualTo(307.04f)
        val listWeatherJson = firstItem?.weather
        Truth.assertThat(listWeatherJson).isNotNull()
        Truth.assertThat(listWeatherJson?.size).isEqualTo(1)
        val firstWeatherJson = listWeatherJson?.firstOrNull()
        Truth.assertThat(firstWeatherJson).isNotNull()
        Truth.assertThat(firstWeatherJson?.description).isEqualTo("moderate rain")
        Truth.assertThat(firstItem?.pressure).isEqualTo(1009)
        Truth.assertThat(firstItem?.humidity).isEqualTo(50)
    }

}