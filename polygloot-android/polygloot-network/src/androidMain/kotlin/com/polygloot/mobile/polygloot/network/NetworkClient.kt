package com.polygloot.mobile.polygloot.network

import android.util.Log
import com.polygloot.mobile.polygloot.network.dto.ChatCompletionRequestDTO
import com.polygloot.mobile.polygloot.network.service.HttpRoutes
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

actual class NetworkClient @Inject constructor() {

    private val openAIApiKey = BuildConfig.OPENAI_API_KEY

    internal actual val client: HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                classDiscriminator = "type"
                prettyPrint = true
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        engine {
            config {
                retryOnConnectionFailure(true)
            }
        }
        //Logging Plugin
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    Log.v("Logger Ktor =>", message)
                }

            }
            level = LogLevel.ALL
        }
        //Response Observer Plugin
        install(ResponseObserver) {
            onResponse { response ->
                Log.d("HTTP status:", "${response.status.value}")
            }
        }
        install(DefaultRequest) {
            bearerAuth(openAIApiKey)
        }
    }

    actual suspend fun detectLanguage(inputText: String): HttpResponse {
        return client.post {
            url(HttpRoutes.getCompletionsRoute())
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody( //TODO: generate proper DTO file
                """
            {
                "model": "gpt-4o-mini-2024-07-18",
                "messages": [
                    {
                        "role": "system",
                        "content": "You are a helpful translator, detect the language of the input text and return it as a single String"
                    },
                    {
                        "role": "user",
                        "content": "$inputText"
                    }
                ]
            }
            """.trimIndent()
            )
        }
    }

    actual suspend fun translateText(
        inputText: String,
        firstLanguage: String,
        secondLanguage: String
    ): HttpResponse {
        return client.post {
            url(HttpRoutes.getCompletionsRoute())
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(
                """
                {
                "model": "gpt-4o-mini-2024-07-18",
                "messages": [
                    {
                      "role": "system",
                      "content": "You are a translator assistant. Given 2 languages: firstLanguage and secondLanguage, and input text, translate the input text from one language to another and return a JSON with the translated text as translatedText and the original language as sourceLanguage"
                    },
                    {
                      "role": "user",
                      "content": "{ \"firstLanguage\": \"$firstLanguage\", \"secondLanguage\": \"$secondLanguage\", \"text\": \"$inputText\" }"
                    }
                  ],
                  "response_format": {
                    "type": "json_schema",
                    "json_schema": {
                      "name": "translation_response",
                      "strict": true,
                      "schema": {
                        "type": "object",
                        "properties": {
                            "sourceLanguage": {
                              "type": "string"
                            },
                            "translatedText": {
                              "type": "string"
                            }
                        },
                        "required": ["sourceLanguage", "translatedText"],
                        "additionalProperties": false
                      }
                    }
                  },
                  "temperature": 0.2 
                  }
            """.trimIndent()
            )
        }
    }

    actual suspend fun detectLanguageAndTranslate(
        inputText: String,
        targetLanguage: String
    ): HttpResponse {
        return client.post {
            url(HttpRoutes.getCompletionsRoute())
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody( //TODO: generate proper DTO file
                """
            {
                "model": "gpt-4o-mini-2024-07-18",
                "messages": [
                    {
                        "role": "system",
                        "content": "You are a helpful translator, you are given an input text, detect the language of it, translate it to $targetLanguage. Then return the translated text as a single String"
                    },
                    {
                        "role": "user",
                        "content": "$inputText"
                    }
                ]
            }
            """.trimIndent()
            )
        }
    }

    actual suspend fun textToSpeech(inputText: String, voice: String): HttpResponse {
        return client.post {
            url(HttpRoutes.getAudioSpeechRoute())
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody( //TODO: generate proper DTO file
                """
            {
                "model": "tts-1",
                "input": "$inputText",
                "voice": "$voice"
            }
            """.trimIndent()
            )
        }
    }

    actual suspend fun speechToText(filePath: String): HttpResponse {
        val supportedFormats = listOf("flac", "m4a", "mp3", "mp4", "mpeg", "mpga", "oga", "ogg", "wav", "webm")
        val file = File(filePath)
        val fileExtension = file.extension.lowercase()

        if (fileExtension !in supportedFormats) {
            throw IllegalArgumentException("Invalid file format: $fileExtension Supported formats: $supportedFormats")
        }

        return client.post {
            url(HttpRoutes.getTranscriptionRoute())
            header(HttpHeaders.ContentType, ContentType.MultiPart.FormData.toString())
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", File(filePath).readBytes(), Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"file\"; filename=\"${File(filePath).name}\""
                            )
                        })
                        append("model", "whisper-1")
                        append("response_format", "json")
                    }
                ))
        }
    }

    actual suspend fun translateTextAndTextToSpeech(
        inputText: String,
        voice: String,
        sourceLanguage: String,
        targetLanguage: String
    ): HttpResponse{
        return client.post {
            url(HttpRoutes.getCompletionsRoute())
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody( //TODO: generate proper DTO file
                """
            {
                "model": "gpt-4o-audio-preview",
                "modalities": ["text", "audio"],
                "audio": { "voice": "$voice", "format": "wav" },
                "max_completion_tokens": 300,
                "messages": [
                    {
                        "role": "system",
                        "content": "You are a helpful translator, you are given an input text in $sourceLanguage, translate it to $targetLanguage, don't think about the content of the input, just translate it"
                    },
                    {
                        "role": "user",
                        "content": "$inputText"
                    }
                ]
            }
            """.trimIndent()
            )
        }
    }



    actual suspend fun testingQuery(): HttpResponse {
        return client.post {
            url(HttpRoutes.getCompletionsRoute())
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody( //TODO: generate proper DTO file
                """
            {
                "model": "gpt-4o-audio-preview",
                "modalities": ["text", "audio"],
                "audio": { "voice": "alloy", "format": "wav" },
                "max_completion_tokens": 300,
                "messages": [
                    {
                        "role": "system",
                        "content": "You are a multilingual assistant. Your task is to: 1) Analyze the audio input to identify which of the two provided languages it is spoken in. 2) Transcribe the audio into text. 3) Translate the text into the other language. 4) Respond with JSON containing the detected source language, the transcription, and the translated text in plain text. 5) Provide an audio response only for the translated text, formatted as a WAV file."
                    },
                    {
                        "role": "user",
                        "content": {
                            "languages": {
                                "language_1": "English",
                                "language_2": "Spanish"
                            },
                            "audio": "UklGRmQAAABXQVZFZm10IBAAAAABAAEAgD4AAIA+AAABAAgAZGF0YQAAAC7AAAAEQAAAAUAAAAEAAAABcADwAAAAAAAEAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABACAEQAAQAEQABQAEQAAQAEQAAAAA="
                        }
                    }
                ]
            }
            """.trimIndent()
            )
        }
    }

    actual suspend fun translateAudioAndTextToSpeech(bodyRequestDTO: ChatCompletionRequestDTO): HttpResponse {
        return client.post {
            url(HttpRoutes.getCompletionsRoute())
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(bodyRequestDTO)
        }
    }
}
