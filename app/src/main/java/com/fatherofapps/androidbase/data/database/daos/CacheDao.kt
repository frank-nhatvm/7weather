package com.fatherofapps.androidbase.data.database.daos

import androidx.room.Dao
import androidx.room.*
import com.fatherofapps.androidbase.data.database.entities.CacheEntity


@Dao
interface CacheDao {

    @Query("SELECT * FROM cache WHERE query_hash == :queryHash ORDER BY datetime(created_at) ASC LIMIT 1")
    suspend fun getCacheByQuery(queryHash: String): CacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cacheEntity: CacheEntity)

    @Delete
    suspend fun delete(cacheEntity: CacheEntity)
}