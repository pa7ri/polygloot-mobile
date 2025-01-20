package com.polygloot.mobile.android.ui.login

import android.app.Activity.RESULT_OK
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.polygloot.mobile.android.R
import com.polygloot.mobile.android.ui.utils.Consts.Companion.PREFERENCES_LOGIN_REMEMBER_CHECKED
import com.polygloot.mobile.android.ui.utils.Consts.Companion.PREFERENCES_LOGIN_REMEMBER_PASSWORD
import com.polygloot.mobile.android.ui.utils.Consts.Companion.PREFERENCES_LOGIN_REMEMBER_USERNAME
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth

    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var isChecked = mutableStateOf(false)

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    init {
        loadCredentials()
    }

    fun login() {
        auth.signInWithEmailAndPassword(username.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginResult.value =
                        task.result.user?.let {
                            LoginResult(
                                success = LoggedInUserView(
                                    displayName = it.displayName ?: ""
                                )
                            )
                        }
                            ?: LoginResult(error = R.string.login_failed)
                    saveCredentials()
                } else {
                    _loginResult.value = LoginResult(error = R.string.login_failed)
                }
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

    private fun saveCredentials() {
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

    fun loadCredentials() {
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

    fun handleSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = user?.displayName ?: ""))
        } else {
            response?.let {
                _loginResult.value = LoginResult(error = R.string.signin_failed)
            }
        }
    }
}