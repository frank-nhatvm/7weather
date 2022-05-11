package com.fatherofapps.androidbase.data.services

import androidx.annotation.VisibleForTesting
import com.fatherofapps.androidbase.common.CacheFileUtils
import com.fatherofapps.androidbase.common.isToday
import com.fatherofapps.androidbase.data.database.daos.CacheDao
import com.fatherofapps.androidbase.data.database.daos.KeywordDao
import com.fatherofapps.androidbase.data.database.entities.CacheEntity
import com.fatherofapps.androidbase.data.database.entities.KeywordEntity
import com.fatherofapps.androidbase.data.models.WeatherInfo
import org.threeten.bp.OffsetDateTime
import java.io.File
import javax.inject.Inject

class ForecastLocalService @Inject constructor(
    private val cacheDao: CacheDao,
    private val keywordDao: KeywordDao,
    private val cacheFileUtils: CacheFileUtils = CacheFileUtils()
) {

    suspend fun getCacheData(queryHash: String): List<WeatherInfo>? {
        val cache = getCacheByQueryHash(queryHash)
        cache?.pathFile?.let { filePath ->
          return  cacheFileUtils.getCacheDataFromFile(filePath)
        }

        return null
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun getCacheByQueryHash(queryHash: String): CacheEntity? {
        return cacheDao.getCacheByQuery(queryHash)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun saveKeyword(query: String,createdAt: OffsetDateTime) {
        val keywordEntity = KeywordEntity(keyword = query, createdAt = createdAt)
        keywordDao.insert(keywordEntity)
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun saveCache(queryHash: String, filePath: String,createdAt: OffsetDateTime) {
        val cacheEntity =
            CacheEntity(pathFile = filePath, queryHash = queryHash, createdAt = createdAt)
        cacheDao.insert(cacheEntity)
    }

    suspend fun saveData(
        queryHash: String,
        query: String,
        list: List<WeatherInfo>,
        cacheFolder: File,
        createdAt: OffsetDateTime
    ) {
        saveKeyword(query,createdAt)
        val filePath = cacheFileUtils.writeDataToFile(list, cacheFolder)
        saveCache(queryHash = queryHash, filePath = filePath,createdAt)
    }

    suspend fun cleanup(){
        val allCaches = cacheDao.getAllCache()
        allCaches.forEach { cache ->
            if(cache.createdAt?.isToday() == false){
                cacheFileUtils.deleteCacheFile(cache.pathFile)
                cacheDao.delete(cache)

            }
        }
    }

}