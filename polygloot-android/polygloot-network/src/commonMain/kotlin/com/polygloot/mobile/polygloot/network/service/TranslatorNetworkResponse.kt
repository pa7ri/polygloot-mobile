package com.polygloot.mobile.polygloot.network.service

import io.ktor.utils.io.errors.IOException

sealed class TranslatorNetworkResponse<out T : Any, out U : Any> {
    /**
     * Success response with body
     */
    data class Success<T : Any>(val body: T) : TranslatorNetworkResponse<T, Nothing>()

    /**
     * Success response with empty body
     */
    object Empty : TranslatorNetworkResponse<Nothing, Nothing>()

    /**
     * Not found
     */
    object NotFound : TranslatorNetworkResponse<Nothing, Nothing>()

    /**
     * Failure response with body
     */
    data class ClientError<U : Any>(val body: U?, val code: Int) : TranslatorNetworkResponse<Nothing, U>()

    /**
     * Failure response with body
     */
    data class ServerError<U : Any>(val body: U?, val code: Int) : TranslatorNetworkResponse<Nothing, U>()

    /**
     * Network error
     */
    data class NetworkError(val error: IOException) : TranslatorNetworkResponse<Nothing, Nothing>()

    /**
     * Generic error indication a failure in serialization or deserialization process.
     */
    data class SerializationError(val error: Throwable?) : TranslatorNetworkResponse<Nothing, Nothing>()

    /**
     * Other error cases
     */
    data class UnknownError(val error: Throwable?) : TranslatorNetworkResponse<Nothing, Nothing>()
}