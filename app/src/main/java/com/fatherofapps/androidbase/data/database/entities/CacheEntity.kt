package com.fatherofapps.androidbase.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "cache", indices = [Index(value = ["query_hash"], unique = true)])
data class CacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "path_file")
    val pathFile: String,
    @ColumnInfo(name = "query_hash")
    val queryHash: String,
    @ColumnInfo(name = "created_at")
    val createdAt: OffsetDateTime? = null
    )