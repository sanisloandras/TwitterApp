package com.sanislo.bvtech.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.sanislo.bvtech.db.FeedDao
import com.sanislo.bvtech.domain.FeedItemModel
import com.sanislo.bvtech.domain.api.TwitterStreamApi
import com.sanislo.bvtech.domain.api.TwitterStreamApiResult
import com.sanislo.bvtech.ui.FeedListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FeedRepository(
    private val twitterStreamApi: TwitterStreamApi,
    private val feedDao: FeedDao) {

    fun feedFlow(query: String): Flow<FeedFlowItem> {
        return twitterStreamApi.stream(query).map {
            when (it) {
                is TwitterStreamApiResult.Item -> {
                    val feedItemModel = it.run {
                        FeedItemModel(
                            id,
                            userName,
                            text,
                            createdAt
                        )
                    }
                    FeedFlowItem.Item(feedItemModel)
                }
                is TwitterStreamApiResult.Error -> FeedFlowItem.Error(it.exception)
            }
        }
    }

    fun feedList(): LiveData<List<FeedListItem>> {
        return feedDao.feed().map {
            it.map { feedEntity ->
                feedEntity.run {
                    FeedListItem(id, userName, text)
                }
            }
        }
    }

    companion object {
        val TAG = FeedRepository::class.java.simpleName
    }
}