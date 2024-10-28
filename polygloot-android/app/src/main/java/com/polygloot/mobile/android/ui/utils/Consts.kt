package com.polygloot.mobile.android.ui.utils

class Consts {
    companion object {

        const val REQUEST_RECORD_AUDIO_PERMISSION = 200

        const val EXTRAS_LOGIN_USERNAME = "extras.login.username"
        const val AUDIO_RESPONSE_NAME = "audio_output.mp3"

        val SUPPORTED_LANGUAGES: List<Pair<String, String>> = listOf(
        "Spanish" to "Spanish",
        "English" to "English",
        "Italian" to "Italian",
        "French" to "French",
        "German" to "German",
        )
    }
}