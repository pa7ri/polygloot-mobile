package com.polygloot.mobile.polygloot.network.service

import io.ktor.client.statement.HttpResponse

internal fun HttpResponse.handleErrorResponse(): TranslatorNetworkResponse<Nothing, Nothing> {
    return when (val code = status.value) {
        404 -> TranslatorNetworkResponse.NotFound
        in 400..499 -> TranslatorNetworkResponse.ClientError(null, code)
        in 500..599 -> TranslatorNetworkResponse.ServerError(null, code)
        else -> TranslatorNetworkResponse.UnknownError(null)
    }
}