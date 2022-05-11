package com.fatherofapps.androidbase.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fatherofapps.androidbase.base.viewmodel.BaseViewModel
import com.fatherofapps.androidbase.data.repositories.ForecastRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val forecastRepository: ForecastRepository) :
    BaseViewModel() {

    private var _openMainActivity = MutableLiveData<Boolean>()
    val openMainActivity: LiveData<Boolean>
        get() = _openMainActivity

    fun cleanup() {
        viewModelScope.launch(handler) {
            forecastRepository.cleanup()
            _openMainActivity.postValue(true)
        }

    }
}