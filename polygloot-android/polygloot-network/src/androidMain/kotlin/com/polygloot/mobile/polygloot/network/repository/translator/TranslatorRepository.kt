package com.polygloot.mobile.polygloot.network.repository.translator

import com.polygloot.mobile.polygloot.network.model.TTSResponse
import com.polygloot.mobile.polygloot.network.repository.DomainResult
import java.io.File

interface TranslatorRepository { //TODO: add domain result logic instead of returning raw data
    suspend fun detectLanguage(text: String): DomainResult<String>
    suspend fun translateText(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): DomainResult<String>

    suspend fun translateTextAndTTS(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): DomainResult<TTSResponse>

    suspend fun translateAudioAndTTS(
        base64audio: String,
        sourceLanguage: String,
        targetLanguage: String
    ): DomainResult<TTSResponse>

    suspend fun textToSpeech(text: String): DomainResult<File>
    suspend fun speechToText(filePath: String): DomainResult<String>
}