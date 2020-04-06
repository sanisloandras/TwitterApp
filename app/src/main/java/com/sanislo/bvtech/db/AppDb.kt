package com.sanislo.bvtech.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FeedEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun feedDao(): FeedDao
}