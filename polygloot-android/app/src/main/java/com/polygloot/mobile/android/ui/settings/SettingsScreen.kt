package com.polygloot.mobile.android.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.jamal.composeprefs.ui.PrefsScreen
import com.jamal.composeprefs.ui.prefs.MultiSelectListPref
import com.jamal.composeprefs.ui.prefs.SwitchPref
import com.polygloot.mobile.android.ui.utils.Consts.Companion.SELECTED_LANGUAGES_KEY
import com.polygloot.mobile.android.ui.utils.Consts.Companion.SUPPORTED_LANGUAGES

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
    modifier: Modifier,
    viewModel: SettingsViewModel,
    dataStore: DataStore<Preferences>
) {
    Scaffold(modifier = modifier.background(color = MaterialTheme.colorScheme.surface)) { paddingValues ->
        PrefsScreen(modifier = Modifier.padding(paddingValues), dataStore = dataStore) {
            prefsItem {
                SwitchPref(
                    key = "autodetectLanguage",
                    title = "Auto-detect language",
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }
            prefsItem {
                MultiSelectListPref(
                    key = SELECTED_LANGUAGES_KEY,
                    title = "Favorite languages",
                    textColor = MaterialTheme.colorScheme.onSurface,
                    dialogBackgroundColor = MaterialTheme.colorScheme.surface,
                    summary = "Select your favorite languages",
                    defaultValue = setOf("Spanish", "Italian"),
                    entries = SUPPORTED_LANGUAGES
                )
            }

            prefsItem {
                SwitchPref(
                    key = "optimalPerformance",
                    title = "High-speed translations",
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
