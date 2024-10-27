package com.polygloot.mobile.polygloot.network.repository

import com.polygloot.mobile.polygloot.network.service.TranslatorNetworkResponse

fun <T : Any, U : Any> TranslatorNetworkResponse<T, Any>.toDomainResult(mapping: (T) -> U): DomainResult<U> {
    return when (this) {
        TranslatorNetworkResponse.Empty -> DomainResult.Success(null)
        is TranslatorNetworkResponse.Success -> DomainResult.Success(mapping(body))
        else -> DomainResult.Fail
    }
}