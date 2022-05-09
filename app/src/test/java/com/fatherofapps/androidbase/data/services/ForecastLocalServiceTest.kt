package com.fatherofapps.androidbase.data.services

import com.fatherofapps.androidbase.MainCoroutineRule
import com.fatherofapps.androidbase.data.database.daos.CacheDao
import com.fatherofapps.androidbase.data.database.daos.KeywordDao
import com.fatherofapps.androidbase.data.database.entities.CacheEntity
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.threeten.bp.OffsetDateTime


@ExperimentalCoroutinesApi
class ForecastLocalServiceTest {

    @get:Rule
    var mainCoroutinesApi = MainCoroutineRule()

    private lateinit var cacheDao: CacheDao
    private lateinit var keywordDao: KeywordDao
    private lateinit var forecastLocalService: ForecastLocalService

    @Before
    fun init(){
        cacheDao = Mockito.mock(CacheDao::class.java)
        keywordDao = Mockito.mock(KeywordDao::class.java)
        forecastLocalService = ForecastLocalService(cacheDao = cacheDao,keywordDao = keywordDao)
    }

    @Test
    fun getCacheByQueryHash_withNewQuery_returnNull() = mainCoroutinesApi.dispatcher.runBlockingTest{
        val queryHash = "queryHash24343"
        Mockito.`when`(cacheDao.getCacheByQuery(queryHash)).thenReturn(null)
        val result =  forecastLocalService.getCacheByQueryHash(queryHash = queryHash)
        Truth.assertThat(result).isNull()
    }

    @Test
    fun getCacheByQueryHash_withExistQuery_returnACacheEntity() = mainCoroutinesApi.dispatcher.runBlockingTest {
        val queryHash = "queryHash24343"
        val createdAt = OffsetDateTime.now()
        val cacheEntity = CacheEntity(id = 1, pathFile = "/caches/89999.json",queryHash = queryHash, createdAt = createdAt)
        Mockito.`when`(cacheDao.getCacheByQuery(queryHash)).thenReturn(cacheEntity)
        val result =  forecastLocalService.getCacheByQueryHash(queryHash = queryHash)
        Truth.assertThat(result).isNotNull()
        Truth.assertThat(result?.id).isEqualTo(cacheEntity.id)
        Truth.assertThat(result?.queryHash).isEqualTo(cacheEntity.queryHash)
        Truth.assertThat(result?.pathFile).isEqualTo(cacheEntity.pathFile)
    }



}