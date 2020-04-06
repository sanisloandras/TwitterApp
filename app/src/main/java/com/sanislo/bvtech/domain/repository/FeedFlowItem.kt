package com.sanislo.bvtech.domain.repository

import com.sanislo.bvtech.domain.FeedItemModel

sealed class FeedFlowItem {
    class Error(val exception: Exception) : FeedFlowItem()
    data class Item(val feedItemModel: FeedItemModel): FeedFlowItem()
}