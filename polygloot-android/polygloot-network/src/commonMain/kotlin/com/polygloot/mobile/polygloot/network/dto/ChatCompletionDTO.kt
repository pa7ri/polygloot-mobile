package com.polygloot.mobile.polygloot.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionDTO(
    val id: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message
)

@Serializable
data class Message(
    val role: String,
    val content: String? = null,
    val refusal: String? = null,
    val audio: Audio? = null
)

@Serializable
data class Audio(
    val id: String,
    val data: String, // This will hold the base64 encoded audio data
    val transcript: String
)
