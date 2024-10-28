package com.polygloot.mobile.polygloot.network.repository.login

import com.polygloot.mobile.polygloot.network.model.LoggedInUser
import com.polygloot.mobile.polygloot.network.repository.DomainResult
import com.polygloot.mobile.polygloot.network.BuildConfig
import java.util.UUID

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): DomainResult<LoggedInUser> {
        return try {
            if (username == BuildConfig.POLYGLOOT_TESTING_USERNAME && password == BuildConfig.POLYGLOOT_TESTING_PASSWORD) {
                DomainResult.Success(LoggedInUser(UUID.randomUUID().toString(), "TestUser 101"))
            } else DomainResult.Fail
        } catch (e: Throwable) {
            DomainResult.Fail
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}