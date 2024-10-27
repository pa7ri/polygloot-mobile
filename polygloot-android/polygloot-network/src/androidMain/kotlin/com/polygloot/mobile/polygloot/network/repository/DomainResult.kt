package com.polygloot.mobile.polygloot.network.repository

sealed class DomainResult<out T : Any> {

    /**
     * A successful response from the server, which might include a body.
     */
    data class Success<T : Any>(val body: T?) : DomainResult<T>()

    /**
     * A generic error response, which can be further divided into more specific failure types
     */
    abstract class Error : DomainResult<Nothing>()

    /**
     * Invalid request. Should not be repeated without any changes
     */
    object Fail : Error()
}