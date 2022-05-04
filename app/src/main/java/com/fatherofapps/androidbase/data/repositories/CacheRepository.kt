package com.fatherofapps.androidbase.data.repositories

import com.fatherofapps.androidbase.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher

class CacheRepository constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
}