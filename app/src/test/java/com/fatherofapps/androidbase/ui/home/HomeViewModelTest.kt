package com.fatherofapps.androidbase.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fatherofapps.androidbase.MainCoroutineRule
import com.fatherofapps.androidbase.base.network.BaseNetworkException
import com.fatherofapps.androidbase.data.models.WeatherInfo
import com.fatherofapps.androidbase.data.repositories.ForecastRepository
import com.fatherofapps.androidbase.getOrAwaitValue
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.threeten.bp.OffsetDateTime
import java.io.File

@ExperimentalCoroutinesApi
class HomeViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var forecastRepository: ForecastRepository
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun init() {
        forecastRepository = Mockito.mock(ForecastRepository::class.java)
        homeViewModel = HomeViewModel(forecastRepository)
    }


    @Test
    fun searchAction_loading() = mainCoroutineRule.dispatcher.runBlockingTest {
        val query = "ha noi"
        val day = 7
        val cacheFolder = File("caches/99900.json")
        val queryDate = OffsetDateTime.now()

        mainCoroutineRule.dispatcher.pauseDispatcher()
        Mockito.`when`(
            forecastRepository.search(
                query = query,
                days = day,
                cacheFolder = cacheFolder,
                queryDate = queryDate
            )
        ).thenReturn(fakeListWeatherInfo())
        homeViewModel.searchAction(query = query, cacheFolder = cacheFolder, queryDate = queryDate)
        Truth.assertThat(homeViewModel.isLoading.getOrAwaitValue().getContentIfNotHandled())
            .isTrue()
        mainCoroutineRule.dispatcher.resumeDispatcher()
        Truth.assertThat(homeViewModel.isLoading.getOrAwaitValue().getContentIfNotHandled())
            .isFalse()
    }

    @Test
    fun searchAction_callToSearchOfForecastRepository() =
        mainCoroutineRule.dispatcher.runBlockingTest {
            val query = "ha noi"
            val day = 7
            val cacheFolder = File("caches/99900.json")
            val queryDate = OffsetDateTime.now()


            Mockito.`when`(
                forecastRepository.search(
                    query = query,
                    days = day,
                    cacheFolder = cacheFolder,
                    queryDate = queryDate
                )
            ).thenReturn(fakeListWeatherInfo())
            homeViewModel.searchAction(
                query = query,
                cacheFolder = cacheFolder,
                queryDate = queryDate
            )
            Mockito.verify(forecastRepository, Mockito.times(1))
                .search(query = query, days = day, cacheFolder = cacheFolder, queryDate = queryDate)
        }

    @Test
    fun searchAction_fail_baseNetworkExceptionPostAValue() = mainCoroutineRule.dispatcher.runBlockingTest {
        val query = "ha noi"
        val day = 7
        val cacheFolder = File("caches/99900.json")
        val queryDate = OffsetDateTime.now()

        val errorMessage = "city not found"
        val baseNetworkException = BaseNetworkException(responseCode = 404)
        baseNetworkException.mainMessage = errorMessage

        Mockito.`when`(
            forecastRepository.search(
                query = query,
                days = day,
                cacheFolder = cacheFolder,
                queryDate = queryDate
            )
        ).thenAnswer { throw baseNetworkException }

        homeViewModel.searchAction(
            query = query,
            cacheFolder = cacheFolder,
            queryDate = queryDate
        )
        val exception = homeViewModel.baseNetworkException.getOrAwaitValue()
            .getContentIfNotHandled()
        Truth.assertThat(exception).isNotNull()

        Truth.assertThat(exception?.mainMessage).isEqualTo(errorMessage)
    }

    @Test
    fun searchAction_success_listWeatherInfoPostAValue() = mainCoroutineRule.dispatcher.runBlockingTest {
        val query = "ha noi"
        val day = 7
        val cacheFolder = File("caches/99900.json")
        val queryDate = OffsetDateTime.now()

       val listWeatherInfo = fakeListWeatherInfo()
        val firstItem = listWeatherInfo.firstOrNull()
        Mockito.`when`(
            forecastRepository.search(
                query = query,
                days = day,
                cacheFolder = cacheFolder,
                queryDate = queryDate
            )
        ).thenReturn(listWeatherInfo)

        homeViewModel.searchAction(
            query = query,
            cacheFolder = cacheFolder,
            queryDate = queryDate
        )
       val result = homeViewModel.listWeatherInfo.getOrAwaitValue()
        Truth.assertThat(result).isNotNull()
        Truth.assertThat(result?.size).isEqualTo(1)
        val firstItemResult = result?.firstOrNull()
        Truth.assertThat(firstItemResult?.date).isEqualTo(firstItem?.date)
        Truth.assertThat(firstItemResult?.averageTemp).isEqualTo(firstItem?.averageTemp)
        Truth.assertThat(firstItemResult?.pressure).isEqualTo(firstItem?.pressure)
        Truth.assertThat(firstItemResult?.humidity).isEqualTo(firstItem?.humidity)
        Truth.assertThat(firstItemResult?.description).isEqualTo(firstItem?.description)
    }



    private fun fakeListWeatherInfo(): List<WeatherInfo> {
        val weatherInfo = WeatherInfo(
            date = 99888999,
            averageTemp = 23,
            pressure = 233,
            humidity = 12,
            description = "no rain"
        )
        return listOf(weatherInfo)
    }

}