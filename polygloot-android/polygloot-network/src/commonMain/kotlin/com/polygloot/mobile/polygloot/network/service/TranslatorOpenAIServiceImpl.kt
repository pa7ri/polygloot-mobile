package com.polygloot.mobile.polygloot.network.service

import com.polygloot.mobile.polygloot.network.NetworkClient
import com.polygloot.mobile.polygloot.network.dto.ChatCompletionDTO
import com.polygloot.mobile.polygloot.network.dto.ChatCompletionRequestDTO
import com.polygloot.mobile.polygloot.network.dto.InputAudio
import com.polygloot.mobile.polygloot.network.dto.MessageInputAdvance
import com.polygloot.mobile.polygloot.network.dto.MessageInputSimple
import com.polygloot.mobile.polygloot.network.dto.SpeechToTextDTO
import com.polygloot.mobile.polygloot.network.dto.UserContent
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import java.io.File

class TranslatorOpenAIServiceImpl(private val networkClient: NetworkClient) :
    TranslatorOpenAIService {

    override suspend fun detectLanguage(inputText: String): TranslatorNetworkResponse<String, String> =
        networkClient.detectLanguage(inputText).handleResponse()

    override suspend fun translateText(
        inputText: String,
        firstLanguage: String,
        secondLanguage: String
    ): TranslatorNetworkResponse<ChatCompletionDTO, String> =
        networkClient.translateText(inputText, firstLanguage, secondLanguage).handleResponse()

    override suspend fun translateTextAndTTS(
        inputText: String,
        voice: String,
        sourceLanguage: String,
        targetLanguage: String
    ): TranslatorNetworkResponse<ChatCompletionDTO, String> =
        networkClient.translateTextAndTextToSpeech(inputText, voice, sourceLanguage, targetLanguage)
            .handleResponse()

    override suspend fun translateAudioAndTTS(
        base64audio: String,
        sourceLanguage: String,
        targetLanguage: String
    ): TranslatorNetworkResponse<ChatCompletionDTO, String> {
        val bodyRequestDTO = ChatCompletionRequestDTO(
            messages = listOf(
                MessageInputSimple(
                    role = "system",
                    content = "You are a helpful translator, you are given an input text in $sourceLanguage, translate it to $targetLanguage"
                ),
                MessageInputAdvance(
                    role = "user",
                    content = listOf(
                        UserContent("input_audio", InputAudio(data = base64audio, format = "mp3"))
                    )
                )
            )
        )
        return networkClient.translateAudioAndTextToSpeech(bodyRequestDTO).handleResponse()
    }

    override suspend fun detectLanguageAndTranslate(
        inputText: String,
        targetLanguage: String
    ): TranslatorNetworkResponse<String, String> =
        networkClient.detectLanguageAndTranslate(inputText, targetLanguage).handleResponse()

    override suspend fun textToSpeech(
        inputText: String,
        voice: String,
        outputFile: File
    ): TranslatorNetworkResponse<File, String> =
        networkClient.textToSpeech(inputText, voice).handleAudioResponse(outputFile)

    override suspend fun speechToText(filePath: String): TranslatorNetworkResponse<SpeechToTextDTO, String> =
        networkClient.speechToText(filePath).handleResponse()

    private suspend inline fun <reified T : Any> HttpResponse.handleResponse(customBody: T? = null): TranslatorNetworkResponse<T, String> =
        if (this.status.isSuccess()) {
            TranslatorNetworkResponse.Success(customBody ?: this.body())
        } else {
            this.handleErrorResponse()
        }

    private suspend fun HttpResponse.handleAudioResponse(outputFile: File): TranslatorNetworkResponse<File, String> =
        if (this.status.isSuccess() && this.contentType()?.match(ContentType.Audio.MPEG) == true) {
            val audioBytes = this.readBytes()
            outputFile.outputStream().use { fileOut ->
                fileOut.write(audioBytes)
            }
            TranslatorNetworkResponse.Success(outputFile)
        } else {
            this.handleErrorResponse()
        }
}