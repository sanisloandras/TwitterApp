package com.sanislo.bvtech.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.sanislo.bvtech.db.AppDb
import com.sanislo.bvtech.domain.NetworkStateMonitor
import com.sanislo.bvtech.domain.api.TwitterStreamApi
import com.sanislo.bvtech.domain.api.TwitterStreamApiImpl
import com.sanislo.bvtech.domain.repository.FeedRepository
import com.sanislo.bvtech.domain.usecase.ClearPersistedFeedUseCase
import com.sanislo.bvtech.domain.usecase.FeedFlowUseCase
import com.sanislo.bvtech.domain.usecase.PersistFeedItemUseCase
import com.sanislo.bvtech.domain.usecase.ScheduleFeedItemRemovalUseCase
import com.sanislo.bvtech.ui.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import twitter4j.TwitterStreamFactory
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationBuilder
import java.util.concurrent.TimeUnit

val appModule = module {
    factory { ConfigurationBuilder()
        .setDebugEnabled(true)
        .setOAuthConsumerKey(CONSUMER_KEY)
        .setOAuthConsumerSecret(CONSUMER_SECRET)
        .setOAuthAccessToken(ACCESS_TOKEN)
        .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET)
        .setOAuth2AccessToken(TOKEN).build() }
    factory { TwitterStreamFactory(get<Configuration>()).instance }
    factory { Room
        .databaseBuilder(androidApplication(), AppDb::class.java, "appdb.db")
        .fallbackToDestructiveMigration()
        .build()
    }
    single { get<AppDb>().feedDao() }
    factory<TwitterStreamApi> { TwitterStreamApiImpl(get()) }
    factory { FeedRepository(get(), get()) }
    factory { PersistFeedItemUseCase(get()) }
    factory { ClearPersistedFeedUseCase(get()) }
    factory { ScheduleFeedItemRemovalUseCase(androidApplication(), 15L, TimeUnit.SECONDS) }
    factory { FeedFlowUseCase(get(), get(), get(), get(), get()) }
    factory { NetworkStateMonitor(androidApplication().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) }
}

fun getModules(): List<Module> {
    return appModule + mainModule
}

val mainModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
}

const val TOKEN = "AAAAAAAAAAAAAAAAAAAAAHptDQEAAAAAmC4OvNQuk2zj4yGeR8DUJUOsJiE%3D5IdDlBQejs743NDbLB4gRhLylmBtVUaOZGXGotB8rt8Ikz4AVE"
const val CONSUMER_KEY = "CW1SJ7nBC6i3FhzwNm5p3qpQw"
const val CONSUMER_SECRET = "n0zLyNSMswYNJDwf8bd1e2A82sPIqjn73ZHkh0rpkAVjsDMTWT"
const val ACCESS_TOKEN = "818231716703772674-UkqjJuEJ0WDgcbcK7MZutX0NvJC6Vw4"
const val ACCESS_TOKEN_SECRET = "RSaE9NWZsHmolkwXoUlISRNWR5PoPgOPvJRto6hqGLABV"