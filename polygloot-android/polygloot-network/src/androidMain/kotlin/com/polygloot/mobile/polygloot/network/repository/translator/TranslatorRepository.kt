package com.polygloot.mobile.polygloot.network.repository.translator

import com.polygloot.mobile.polygloot.network.model.TTSResponse
import com.polygloot.mobile.polygloot.network.model.TranslationResponse
import com.polygloot.mobile.polygloot.network.repository.DomainResult
import java.io.File

interface TranslatorRepository {
    suspend fun detectLanguage(text: String): DomainResult<String>

    suspend fun translateTextAndTTS(
        text: String,
        voice: String,
        sourceLanguage: String,
        targetLanguage: String
    ): DomainResult<TTSResponse>

    suspend fun translateAudioAndTTS(
        base64audio: String,
        sourceLanguage: String,
        targetLanguage: String
    ): DomainResult<TTSResponse>

    suspend fun textToSpeech(text: String, voice: String, outputFile: File): DomainResult<Boolean>
    suspend fun speechToText(filePath: String): DomainResult<String>
    suspend fun translateText(
        text: String,
        firstLanguage: String,
        secondLanguage: String
    ): DomainResult<TranslationResponse>
}