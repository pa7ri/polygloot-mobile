package com.polygloot.mobile.android.ui.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.polygloot.mobile.android.R
import com.polygloot.mobile.android.ui.theme.ChatScreen
import com.polygloot.mobile.android.ui.theme.LanguagePicker
import com.polygloot.mobile.android.ui.theme.PolyglootAnimation
import com.polygloot.mobile.android.ui.theme.SearchableMultiSelectScreen
import com.polygloot.mobile.android.ui.translator.ErrorType
import com.polygloot.mobile.android.ui.translator.TranslatorActivity
import com.polygloot.mobile.android.ui.translator.TranslatorStatusError
import com.polygloot.mobile.android.ui.translator.TranslatorStatusLoading
import com.polygloot.mobile.android.ui.utils.AudioUtils.Companion.isRecordAudioPermissionGranted
import com.polygloot.mobile.android.ui.utils.Consts.Companion.PREFERENCES_SETTINGS_SELECTED_LANGUAGES_KEY
import com.polygloot.mobile.android.ui.utils.Consts.Companion.SUPPORTED_LANGUAGES
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.AbstractMap

@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    activity: TranslatorActivity,
    viewModel: ConversationViewModel,
    dataStore: DataStore<Preferences>
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var showLanguageSelectionDialog by remember { mutableStateOf(false) }

    val preferredLanguages by dataStore.data.map { preferences ->
        preferences[stringSetPreferencesKey(PREFERENCES_SETTINGS_SELECTED_LANGUAGES_KEY)]?.associateWith {
            SUPPORTED_LANGUAGES[it] ?: ""
        }?.filterValues { it != "" } ?: emptyMap()
    }.collectAsState(initial = emptyMap())

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LanguagePicker(
                    Modifier.padding(10.dp),
                    preselected = viewModel.sourceLanguage.collectAsStateWithLifecycle().value,
                    languages = preferredLanguages,
                    onSelectionChanged = {
                        viewModel.sourceLanguage.value = AbstractMap.SimpleEntry(it.key, it.value)
                    },
                    onMoreLanguagesClicked = { showLanguageSelectionDialog = true }
                )
                Icon(
                    imageVector = Icons.Outlined.SwapHoriz,
                    contentDescription = stringResource(R.string.action_swap),
                    modifier = Modifier.size(24.dp)
                )
                LanguagePicker(
                    Modifier.padding(10.dp),
                    preselected = viewModel.targetLanguage.collectAsStateWithLifecycle().value,
                    languages = preferredLanguages,
                    onSelectionChanged = {
                        viewModel.targetLanguage.value = AbstractMap.SimpleEntry(it.key, it.value)
                    },
                    onMoreLanguagesClicked = { showLanguageSelectionDialog = true }
                )
            }

            ChatScreen(viewModel.translatorResult.collectAsStateWithLifecycle().value)

            FloatingActionButton(
                onClick = {
                    if (isRecording) {
                        if (isRecordAudioPermissionGranted(context)) {
                            activity.lifecycleScope.launch {
                                viewModel.stopRecording()
                            }
                        } else {
                            viewModel.translatorStatus.value =
                                TranslatorStatusError(ErrorType.AUDIO_ERROR)
                        }
                    } else {
                        if (isRecordAudioPermissionGranted(activity)) {
                            activity.externalCacheDir?.absolutePath?.let {
                                viewModel.startRecording(context, it)
                            }
                        } else {
                            viewModel.translatorStatus.value =
                                TranslatorStatusError(ErrorType.AUDIO_ERROR)
                        }
                    }
                    isRecording = !isRecording
                },
                shape = RoundedCornerShape(60.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(if (isRecording) R.drawable.ic_stop else R.drawable.ic_microphone),
                    tint = MaterialTheme.colorScheme.surface,
                    contentDescription = stringResource(R.string.action_microphone)
                )
            }
        }
        when (val status =
            viewModel.translatorStatus.collectAsStateWithLifecycle().value) {
            TranslatorStatusLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    PolyglootAnimation(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.BottomCenter)
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
        if (preferredLanguages.isEmpty() || showLanguageSelectionDialog) {
            SearchableMultiSelectScreen(modifier, dataStore, preferredLanguages.isEmpty()) {
                showLanguageSelectionDialog = false
            }
        }
    }
}