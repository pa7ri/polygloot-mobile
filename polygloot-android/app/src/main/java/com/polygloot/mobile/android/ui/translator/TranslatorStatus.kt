package com.polygloot.mobile.android.ui.translator

import androidx.annotation.StringRes
import com.polygloot.mobile.android.R

sealed class TranslatorStatus

data object TranslatorStatusIdle : TranslatorStatus()

data object TranslatorStatusLoading : TranslatorStatus()

enum class ErrorType(@StringRes val title: Int, @StringRes val description: Int) {
    NETWORK_ERROR(R.string.network_error_title, R.string.network_error_description),
    TRANSLATION_ERROR(R.string.translation_error_title, R.string.translation_error_description),
    AUDIO_ERROR(R.string.audio_error_title, R.string.audio_error_description)
}

data class TranslatorStatusError(val errorType: ErrorType) : TranslatorStatus() {
    @StringRes val title: Int = errorType.title
    @StringRes val description: Int = errorType.description
}

enum class TranslatorUser {
    SOURCE,
    TARGET
}

data class TranslatorResult(var user: TranslatorUser, var result: String)
