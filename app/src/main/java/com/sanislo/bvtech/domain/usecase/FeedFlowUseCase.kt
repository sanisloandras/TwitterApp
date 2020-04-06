package com.sanislo.bvtech.domain.usecase

import android.content.Context
import androidx.work.WorkManager
import com.sanislo.bvtech.domain.repository.FeedFlowItem
import com.sanislo.bvtech.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class FeedFlowUseCase(private val applicationContext: Context,
                      private val feedRepository: FeedRepository,
                      private val clearPersistedFeedUseCase: ClearPersistedFeedUseCase,
                      private val scheduleFeedItemRemovalUseCase: ScheduleFeedItemRemovalUseCase,
                      private val persistFeedItemUseCase: PersistFeedItemUseCase
) {
    fun invoke(query: String): Flow<FeedFlowItem> {
        return feedRepository.feedFlow(query)
            .onStart {
                WorkManager.getInstance(applicationContext).cancelAllWork()
                clearPersistedFeedUseCase.invoke()
            }
            .onEach {
                if (it is FeedFlowItem.Item) {
                    persistFeedItemUseCase.invoke(it.feedItemModel)
                    scheduleFeedItemRemovalUseCase.invoke(it.feedItemModel.id)
                }
            }
    }
}