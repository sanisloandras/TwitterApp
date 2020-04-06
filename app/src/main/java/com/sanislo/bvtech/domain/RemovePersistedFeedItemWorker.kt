package com.sanislo.bvtech.domain

import android.content.Context
import android.util.Log.d
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sanislo.bvtech.db.FeedDao
import com.sanislo.bvtech.domain.Constants.Companion.KEY_FEED_ID
import org.koin.core.KoinComponent
import org.koin.core.inject

class RemovePersistedFeedItemWorker(context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams), KoinComponent {

    private val feedDao: FeedDao by inject()

    override fun doWork(): Result {
        val feedId = inputData.getLong(KEY_FEED_ID, -1L)
        d(TAG, "doWork $feedId")
        if (feedId == -1L) return Result.failure()
        feedDao.deleteById(feedId)
        return Result.success()
    }

    companion object {
        const val TAG = "RemoveFeedItemWorker"
    }
}