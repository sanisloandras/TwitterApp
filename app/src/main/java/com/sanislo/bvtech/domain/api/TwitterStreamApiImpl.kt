package com.sanislo.bvtech.domain.api

import android.util.Log
import com.sanislo.bvtech.domain.repository.FeedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import twitter4j.*

class TwitterStreamApiImpl(private val twitterStream: TwitterStream) : TwitterStreamApi {
    override fun stream(query: String): Flow<TwitterStreamApiResult> {
        return channelFlow {
            val twitterStream = twitterStream.addListener(object : StatusListener {
                override fun onTrackLimitationNotice(p0: Int) {
                    TODO("Not yet implemented")
                }

                override fun onStallWarning(p0: StallWarning?) {
                    TODO("Not yet implemented")
                }

                override fun onException(exception: Exception) {
                    launch { send(TwitterStreamApiResult.Error(exception)) }
                }

                override fun onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {
                    TODO("Not yet implemented")
                }

                override fun onStatus(status: Status) {
                    Log.d(FeedRepository.TAG, "onStatus ${status.id}")
                    launch(Dispatchers.IO) {
                        val result = status.run {
                            TwitterStreamApiResult.Item(
                                id,
                                user.name,
                                text,
                                createdAt
                            )
                        }
                        channel.send(result)
                    }
                }

                override fun onScrubGeo(p0: Long, p1: Long) {
                    TODO("Not yet implemented")
                }
            }).filter(FilterQuery(query))
            awaitClose {
                Log.d(TAG, "awaitClose for query $query")
                twitterStream.shutdown()
            }
        }
    }

    companion object {
        val TAG = TwitterStreamApi::class.java.simpleName
    }
}