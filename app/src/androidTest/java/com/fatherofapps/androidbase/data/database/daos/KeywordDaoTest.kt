package com.fatherofapps.androidbase.data.database.daos

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fatherofapps.androidbase.data.database.AppDatabaseTest
import com.fatherofapps.androidbase.data.database.entities.KeywordEntity
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.OffsetDateTime

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class KeywordDaoTest : AppDatabaseTest() {

    @Test
    fun insert_newKeywordEntity_returnSuccess() = runBlocking {
        val keyword = "ha noi"
        val today = OffsetDateTime.now()
        val keywordEntity = KeywordEntity(
            keyword = keyword,
            createdAt = today
        )
        db.keywordDao().insert(keywordEntity)
        val result = db.keywordDao().getTopTenRecentKeyword()
        Truth.assertThat(result.size).isEqualTo(1)
        val savedKeywordEntity = result.first()
        Truth.assertThat(savedKeywordEntity).isNotNull()
        Truth.assertThat(savedKeywordEntity.keyword).isEqualTo(keyword)
    }

    @Test
    fun insert_keywordEntityWithExistKeyword_replaceOldKeywordEntity_returnSuccess() = runBlocking {
        val keyword = "ha noi"
        val oneDayAgo = OffsetDateTime.now().plusDays(-1)
        val oneDayAgoKeywordEntity = KeywordEntity(
            keyword = keyword,
            createdAt = oneDayAgo
        )
        db.keywordDao().insert(oneDayAgoKeywordEntity)

        val today = OffsetDateTime.now()
        val newKeywordEntity = KeywordEntity(
            keyword = keyword,
            createdAt = today
        )
        db.keywordDao().insert(newKeywordEntity)
        val result = db.keywordDao().getTopTenRecentKeyword()
        Truth.assertThat(result.size).isEqualTo(1)
        val savedKeywordEntity = result.first()
        Truth.assertThat(savedKeywordEntity).isNotNull()
        Truth.assertThat(savedKeywordEntity.keyword).isEqualTo(keyword)
        Truth.assertThat(savedKeywordEntity.createdAt).isEqualTo(today)
    }

    @Test
    fun delete_AnExistKeywordEntity_returnSuccess() = runBlocking {
        val keyword = "ha noi"
        val today = OffsetDateTime.now()
        val keywordEntity = KeywordEntity(
            keyword = keyword,
            createdAt = today
        )
        db.keywordDao().insert(keywordEntity)

        val result = db.keywordDao().getTopTenRecentKeyword()
        Truth.assertThat(result.size).isEqualTo(1)
        val savedKeywordEntity = result.first()
        db.keywordDao().delete(savedKeywordEntity)
        val resultAfterDelete = db.keywordDao().getTopTenRecentKeyword()
        Truth.assertThat(resultAfterDelete.size).isEqualTo(0)
    }

    @Test
    fun getTopTenRecentKeyword_haveNotSearchedAnything_returnEmptyList() = runBlocking {
        val result = db.keywordDao().getTopTenRecentKeyword()
        Truth.assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun getTopTenRecentKeyword_searched_returnAListOfKeywordEntityInOrderByDatetime() = runBlocking {
        val hanoiKeyword = "ha noi"
        val tenHoursAgo = OffsetDateTime.now().plusHours(-10)
        val hanoiKeywordEntity = KeywordEntity(
            keyword = hanoiKeyword,
            createdAt = tenHoursAgo
        )
        db.keywordDao().insert(hanoiKeywordEntity)
        val thaiBinhKeyword = "thai binh"
        val twoHoursAgo = OffsetDateTime.now().plusHours(-2)
        val thaiBinhKeywordEnity = KeywordEntity(
            keyword = thaiBinhKeyword,
            createdAt = twoHoursAgo
        )
        db.keywordDao().insert(thaiBinhKeywordEnity)
        val nhaTrangKeyword = "nha trang"
        val rightNow = OffsetDateTime.now()
        val nhaTrangKeywordEntity = KeywordEntity(
            keyword = nhaTrangKeyword,
            createdAt = rightNow
        )
        db.keywordDao().insert(nhaTrangKeywordEntity)

        val listKeywords = db.keywordDao().getTopTenRecentKeyword()
        Truth.assertThat(listKeywords.size).isEqualTo(3)
        val latestKeyword = listKeywords.first()
        Truth.assertThat(latestKeyword).isNotNull()
        Truth.assertThat(latestKeyword.keyword).isEqualTo(nhaTrangKeyword)
        Truth.assertThat(latestKeyword.createdAt).isEqualTo(rightNow)
    }

    @Test
    fun getTopTenRecentKeyword_searchedMoreThanTenTime_returnAListTenKeyword() = runBlocking {
        val rightNow = OffsetDateTime.now()
        for(index in 0..19){
            val keyword = "keyword $index"
            val minuteAgo =  0-index
            val createdAt = rightNow.plusMinutes(minuteAgo.toLong())
            val keywordEntity = KeywordEntity(
                keyword = keyword,
                createdAt = createdAt
            )
            db.keywordDao().insert(keywordEntity)
        }
        val listKeywords = db.keywordDao().getTopTenRecentKeyword()
        Truth.assertThat(listKeywords.size).isEqualTo(10)
    }


}