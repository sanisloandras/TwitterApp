package com.sanislo.bvtech.domain

import java.util.*

data class FeedItemModel(val id: Long,
                         val userName: String,
                         val text: String,
                         val createdAt: Date
)