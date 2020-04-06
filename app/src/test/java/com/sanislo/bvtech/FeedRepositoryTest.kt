package com.sanislo.bvtech

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sanislo.bvtech.db.FeedDao
import com.sanislo.bvtech.db.FeedEntity
import com.sanislo.bvtech.domain.FeedItemModel
import com.sanislo.bvtech.domain.api.TwitterStreamApi
import com.sanislo.bvtech.domain.api.TwitterStreamApiResult
import com.sanislo.bvtech.domain.repository.FeedFlowItem
import com.sanislo.bvtech.domain.repository.FeedRepository
import com.sanislo.bvtech.ui.FeedListItem
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*

@RunWith(JUnit4::class)
class FeedRepositoryTest {
    private lateinit var feedRepository: FeedRepository
    private val twitterStreamApi = mock(TwitterStreamApi::class.java)
    private val feedDao = mock(FeedDao::class.java)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        feedRepository = FeedRepository(twitterStreamApi, feedDao)
    }

    @Test
    fun testFeed() {
        val date = Date()
        val thenReturn = MutableLiveData<List<FeedEntity>>(listOf(
            FeedEntity(1, "userName", "text", date)
        ))
        `when`(feedDao.feed()).thenReturn(thenReturn)
        val feedList = feedRepository.feedList()
        val observer = mock<Observer<List<FeedListItem>>>()
        feedList.observeForever(observer)
        verify(observer).onChanged(listOf(FeedListItem(1, "userName", "text")))
    }

    @Test
    fun testFeedFlow() {
        val date = Date()
        val twitterStreamApiResult = TwitterStreamApiResult.Item(1, "mockUserName", "mockText", date)
        `when`(twitterStreamApi.stream(ArgumentMatchers.anyString())).thenReturn(flowOf(twitterStreamApiResult))
        val feedFlowData = mutableListOf<FeedFlowItem>()
        val feedFlow = feedRepository.feedFlow("mockQuery")
        runBlocking {
            feedFlow.collect {
                feedFlowData.add(it)
            }
        }
        val feedFlowItem = FeedFlowItem.Item(FeedItemModel(1, "mockUserName", "mockText", date))
        assert(feedFlowData.isNotEmpty()) { "should not be empty" }
        assertEquals(feedFlowItem, feedFlowData.first())
    }

    @Test
    fun testFeedFlowError() {
        val twitterStreamApiResult = TwitterStreamApiResult.Error(Exception("mockException"))
        `when`(twitterStreamApi.stream(ArgumentMatchers.anyString())).thenReturn(flowOf(twitterStreamApiResult))
        val feedFlowData = mutableListOf<FeedFlowItem>()
        val feedFlow = feedRepository.feedFlow("mockQuery")
        runBlocking {
            feedFlow.collect {
                feedFlowData.add(it)
            }
        }
        assert(feedFlowData.isNotEmpty()) { "should not be empty" }
        assertTrue(feedFlowData.first() is FeedFlowItem.Error)
    }

    inline fun <reified T> mock(): T = Mockito.mock(T::class.java)
}