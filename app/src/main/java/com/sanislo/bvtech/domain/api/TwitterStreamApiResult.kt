package com.sanislo.bvtech.domain.api

import java.util.*

sealed class TwitterStreamApiResult {
    class Error(val exception: Exception) : TwitterStreamApiResult()
    data class Item(val id: Long,
                    val userName: String,
                    val text: String,
                    val createdAt: Date): TwitterStreamApiResult()
}