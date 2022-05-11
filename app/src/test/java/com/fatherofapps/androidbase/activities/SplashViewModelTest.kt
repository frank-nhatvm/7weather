package com.fatherofapps.androidbase.activities

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fatherofapps.androidbase.MainCoroutineRule
import com.fatherofapps.androidbase.data.repositories.ForecastRepository
import com.fatherofapps.androidbase.getOrAwaitValue
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class SplashViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var forecastRepository: ForecastRepository
    private lateinit var splashViewModel: SplashViewModel

    @Before
    fun init() {
        forecastRepository = Mockito.mock(ForecastRepository::class.java)
        splashViewModel = SplashViewModel(forecastRepository = forecastRepository)
    }

    @Test
    fun cleanup_callToCleanupOfForecastRepository() =
        mainCoroutineRule.dispatcher.runBlockingTest {
            splashViewModel.cleanup()
            Mockito.verify(forecastRepository, Mockito.times(1)).cleanup()
        }

    @Test
    fun cleanup_openMainActivityPosTrue() = mainCoroutineRule.dispatcher.runBlockingTest {
        splashViewModel.cleanup()
        val result = splashViewModel.openMainActivity.getOrAwaitValue()
        Truth.assertThat(result).isTrue()
    }

}