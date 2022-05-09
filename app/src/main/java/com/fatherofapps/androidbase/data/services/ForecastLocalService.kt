package com.fatherofapps.androidbase.data.services

import androidx.annotation.VisibleForTesting
import com.fatherofapps.androidbase.data.database.daos.CacheDao
import com.fatherofapps.androidbase.data.database.daos.KeywordDao
import com.fatherofapps.androidbase.data.database.entities.CacheEntity
import javax.inject.Inject

class ForecastLocalService @Inject constructor(
    private val cacheDao: CacheDao,
    private val keywordDao: KeywordDao
) {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun getCacheByQueryHash(queryHash: String): CacheEntity? {
        return cacheDao.getCacheByQuery(queryHash)
    }



}