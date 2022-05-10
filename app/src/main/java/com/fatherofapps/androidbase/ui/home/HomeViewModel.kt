package com.fatherofapps.androidbase.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fatherofapps.androidbase.base.viewmodel.BaseViewModel
import com.fatherofapps.androidbase.data.models.WeatherInfo
import com.fatherofapps.androidbase.data.repositories.ForecastRepository
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import java.io.File
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val forecastRepository: ForecastRepository) :
    BaseViewModel() {

    private var _listWeatherInfo = MutableLiveData<List<WeatherInfo>?>()
    val listWeatherInfo: LiveData<List<WeatherInfo>?>
        get() = _listWeatherInfo

    fun searchAction(query: String,days: Int = 7, cacheFolder: File,queryDate: OffsetDateTime) {
        showLoading(true)
        parentJob = viewModelScope.launch(handler) {
            val result =
                forecastRepository.search(query = query, days = days, cacheFolder = cacheFolder, queryDate = queryDate)
            _listWeatherInfo.postValue(result)
        }
        registerJobFinish()

    }
}