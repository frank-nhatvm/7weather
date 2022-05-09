package com.fatherofapps.androidbase.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fatherofapps.androidbase.data.database.converter.DatabaseConverter
import com.fatherofapps.androidbase.data.database.daos.CacheDao
import com.fatherofapps.androidbase.data.database.daos.KeywordDao
import com.fatherofapps.androidbase.data.database.entities.CacheEntity
import com.fatherofapps.androidbase.data.database.entities.KeywordEntity

@Database(entities = [CacheEntity::class, KeywordEntity::class],version = 1)
@TypeConverters(DatabaseConverter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun cacheDao(): CacheDao
    abstract fun keywordDao(): KeywordDao
}