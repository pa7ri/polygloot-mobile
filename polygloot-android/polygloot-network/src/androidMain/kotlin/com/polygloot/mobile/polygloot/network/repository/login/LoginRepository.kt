package com.polygloot.mobile.polygloot.network.repository.login

import com.polygloot.mobile.polygloot.network.model.LoggedInUser
import com.polygloot.mobile.polygloot.network.repository.DomainResult

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): DomainResult<LoggedInUser> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is DomainResult.Success<LoggedInUser>) {
            result.body?.let { setLoggedInUser(it) }
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}