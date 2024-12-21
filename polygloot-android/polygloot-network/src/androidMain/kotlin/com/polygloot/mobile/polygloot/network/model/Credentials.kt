package com.polygloot.mobile.polygloot.network.model

import com.polygloot.mobile.polygloot.network.BuildConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserCredentials(val username: String, val password: String)

fun getCredentials(): Map<String, String> {
    val jsonString = BuildConfig.POLYGLOOT_TESTING_CREDENTIALS
    return Json.decodeFromString<List<UserCredentials>>(jsonString).associate { it.username to it.password }
}