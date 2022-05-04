package com.fatherofapps.androidbase.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keyword")
data class KeywordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val keyword: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String
)
