package com.sanislo.bvtech.domain.usecase

import android.util.Log.d
import com.sanislo.bvtech.db.FeedDao
import com.sanislo.bvtech.db.FeedEntity
import com.sanislo.bvtech.domain.FeedItemModel

class PersistFeedItemUseCase(private val feedDao: FeedDao) {
    fun invoke(feedItemModel: FeedItemModel) {
        val feedEntity = feedItemModel.run { FeedEntity(id, userName, text, createdAt) }
        val inserted = feedDao.insert(feedEntity)
        d(TAG, "inserted $inserted")
    }

    companion object {
        const val TAG = "PersistFeedItemUseCase"
    }
}