package com.polygloot.mobile.polygloot.network.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator


@Serializable
data class ChatCompletionRequestDTO(
    val model: String = "gpt-4o-audio-preview",
    val max_completion_tokens: Int = 300,
    val audio: AudioFormat = AudioFormat(voice = "alloy", format = "wav"),
    val modalities: List<String> = listOf("text", "audio"),
    val messages: List<MessageInput>
)

@Serializable
data class AudioFormat(
    val voice: String,
    val format: String
)

@Serializable
@JsonClassDiscriminator("type")
sealed class MessageInput

@Serializable
data class MessageInputSimple(
    val role: String,
    val content: String
): MessageInput()

@Serializable
data class MessageInputAdvance(
    val role: String,
    val content: List<UserContent>
): MessageInput()

@Serializable
data class UserContent(
    val type: String,
    val input_audio: InputAudio
)

@Serializable
data class InputAudio(
    val data: String,
    val format: String
)