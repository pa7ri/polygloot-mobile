package com.polygloot.mobile.android.ui.login

import android.content.Intent
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.annotation.StringRes
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.polygloot.mobile.android.R
import com.polygloot.mobile.android.ui.theme.LoginField
import com.polygloot.mobile.android.ui.theme.PasswordField
import com.polygloot.mobile.android.ui.theme.PolyglootTheme
import com.polygloot.mobile.android.ui.translator.TranslatorActivity
import com.polygloot.mobile.android.ui.utils.Consts.Companion.EXTRAS_LOGIN_USERNAME
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PolyglootTheme {
                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
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
                                value = username,
                                onValueChange = {
                                    username = it
                                    viewModel.loginDataChanged(username, password)
                                }
                            )
                            PasswordField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                value = password,
                                onValueChange = {
                                    password = it
                                    viewModel.loginDataChanged(username, password)
                                }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Button(modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = Color.DarkGray,
                                    disabledContentColor = Color.Gray
                                ), onClick = {
                                    viewModel.login(username, password)
                                }) {
                                Text("Login".uppercase())
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