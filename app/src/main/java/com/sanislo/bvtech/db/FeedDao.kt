package com.sanislo.bvtech.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(feedEntity: FeedEntity): Long

    @Query("SELECT * FROM feedentity ORDER BY createdAt")
    fun feed(): LiveData<List<FeedEntity>>

    @Query("DELETE FROM feedentity WHERE id = :feedId")
    fun deleteById(feedId: Long)

    @Query("DELETE FROM feedentity")
    fun clearFeed()
}