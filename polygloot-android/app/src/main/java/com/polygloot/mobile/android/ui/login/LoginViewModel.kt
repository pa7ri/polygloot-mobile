package com.polygloot.mobile.android.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewModelScope
import com.polygloot.mobile.android.R
import com.polygloot.mobile.android.ui.utils.Consts.Companion.PREFERENCES_LOGIN_REMEMBER_CHECKED
import com.polygloot.mobile.android.ui.utils.Consts.Companion.PREFERENCES_LOGIN_REMEMBER_PASSWORD
import com.polygloot.mobile.android.ui.utils.Consts.Companion.PREFERENCES_LOGIN_REMEMBER_USERNAME
import com.polygloot.mobile.polygloot.network.repository.login.LoginRepository
import com.polygloot.mobile.polygloot.network.repository.DomainResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {

    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var isChecked = mutableStateOf(false)

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(dataStore: DataStore<Preferences>) {
        val result = loginRepository.login(username.value, password.value)
        if (result is DomainResult.Success) {
            _loginResult.value =
                result.body?.let { LoginResult(success = LoggedInUserView(displayName = it.displayName)) }
                    ?: LoginResult(error = R.string.login_failed)
            saveCredentials(dataStore)
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun onUsernameChanged(updatedUsername: String) {
        username.value = updatedUsername
        if (!isUserNameValid(updatedUsername)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun onPasswordChanged(updatedPassword: String) {
        password.value = updatedPassword
        if (!isPasswordValid(updatedPassword)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun onCheckedChanged(updatedCheck: Boolean) {
        isChecked.value = updatedCheck
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun saveCredentials(dataStore: DataStore<Preferences>) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(PREFERENCES_LOGIN_REMEMBER_USERNAME)] =
                    if (isChecked.value) username.value else ""
                preferences[stringPreferencesKey(PREFERENCES_LOGIN_REMEMBER_PASSWORD)] =
                    if (isChecked.value) password.value else ""
                preferences[booleanPreferencesKey(PREFERENCES_LOGIN_REMEMBER_CHECKED)] =
                    isChecked.value
            }
        }
    }

    fun loadCredentials(dataStore: DataStore<Preferences>) {
        viewModelScope.launch {
            val keyUsername = stringPreferencesKey(PREFERENCES_LOGIN_REMEMBER_USERNAME)
            val keyPassword = stringPreferencesKey(PREFERENCES_LOGIN_REMEMBER_PASSWORD)
            val keyChecked = booleanPreferencesKey(PREFERENCES_LOGIN_REMEMBER_CHECKED)
            dataStore.data.first().let { preferences ->
                isChecked.value = preferences[keyChecked] ?: false
                if (isChecked.value) {
                    username.value = preferences[keyUsername] ?: ""
                    password.value = preferences[keyPassword] ?: ""
                }
            }
        }
    }
}