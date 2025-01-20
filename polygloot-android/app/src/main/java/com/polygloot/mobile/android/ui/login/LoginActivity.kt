package com.polygloot.mobile.android.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.polygloot.mobile.android.R
import com.polygloot.mobile.android.ui.theme.CredentialsRememberField
import com.polygloot.mobile.android.ui.theme.LoginField
import com.polygloot.mobile.android.ui.theme.PasswordField
import com.polygloot.mobile.android.ui.theme.PolyglootTheme
import com.polygloot.mobile.android.ui.theme.SignInButton
import com.polygloot.mobile.android.ui.translator.TranslatorActivity
import com.polygloot.mobile.android.ui.utils.Consts.Companion.EXTRAS_LOGIN_USERNAME
import com.polygloot.mobile.android.ui.utils.Consts.Companion.signInIntent
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModel<LoginViewModel>()

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract())
    { result -> viewModel.handleSignInResult(result) }

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PolyglootTheme {
                val keyboardVisible = WindowInsets.isImeVisible
                val imeBottomPadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
                LaunchedEffect(Unit) {
                    viewModel.loginResult.observe(this@LoginActivity, Observer { it ->
                        val loginResult = it ?: return@Observer
                        loginResult.error?.let { showLoginFailed(it) }
                        loginResult.success?.let { startTranslatorActivity(it) }
                    })
                }

                Scaffold(
                    content = { paddingValues ->
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxSize()
                                .padding(top = 100.dp, start = 10.dp, end = 10.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(80.dp),
                                painter = painterResource(id = R.mipmap.ic_logo),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Polygloot Logo",
                            )
                            LoginField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 50.dp, start = 10.dp, end = 10.dp),
                                value = viewModel.username.value,
                                onValueChange = {
                                    viewModel.onUsernameChanged(it)
                                }
                            )
                            PasswordField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                value = viewModel.password.value,
                                onValueChange = {
                                    viewModel.onPasswordChanged(it)
                                },
                                onDone = {
                                    viewModel.login()
                                }
                            )
                            CredentialsRememberField(
                                isChecked = viewModel.isChecked.value,
                                onValueChange = {
                                    viewModel.onCheckedChanged(it)
                                }
                            )
                            SignInButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { signInLauncher.launch(signInIntent) }
                            )

                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = if (keyboardVisible) imeBottomPadding else 16.dp)
                            ) {
                                Button(modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                        disabledContainerColor = Color.DarkGray,
                                        disabledContentColor = Color.Gray
                                    ), onClick = {
                                        viewModel.login()
                                    }) {
                                    Text(stringResource(R.string.action_login).uppercase())
                                }
                            }
                        }
                    }
                )
            }
        }
    }


    private fun startTranslatorActivity(model: LoggedInUserView) {
        startActivity(Intent(this, TranslatorActivity::class.java).apply {
            putExtra(EXTRAS_LOGIN_USERNAME, model.displayName)
        })
        setResult(RESULT_OK)
        finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}