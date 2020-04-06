package com.sanislo.bvtech.db

import androidx.room.Entity
import androidx.room.TypeConverters
import java.util.*

@Entity(primaryKeys = ["id"])
@TypeConverters(DateConverter::class)
data class FeedEntity(
    val id: Long,
    val userName: String,
    val text: String,
    val createdAt: Date
)