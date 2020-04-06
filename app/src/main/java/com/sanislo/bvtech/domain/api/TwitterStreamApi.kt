package com.sanislo.bvtech.domain.api

import kotlinx.coroutines.flow.Flow

interface TwitterStreamApi {
    fun stream(query: String): Flow<TwitterStreamApiResult>
}
