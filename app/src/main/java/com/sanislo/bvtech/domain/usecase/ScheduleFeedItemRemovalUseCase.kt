package com.sanislo.bvtech.domain.usecase

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sanislo.bvtech.domain.Constants
import com.sanislo.bvtech.domain.RemovePersistedFeedItemWorker
import java.util.concurrent.TimeUnit

class ScheduleFeedItemRemovalUseCase(private val applicationContext: Context,
                                     private val duration: Long,
                                     private val timeUnit: TimeUnit) {
    fun invoke(id: Long) {
        val inputData = workDataOf(Constants.KEY_FEED_ID to id)
        val uploadWorkRequest = OneTimeWorkRequestBuilder<RemovePersistedFeedItemWorker>()
            .setInputData(inputData)
            .setInitialDelay(duration, timeUnit)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(uploadWorkRequest)
    }
}