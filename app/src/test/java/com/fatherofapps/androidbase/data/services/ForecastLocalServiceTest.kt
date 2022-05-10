package com.fatherofapps.androidbase.data.services

import com.fatherofapps.androidbase.MainCoroutineRule
import com.fatherofapps.androidbase.common.CacheFileUtils
import com.fatherofapps.androidbase.data.database.daos.CacheDao
import com.fatherofapps.androidbase.data.database.daos.KeywordDao
import com.fatherofapps.androidbase.data.database.entities.CacheEntity
import com.fatherofapps.androidbase.data.database.entities.KeywordEntity
import com.fatherofapps.androidbase.data.models.WeatherInfo
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.threeten.bp.OffsetDateTime
import java.io.File


@ExperimentalCoroutinesApi
class ForecastLocalServiceTest {

    @get:Rule
    var mainCoroutinesApi = MainCoroutineRule()

    private lateinit var cacheDao: CacheDao
    private lateinit var keywordDao: KeywordDao
    private lateinit var cacheFileUtils: CacheFileUtils
    private lateinit var forecastLocalService: ForecastLocalService


    @Before
    fun init() {
        cacheDao = Mockito.mock(CacheDao::class.java)
        keywordDao = Mockito.mock(KeywordDao::class.java)
        cacheFileUtils = Mockito.mock(CacheFileUtils::class.java)
        forecastLocalService = ForecastLocalService(
            cacheDao = cacheDao,
            keywordDao = keywordDao,
            cacheFileUtils = cacheFileUtils
        )
    }

    @Test
    fun getCacheByQueryHash_callToGetCacheByQueryOfCacheDao() =
        mainCoroutinesApi.dispatcher.runBlockingTest {
            val queryHash = "queryHash24343"
            forecastLocalService.getCacheByQueryHash(queryHash = queryHash)
            Mockito.verify(cacheDao, Mockito.times(1)).getCacheByQuery(queryHash)
        }

    @Test
    fun getCacheData_callToGetCacheByQueryOfCacheDao() =
        mainCoroutinesApi.dispatcher.runBlockingTest {
            val queryHash = "ha noi"
            forecastLocalService.getCacheData(queryHash)
            Mockito.verify(cacheDao, Mockito.times(1)).getCacheByQuery(queryHash)
        }

    @Test
    fun getCacheData_withNewQuery_donNotCallToGetCacheDataFromFileOfCacheFileUtils_returnNull() =
        mainCoroutinesApi.dispatcher.runBlockingTest {
            val queryHash = "ha noi"
            val filePath = "caches/990008.json"

            Mockito.`when`(cacheDao.getCacheByQuery(queryHash = queryHash)).thenReturn(null)
            val listWeatherInfo = forecastLocalService.getCacheData(queryHash)
            Mockito.verify(cacheFileUtils, Mockito.times(0)).getCacheDataFromFile(filePath)
            Truth.assertThat(listWeatherInfo).isNull()
        }

    @Test
    fun getCacheData_withExistQuery_callToGetCacheDataFromFileOfCacheFileUtils_returnAListWeatherInfo() =
        mainCoroutinesApi.dispatcher.runBlockingTest {
            val queryHash = "ha noi"
            val filePath = "caches/990008.json"
            val createdAt = OffsetDateTime.now()
            val cacheEntity = CacheEntity(
                id = 1,
                pathFile = filePath,
                queryHash = queryHash,
                createdAt = createdAt
            )
            Mockito.`when`(cacheDao.getCacheByQuery(queryHash)).thenReturn(cacheEntity)
            val fakeWeatherInfo = WeatherInfo(
                date = 99008090,
                averageTemp = 20,
                pressure = 123,
                humidity = 12,
                description = "rain"
            )
            val fakeListWeatherInfo = listOf(fakeWeatherInfo)
            Mockito.`when`(cacheFileUtils.getCacheDataFromFile(filePath))
                .thenReturn(fakeListWeatherInfo)

            val listWeatherInfo = forecastLocalService.getCacheData(queryHash)
            Truth.assertThat(listWeatherInfo).isNotNull()
            Truth.assertThat(listWeatherInfo?.size).isEqualTo(1)
            val firstItem = listWeatherInfo?.firstOrNull()
            Truth.assertThat(firstItem).isNotNull()
            Truth.assertThat(firstItem?.date).isEqualTo(fakeWeatherInfo.date)
            Truth.assertThat(firstItem?.averageTemp).isEqualTo(fakeWeatherInfo.averageTemp)
            Truth.assertThat(firstItem?.pressure).isEqualTo(fakeWeatherInfo.pressure)
            Truth.assertThat(firstItem?.humidity).isEqualTo(fakeWeatherInfo.humidity)
            Truth.assertThat(firstItem?.description).isEqualTo(fakeWeatherInfo.description)
        }

    @Test
    fun saveKeyword_callToInsertOfKeywordDao() = mainCoroutinesApi.dispatcher.runBlockingTest {
        val query = "ha noi"
        val createdAt = OffsetDateTime.now()
        forecastLocalService.saveKeyword(query, createdAt)
        val keywordEntity = KeywordEntity(keyword = query, createdAt = createdAt)
        Mockito.verify(keywordDao, Mockito.times(1)).insert(keywordEntity)
    }

    @Test
    fun saveCache_callToInsertOfCacheDao() = mainCoroutinesApi.dispatcher.runBlockingTest {
        val queryHash = "queryHash93r93"
        val filePath = "caches/09809.json"
        val createdAt = OffsetDateTime.now()
        forecastLocalService.saveCache(queryHash, filePath, createdAt)
        val cacheEntity =
            CacheEntity(pathFile = filePath, queryHash = queryHash, createdAt = createdAt)
        Mockito.verify(cacheDao, Mockito.times(1)).insert(cacheEntity)
    }

    @Test
    fun saveData_callToInsertOfCacheDao_callToInsertOfKeywordDao_callToWriteDataToFileOfCacheFileUtils() =
        mainCoroutinesApi.dispatcher.runBlockingTest {
            val queryHash = "queryHash999"
            val query = "ha noi"
            val listWeatherInfo = listOf<WeatherInfo>()
            val cacheFolder = File("data/user/0")
            val createdAt = OffsetDateTime.now()
            val filePath = "caches/8899.json"
            Mockito.`when`(cacheFileUtils.writeDataToFile(listWeatherInfo,cacheFolder)).thenReturn(filePath)
            forecastLocalService.saveData(queryHash = queryHash,query = query,list = listWeatherInfo,cacheFolder=cacheFolder,createdAt=createdAt)

            val cacheEntity =
                CacheEntity(pathFile = filePath, queryHash = queryHash, createdAt = createdAt)
            Mockito.verify(cacheDao, Mockito.times(1)).insert(cacheEntity)

            val keywordEntity = KeywordEntity(keyword = query, createdAt = createdAt)
            Mockito.verify(keywordDao, Mockito.times(1)).insert(keywordEntity)

            Mockito.verify(cacheFileUtils,Mockito.times(1)).writeDataToFile(listWeatherInfo,cacheFolder)
        }

}