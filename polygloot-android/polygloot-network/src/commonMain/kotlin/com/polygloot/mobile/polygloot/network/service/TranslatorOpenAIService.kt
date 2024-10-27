package com.polygloot.mobile.polygloot.network.service

import com.polygloot.mobile.polygloot.network.dto.ChatCompletionDTO
import com.polygloot.mobile.polygloot.network.dto.SpeechToTextDTO
import java.io.File

interface TranslatorOpenAIService {
    suspend fun detectLanguage(inputText: String): TranslatorNetworkResponse<String, String>
    suspend fun translateText(inputText: String, sourceLanguage: String, targetLanguage: String): TranslatorNetworkResponse<String, String>
    suspend fun translateTextAndTTS(
        inputText: String,
        sourceLanguage: String,
        targetLanguage: String
    ): TranslatorNetworkResponse<ChatCompletionDTO, String>
    suspend fun translateAudioAndTTS(
        base64audio: String,
        sourceLanguage: String,
        targetLanguage: String
    ): TranslatorNetworkResponse<ChatCompletionDTO, String>
    suspend fun detectLanguageAndTranslate(inputText: String, targetLanguage: String): TranslatorNetworkResponse<String, String>
    suspend fun textToSpeech(inputText: String): TranslatorNetworkResponse<File, String>
    suspend fun speechToText(filePath: String): TranslatorNetworkResponse<SpeechToTextDTO, String>
}