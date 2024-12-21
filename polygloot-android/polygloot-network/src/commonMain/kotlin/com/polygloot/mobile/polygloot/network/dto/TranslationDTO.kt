package com.polygloot.mobile.polygloot.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranslationDTO(
    val translatedText: String,
    val sourceLanguage: String
)