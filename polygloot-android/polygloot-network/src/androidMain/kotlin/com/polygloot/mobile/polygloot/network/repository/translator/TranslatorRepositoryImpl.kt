package com.polygloot.mobile.polygloot.network.repository.translator

import com.polygloot.mobile.polygloot.network.model.TTSResponse
import com.polygloot.mobile.polygloot.network.repository.DomainResult
import com.polygloot.mobile.polygloot.network.repository.toDomainResult
import com.polygloot.mobile.polygloot.network.service.TranslatorOpenAIService
import java.io.File
import javax.inject.Inject

class TranslatorRepositoryImpl @Inject constructor(private val service: TranslatorOpenAIService) :
    TranslatorRepository {
    override suspend fun detectLanguage(text: String): DomainResult<String> =
        service.detectLanguage(text).toDomainResult { it }

    override suspend fun translateText(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): DomainResult<String> =
        service.translateText(text, sourceLanguage, targetLanguage).toDomainResult { it }


    override suspend fun textToSpeech(text: String): DomainResult<File> =
        service.textToSpeech(text).toDomainResult { it }

    override suspend fun translateTextAndTTS(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): DomainResult<TTSResponse> =
        service.translateTextAndTTS(text, sourceLanguage, targetLanguage).toDomainResult {
            TTSResponse(
                it.choices.firstOrNull()?.message?.audio?.transcript,
                it.choices.firstOrNull()?.message?.audio?.data
            )
        }

    override suspend fun translateAudioAndTTS(
        base64audio: String,
        sourceLanguage: String,
        targetLanguage: String
    ): DomainResult<TTSResponse> =
        service.translateAudioAndTTS(base64audio, sourceLanguage, targetLanguage).toDomainResult {
            TTSResponse(
                it.choices.firstOrNull()?.message?.audio?.transcript,
                it.choices.firstOrNull()?.message?.audio?.data
            )
        }

    override suspend fun speechToText(filePath: String): DomainResult<String> =
        service.speechToText(filePath).toDomainResult { it.text }
}