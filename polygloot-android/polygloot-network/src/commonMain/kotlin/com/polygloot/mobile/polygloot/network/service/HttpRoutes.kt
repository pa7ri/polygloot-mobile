package com.polygloot.mobile.polygloot.network.service

object HttpRoutes {

    fun getTranscriptionRoute(): String = "https://api.openai.com/v1/audio/transcriptions"

    fun getAudioSpeechRoute(): String = "https://api.openai.com/v1/audio/speech"

    fun getCompletionsRoute(): String = "https://api.openai.com/v1/chat/completions"
}