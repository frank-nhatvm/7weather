package com.fatherofapps.androidbase.data.database.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import com.fatherofapps.androidbase.data.database.AppDatabaseTest
import com.fatherofapps.androidbase.data.database.entities.CacheEntity
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.threeten.bp.OffsetDateTime


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class CacheDaoTest : AppDatabaseTest(){

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Test
    fun getCacheByQuery_withNewQueryHash_returnNull() = runBlocking{
        val queryHash = "queryHash25254"
        val cacheEntity = db.cacheDao().getCacheByQuery(queryHash)
        Truth.assertThat(cacheEntity).isNull()
    }

    @Test
    fun getCacheByQuery_withExistQueryHash_returnCacheEntity() = runBlocking{
        val queryHash = "queryHash25325454"
        val today = OffsetDateTime.now()
        val cacheEntity = CacheEntity(
            pathFile = "/cache/789990.json",
            queryHash = queryHash,
            createdAt = today
        )
        db.cacheDao().insert(cacheEntity)

        val result = db.cacheDao().getCacheByQuery(queryHash)
        Truth.assertThat(result).isNotNull()
        Truth.assertThat(result?.queryHash).isEqualTo(queryHash)
    }

    @Test
    fun insert_newCacheWithNewQueryHash_returnSuccess() = runBlocking {
        val queryHash = "queryHash25325454"
        val today = OffsetDateTime.now()
        val cacheEntity = CacheEntity(
            pathFile = "/cache/789990.json",
            queryHash = queryHash,
            createdAt = today
        )
        db.cacheDao().insert(cacheEntity)
        val savedCacheEntity = db.cacheDao().getCacheByQuery(queryHash = queryHash)
        Truth.assertThat(savedCacheEntity).isNotNull()
        Truth.assertThat(savedCacheEntity?.pathFile).isEqualTo("/cache/789990.json")
        Truth.assertThat(savedCacheEntity?.queryHash).isEqualTo(queryHash)
    }

    @Test
    fun insert_ACacheWithExistQueryHash_replaceOldCache_returnSuccess() = runBlocking {
        val queryHash = "queryHash50295043298"
        val oneDayAgo = OffsetDateTime.now().plusDays(-1)
        val oldCacheEntity = CacheEntity(
            pathFile = "/cache/789990.json",
            queryHash = queryHash,
            createdAt = oneDayAgo
        )
        db.cacheDao().insert(oldCacheEntity)

        val today = OffsetDateTime.now()
        val newCacheEntity = CacheEntity(
            pathFile = "/cache/789991.json",
            queryHash = queryHash,
            createdAt = today
        )
        db.cacheDao().insert(newCacheEntity)

        val savedCacheEntity = db.cacheDao().getCacheByQuery(queryHash)
        Truth.assertThat(savedCacheEntity).isNotNull()
        Truth.assertThat(savedCacheEntity?.pathFile).isEqualTo(newCacheEntity.pathFile)
        Truth.assertThat(savedCacheEntity?.createdAt).isEqualTo(today)
        Truth.assertThat(savedCacheEntity?.queryHash).isEqualTo(queryHash)
    }

    @Test
    fun delete_AnExistCacheEntity_returnSuccess() = runBlocking {
        val queryHash = "queryHash25325454"
        val today = OffsetDateTime.now()
        val cacheEntity = CacheEntity(
            pathFile = "/cache/789990.json",
            queryHash = queryHash,
            createdAt = today
        )
        db.cacheDao().insert(cacheEntity)

        val savedCacheEntity = db.cacheDao().getCacheByQuery(queryHash)
        Truth.assertThat(savedCacheEntity).isNotNull()
        db.cacheDao().delete(savedCacheEntity!!)
        val result = db.cacheDao().getCacheByQuery(queryHash)
        Truth.assertThat(result).isNull()

    }

}