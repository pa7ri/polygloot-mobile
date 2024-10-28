package com.polygloot.mobile.android.ui.translator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.polygloot.mobile.android.ui.theme.CardTranslationText
import com.polygloot.mobile.android.ui.theme.Dimensions
import com.polygloot.mobile.android.ui.theme.PolyglootAnimation
import com.polygloot.mobile.android.ui.theme.PolyglootTheme
import com.polygloot.mobile.android.ui.theme.TabView
import com.polygloot.mobile.android.ui.theme.bottomNavItems
import com.polygloot.mobile.android.ui.utils.AudioUtils.Companion.isRecordAudioPermissionGranted
import com.polygloot.mobile.android.ui.utils.AudioUtils.Companion.requestAudioPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TranslatorActivity : ComponentActivity() {

    private val viewModel by viewModels<TranslatorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PolyglootTheme {
                val context = LocalContext.current
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { TabView(bottomNavItems, navController) }) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = bottomNavItems.first().title
                    ) {
                        composable(bottomNavItems[0].title) {
                            Box(modifier = Modifier.padding(paddingValues = paddingValues)) {
                                Column(
                                    modifier = Modifier
                                        .padding(Dimensions.large)
                                        .fillMaxSize()
                                ) {
                                    CardTranslationText(
                                        modifier = Modifier
                                            .padding(top = Dimensions.medium)
                                            .weight(1f)
                                            .graphicsLayer {
                                                rotationX = 180f
                                            },
                                        contentText = viewModel.targetResult.collectAsStateWithLifecycle().value.result,
                                        languageSelected = viewModel.targetLanguage,
                                        onSelectionChanged = { viewModel.targetLanguage = it },
                                        onRecordingStarts = {
                                            if (isRecordAudioPermissionGranted(context)) {
                                                externalCacheDir?.absolutePath?.let {
                                                    viewModel.startRecording(
                                                        context, it, TranslatorUser.TARGET
                                                    )
                                                }
                                            } else {
                                                requestAudioPermissions(this@TranslatorActivity)
                                            }
                                        },
                                        onRecordingStops = {
                                            if (isRecordAudioPermissionGranted(context)) {
                                                lifecycleScope.launch {
                                                    viewModel.stopRecording(TranslatorUser.TARGET)
                                                }
                                            } else {
                                                requestAudioPermissions(this@TranslatorActivity)
                                            }
                                        }
                                    )

                                    CardTranslationText(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(top = Dimensions.medium),
                                        contentText = viewModel.sourceResult.collectAsStateWithLifecycle().value.result,
                                        languageSelected = viewModel.sourceLanguage,
                                        onSelectionChanged = { viewModel.sourceLanguage = it },
                                        onRecordingStarts = {
                                            if (isRecordAudioPermissionGranted(context)) {
                                                externalCacheDir?.absolutePath?.let {
                                                    viewModel.startRecording(
                                                        context, it, TranslatorUser.SOURCE
                                                    )
                                                }
                                            } else {
                                                requestAudioPermissions(this@TranslatorActivity)
                                            }
                                        },
                                        onRecordingStops = {
                                            if (isRecordAudioPermissionGranted(context)) {
                                                lifecycleScope.launch {
                                                    viewModel.stopRecording(TranslatorUser.SOURCE)
                                                }
                                            } else {
                                                requestAudioPermissions(this@TranslatorActivity)
                                            }
                                        }
                                    )
                                }
                                when (val status =
                                    viewModel.translatorStatus.collectAsStateWithLifecycle().value) {
                                    TranslatorStatusLoading -> {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            PolyglootAnimation(
                                                modifier = Modifier
                                                    .size(200.dp)
                                                    .align(Alignment.Center)
                                            )
                                        }
                                    }

                                    is TranslatorStatusError -> {
                                        AlertDialog(
                                            onDismissRequest = { viewModel.dismissError() },
                                            confirmButton = {
                                                Button(onClick = { viewModel.dismissError() }) {
                                                    Text("OK")
                                                }
                                            },
                                            title = { Text(text = stringResource(status.title)) },
                                            text = { Text(text = stringResource(status.description)) },
                                            properties = DialogProperties(
                                                dismissOnBackPress = true,
                                                dismissOnClickOutside = true
                                            )
                                        )
                                    }

                                    else -> {}
                                }
                            }
                        }
                        composable(bottomNavItems[1].title) {
                            Text(modifier = Modifier.padding(100.dp), text = "WIP - translator tab")
                        }
                        composable(bottomNavItems[2].title) {
                            Text(
                                modifier = Modifier.padding(100.dp),
                                text = "WIP - Save your favorite translations to use them offline"
                            )
                        }
                        composable(bottomNavItems[3].title) {
                            Text(modifier = Modifier.padding(100.dp), text = "WIP - settings tab")
                        }
                    }
                }
            }
        }
    }
}