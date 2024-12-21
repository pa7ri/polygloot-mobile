package com.polygloot.mobile.polygloot.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TranslationResponse(
    val text: String,
    val sourceLanguage: String
)