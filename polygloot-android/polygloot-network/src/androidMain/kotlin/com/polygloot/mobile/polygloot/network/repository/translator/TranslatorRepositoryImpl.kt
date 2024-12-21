package com.polygloot.mobile.polygloot.network.repository.translator

import com.polygloot.mobile.polygloot.network.dto.TranslationDTO
import com.polygloot.mobile.polygloot.network.model.TTSResponse
import com.polygloot.mobile.polygloot.network.model.TranslationResponse
import com.polygloot.mobile.polygloot.network.repository.DomainResult
import com.polygloot.mobile.polygloot.network.repository.toDomainResult
import com.polygloot.mobile.polygloot.network.service.TranslatorOpenAIService
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class TranslatorRepositoryImpl @Inject constructor(private val service: TranslatorOpenAIService) :
    TranslatorRepository {
    override suspend fun detectLanguage(text: String): DomainResult<String> =
        service.detectLanguage(text).toDomainResult { it }

    override suspend fun translateText(
        text: String,
        firstLanguage: String,
        secondLanguage: String
    ): DomainResult<TranslationResponse> =
        service.translateText(text, firstLanguage, secondLanguage).toDomainResult {
            it.choices.first().message.content?.let {
                Json.decodeFromString<TranslationDTO>(it).let {
                    TranslationResponse(it.translatedText, it.sourceLanguage)
                }
            } ?: TranslationResponse("", "")
        }


    override suspend fun textToSpeech(text: String, voice: String, outputFile: File): DomainResult<Boolean> =
        service.textToSpeech(text, voice, outputFile).toDomainResult { it.isFile }

    override suspend fun translateTextAndTTS(
        text: String,
        voice: String,
        sourceLanguage: String,
        targetLanguage: String
    ): DomainResult<TTSResponse> =
        service.translateTextAndTTS(text, voice, sourceLanguage, targetLanguage).toDomainResult {
            it.choices.firstOrNull()?.message?.audio?.let {
                TTSResponse(it.transcript, it.data)
            } ?: TTSResponse(null, null)
        }

    override suspend fun translateAudioAndTTS(
        base64audio: String,
        sourceLanguage: String,
        targetLanguage: String
    ): DomainResult<TTSResponse> =
        service.translateAudioAndTTS(base64audio, sourceLanguage, targetLanguage).toDomainResult {
            it.choices.firstOrNull()?.message?.audio?.let {
                TTSResponse(it.transcript, it.data)
            } ?: TTSResponse(null, null)
        }

    override suspend fun speechToText(filePath: String): DomainResult<String> =
        service.speechToText(filePath).toDomainResult { it.text }
}