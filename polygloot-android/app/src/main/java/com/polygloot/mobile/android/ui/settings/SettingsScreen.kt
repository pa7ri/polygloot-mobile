package com.polygloot.mobile.android.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.jamal.composeprefs.ui.Divider
import com.polygloot.mobile.android.ui.theme.LanguagePickerPreference
import com.polygloot.mobile.android.ui.theme.SearchableMultiSelectScreen

@Composable
fun SettingsScreen(
    modifier: Modifier,
    dataStore: DataStore<Preferences>
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
    ) { paddingValues ->
        val displayLanguageSelectionDialog = remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            LanguagePickerPreference {
                displayLanguageSelectionDialog.value = true
            }
            Divider(Modifier.padding(horizontal = 16.dp))
        }

        if (displayLanguageSelectionDialog.value) {
            SearchableMultiSelectScreen(dataStore = dataStore) {
                displayLanguageSelectionDialog.value = false
            }
        }
    }
}
