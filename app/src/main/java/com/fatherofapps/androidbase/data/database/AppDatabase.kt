package com.fatherofapps.androidbase.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fatherofapps.androidbase.data.database.daos.CacheDao
import com.fatherofapps.androidbase.data.database.entities.CacheEntity

@Database(entities = [CacheEntity::class],version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun customerDao(): CacheDao
}