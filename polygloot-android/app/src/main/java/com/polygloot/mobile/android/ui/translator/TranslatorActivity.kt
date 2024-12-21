package com.polygloot.mobile.android.ui.translator

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.polygloot.mobile.android.ui.conversation.ConversationScreen
import com.polygloot.mobile.android.ui.conversation.ConversationViewModel
import com.polygloot.mobile.android.ui.settings.SettingsScreen
import com.polygloot.mobile.android.ui.settings.SettingsViewModel
import com.polygloot.mobile.android.ui.theme.PolyglootTheme
import com.polygloot.mobile.android.ui.theme.TabView
import com.polygloot.mobile.android.ui.theme.bottomNavItems
import com.polygloot.mobile.android.ui.utils.AudioUtils.Companion.isRecordAudioPermissionGranted
import com.polygloot.mobile.android.ui.utils.AudioUtils.Companion.requestAppPermissions
import com.polygloot.mobile.android.ui.utils.LocationUtils
import com.polygloot.mobile.android.ui.utils.LocationUtils.Companion.isLocationPermissionGranted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.AbstractMap

@AndroidEntryPoint
class TranslatorActivity : ComponentActivity() {

    private val viewModel by viewModels<ConversationViewModel>()
    private val settingsViewModel by viewModels<SettingsViewModel>()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PolyglootTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    if (!isRecordAudioPermissionGranted(context) || !isLocationPermissionGranted(context)
                    ) {
                        requestAppPermissions(this@TranslatorActivity)
                    } else {
                        LocationUtils(activity = this@TranslatorActivity,
                            {
                                it?.let {
                                    viewModel.targetLanguage.value = AbstractMap.SimpleEntry(it.key, it.value)
                                    lifecycleScope.launch {
                                        dataStore.edit { preferences ->
                                            preferences[stringPreferencesKey(it.key)] = it.value
                                        }
                                    }
                                }
                            },
                            {
                                it?.let {
                                    viewModel.sourceLanguage.value = AbstractMap.SimpleEntry(it.key, it.value)
                                    lifecycleScope.launch {
                                        dataStore.edit { preferences ->
                                            preferences[stringPreferencesKey(it.key)] = it.value
                                        }
                                    }
                                }
                            })
                    }
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow),
                    bottomBar = { TabView(bottomNavItems, navController) }) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = bottomNavItems.first().title
                    ) {
                        composable(bottomNavItems[0].title) {
                            ConversationScreen(
                                modifier = Modifier.padding(paddingValues = paddingValues),
                                activity = this@TranslatorActivity,
                                viewModel = viewModel,
                                dataStore = LocalContext.current.dataStore
                            )
                        }
                        composable(bottomNavItems[1].title) {
                            SettingsScreen(
                                modifier = Modifier.padding(paddingValues = paddingValues),
                                viewModel = settingsViewModel,
                                dataStore = LocalContext.current.dataStore
                            )
                        }
                    }
                }
            }
        }
    }
}