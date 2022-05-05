package com.fatherofapps.androidbase.data.database

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.TimeUnit

abstract  class AppDatabaseTest {

    @Rule
    @JvmField
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    private  lateinit var  _db: AppDatabase
    val db: AppDatabase
    get() = _db

    @Before
    fun initDatabase(){
        _db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),AppDatabase::class.java).build()
    }

    @After
    fun closeDatabase(){
        countingTaskExecutorRule.drainTasks(10, TimeUnit.SECONDS)
        _db.close()
    }
}