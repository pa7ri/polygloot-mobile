package com.polygloot.mobile.polygloot.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class SpeechToTextDTO(
    val text: String
)