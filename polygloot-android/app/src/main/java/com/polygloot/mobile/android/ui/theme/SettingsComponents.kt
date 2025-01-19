package com.polygloot.mobile.android.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.polygloot.mobile.android.R
import com.polygloot.mobile.android.ui.utils.Consts.Companion.PREFERENCES_SETTINGS_SELECTED_LANGUAGES_KEY
import com.polygloot.mobile.android.ui.utils.Consts.Companion.SUPPORTED_LANGUAGES
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun LanguagePickerPreference(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = stringResource(R.string.favorite_languages),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            modifier = Modifier
                .size(24.dp),
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(R.string.favorite_languages)
        )
    }
}

@Composable
fun SearchableMultiSelectScreen(
    modifier: Modifier = Modifier,
    dataStore: DataStore<Preferences>,
    cancelEnabled: Boolean = true,
    onDismiss: () -> Unit = { }
) {
    val scope = rememberCoroutineScope()
    val items = SUPPORTED_LANGUAGES.entries

    var showWarning by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val selectedItems = remember {
        mutableStateListOf<Map.Entry<String, String>>().apply {
            scope.launch {
                addAll(loadSelectedItems(dataStore).associateWith {
                    SUPPORTED_LANGUAGES[it] ?: ""
                }.entries)
            }
        }
    }
    val filteredItems = items.filter { it.value.contains(searchQuery.text, ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = searchQuery,
            label = { Text(stringResource(R.string.action_search)) },
            placeholder = { Text(stringResource(R.string.action_select_fav_languages)) },
            onValueChange = { searchQuery = it },
            singleLine = true,
            trailingIcon = {
                if (searchQuery.text.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.action_clear),
                        modifier = Modifier.clickable {
                            searchQuery = TextFieldValue("")
                        }
                    )
                }
            }
        )

        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            state = listState
        ) {
            val nonSelectedItems = filteredItems.filterNot { selectedItems.contains(it) }

            if (selectedItems.isNotEmpty()) {
                item {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = stringResource(R.string.preferred_languages),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                items(selectedItems.sortedBy { it.value }, key = { "${it}+${UUID.randomUUID()}" }) { item ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            selectedItems.remove(item)
                        }) {
                        Text(
                            modifier = Modifier,
                            text = item.value,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            if (nonSelectedItems.isNotEmpty()) {
                item {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = stringResource(R.string.other_languages),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                items(nonSelectedItems, key = { "${it}+${UUID.randomUUID()}" }) { item ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            selectedItems.add(item)
                        }) {
                        Text(
                            modifier = Modifier,
                            text = item.value,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Button(modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.DarkGray,
                    disabledContentColor = Color.Gray
                ), onClick = {
                    if(selectedItems.size < 2) {
                        showWarning = true
                    } else {
                        scope.launch {
                            saveSelectedItems(dataStore, selectedItems.map { it.key })
                            onDismiss()
                        }
                    }
                }) { Text(stringResource(R.string.action_save).uppercase()) }

            if(cancelEnabled) {
                Button(modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = Color.DarkGray,
                        disabledContentColor = Color.Gray
                    ), onClick = { onDismiss() }) {
                    Text(stringResource(R.string.action_cancel).uppercase())
                }
            }
        }
    }

    LanguageWarningSnackbar(
        message = stringResource(R.string.select_min_languages_warning),
        isVisible = showWarning,
        duration = SnackbarDuration.Short,
        onDismiss = { showWarning = false }
    )
}


@Composable
fun LanguageWarningSnackbar(
    message: String,
    isVisible: Boolean,
    duration: SnackbarDuration = SnackbarDuration.Short,
    onDismiss: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    if (isVisible) {
        LaunchedEffect(key1 = isVisible) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = duration
            )
            onDismiss()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            snackbar = {
                Snackbar(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = message)
                }
            }
        )
    }
}



private suspend fun saveSelectedItems(
    dataStore: DataStore<Preferences>,
    selectedItemKeys: List<String>
) {
    val key = stringSetPreferencesKey(PREFERENCES_SETTINGS_SELECTED_LANGUAGES_KEY)
    dataStore.edit { preferences ->
        preferences[key] = selectedItemKeys.toSet()
    }
}

private suspend fun loadSelectedItems(dataStore: DataStore<Preferences>): Set<String> {
    val key = stringSetPreferencesKey(PREFERENCES_SETTINGS_SELECTED_LANGUAGES_KEY)
    return dataStore.data.first()[key] ?: emptySet()
}