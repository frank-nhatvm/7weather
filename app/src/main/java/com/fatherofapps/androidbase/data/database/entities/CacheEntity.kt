package com.fatherofapps.androidbase.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache")
data class CacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "path_file")
    val pathFile: String,
    @ColumnInfo(name = "query_hash")
    val queryHash: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String
    )