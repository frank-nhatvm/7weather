package com.fatherofapps.androidbase.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer")
data class CacheEntity (@PrimaryKey val id: Int)