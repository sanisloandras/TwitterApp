package com.sanislo.bvtech.ui

import android.util.Log.d
import androidx.lifecycle.*
import com.sanislo.bvtech.domain.Event
import com.sanislo.bvtech.domain.NetworkStateMonitor
import com.sanislo.bvtech.domain.repository.FeedFlowItem
import com.sanislo.bvtech.domain.repository.FeedRepository
import com.sanislo.bvtech.domain.usecase.FeedFlowUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import twitter4j.TwitterException

class MainViewModel constructor(
    networkStateMonitor: NetworkStateMonitor,
    private val feedFlowUseCase: FeedFlowUseCase,
    feedRepository: FeedRepository
) : ViewModel() {
    private val queryLiveData = MutableLiveData<String>()
    val networkState: LiveData<Boolean> = networkStateMonitor.distinctUntilChanged()
    val feedListList: LiveData<List<FeedListItem>> = feedRepository.feedList()
    private val _error = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = _error

    private var feedJob: Job? = null

    init {
        //todo this can be a converted to a livedata, to prevent observing the twitter stream when the app is in background
        feedJob = viewModelScope.launch(Dispatchers.IO) {
            queryLiveData.asFlow()
                .distinctUntilChanged()
                .filter {
                    it.length > 2
                }.flatMapLatest {
                    feedFlowUseCase.invoke(it)
                }.collect {
                    //only handle erros, as for feed, we observe the database
                    onlyHandleErrorItems(it)
                }
        }
    }

    fun setQuery(query: String) {
        d(TAG, "query $query")
        queryLiveData.value = query.trim()
    }

    private fun onlyHandleErrorItems(it: FeedFlowItem) {
        if (it !is FeedFlowItem.Error) return
        d(TAG, "error ${it.exception}")
        //if it's an Exception caused by no internet connection, we ignore, since it's handled by a LiveData
        if ((it.exception as? TwitterException)?.statusCode == -1) return
        _error.postValue(Event(it.exception))
    }

    override fun onCleared() {
        super.onCleared()
        d(TAG, "onCleared ${queryLiveData.value}")
        stopCurrentStream()
    }

    private fun stopCurrentStream() {
        feedJob?.cancel()
    }

    companion object {
        val TAG = MainViewModel::class.java.simpleName
    }
}