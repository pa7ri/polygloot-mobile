package com.polygloot.mobile.polygloot.network.di

import com.polygloot.mobile.polygloot.network.NetworkClient
import com.polygloot.mobile.polygloot.network.repository.TranslatorRepository
import com.polygloot.mobile.polygloot.network.repository.TranslatorRepositoryImpl
import com.polygloot.mobile.polygloot.network.service.TranslatorOpenAIServiceImpl
import com.polygloot.mobile.polygloot.network.service.TranslatorOpenAIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TranslatorModule {

    @Singleton
    @Provides
    fun provideTranslatorRepository(translatorService: TranslatorOpenAIService): TranslatorRepository {
        return TranslatorRepositoryImpl(translatorService)
    }

    @Provides
    fun provideAttendeesService(networkClient: NetworkClient): TranslatorOpenAIService {
        return TranslatorOpenAIServiceImpl(networkClient)
    }
}