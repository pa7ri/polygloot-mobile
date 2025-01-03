package com.polygloot.mobile.polygloot.network

import com.polygloot.mobile.polygloot.network.dto.ChatCompletionRequestDTO
import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse

expect class NetworkClient {
    internal val client: HttpClient
    suspend fun detectLanguage(inputText: String): HttpResponse
    suspend fun testingQuery(): HttpResponse
    suspend fun translateText(
        inputText: String,
        firstLanguage: String,
        secondLanguage: String
    ): HttpResponse

    suspend fun detectLanguageAndTranslate(inputText: String, targetLanguage: String): HttpResponse
    suspend fun textToSpeech(inputText: String, voice: String): HttpResponse
    suspend fun translateTextAndTextToSpeech(
        inputText: String,
        voice: String,
        sourceLanguage: String,
        targetLanguage: String
    ): HttpResponse

    suspend fun translateAudioAndTextToSpeech(bodyRequestDTO: ChatCompletionRequestDTO): HttpResponse

    suspend fun speechToText(filePath: String): HttpResponse
}