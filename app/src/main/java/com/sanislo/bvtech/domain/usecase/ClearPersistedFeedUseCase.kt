package com.sanislo.bvtech.domain.usecase

import com.sanislo.bvtech.db.FeedDao

class ClearPersistedFeedUseCase(private val feedDao: FeedDao) {
    fun invoke() {
        feedDao.clearFeed()
    }
}