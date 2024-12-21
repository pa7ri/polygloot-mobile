package com.polygloot.mobile.polygloot.network.repository.login

import com.polygloot.mobile.polygloot.network.model.LoggedInUser
import com.polygloot.mobile.polygloot.network.model.getCredentials
import com.polygloot.mobile.polygloot.network.repository.DomainResult
import java.util.UUID

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): DomainResult<LoggedInUser> {
        return try {
            if (getCredentials().contains(username) && getCredentials()[username] == password) {
                DomainResult.Success(
                    LoggedInUser(UUID.randomUUID().toString(), "TestUser - $username")
                )
            } else DomainResult.Fail
        } catch (e: Throwable) {
            DomainResult.Fail
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}