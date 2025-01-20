package com.polygloot.mobile.android

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.polygloot.mobile.android.ui.conversation.ConversationViewModel
import com.polygloot.mobile.android.ui.login.LoginViewModel
import com.polygloot.mobile.polygloot.network.NetworkClient
import com.polygloot.mobile.polygloot.network.repository.translator.TranslatorRepository
import com.polygloot.mobile.polygloot.network.repository.translator.TranslatorRepositoryImpl
import com.polygloot.mobile.polygloot.network.service.TranslatorOpenAIService
import com.polygloot.mobile.polygloot.network.service.TranslatorOpenAIServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class TranslatorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TranslatorApp)
            modules(dataStoreModule, networkModule, viewModelsModule)
        }
    }
}

val dataStoreModule = module {
    single<DataStore<Preferences>> { provideDataStore(get()) }
}

fun provideDataStore(context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("preferences.settings.preferences_pb") }
    )
}

val viewModelsModule = module {
    factory { LoginViewModel(get()) }
    factory { ConversationViewModel(get()) }
}

val networkModule = module {
    single<TranslatorRepository> { TranslatorRepositoryImpl(get()) }
    single<TranslatorOpenAIService> { TranslatorOpenAIServiceImpl(get()) }
    single<NetworkClient> { NetworkClient() }
}