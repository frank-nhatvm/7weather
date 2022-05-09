package com.fatherofapps.androidbase.data.database.daos

import androidx.room.*
import com.fatherofapps.androidbase.data.database.entities.KeywordEntity

@Dao
interface KeywordDao {
    @Query("SELECT * FROM keyword ORDER BY datetime(created_at) DESC LIMIT 10")
    suspend fun getTopTenRecentKeyword(): List<KeywordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keywordEntity: KeywordEntity)

    @Delete
    suspend fun delete(keywordEntity: KeywordEntity)
}