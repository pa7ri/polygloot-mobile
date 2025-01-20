package com.polygloot.mobile.android.ui.translator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.polygloot.mobile.android.R
import com.polygloot.mobile.android.ui.conversation.ConversationScreen
import com.polygloot.mobile.android.ui.conversation.ConversationViewModel
import com.polygloot.mobile.android.ui.settings.SettingsScreen
import com.polygloot.mobile.android.ui.theme.PolyglootTheme
import com.polygloot.mobile.android.ui.theme.TabView
import com.polygloot.mobile.android.ui.theme.bottomNavItems
import com.polygloot.mobile.android.ui.utils.AudioUtils.Companion.isRecordAudioPermissionGranted
import com.polygloot.mobile.android.ui.utils.AudioUtils.Companion.requestAppPermissions
import com.polygloot.mobile.android.ui.utils.Consts.Companion.EXTRAS_LOGIN_USERNAME
import com.polygloot.mobile.android.ui.utils.LocationUtils
import com.polygloot.mobile.android.ui.utils.LocationUtils.Companion.isLocationPermissionGranted
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.inject
import java.util.AbstractMap

class TranslatorActivity : ComponentActivity() {

    private val viewModel: ConversationViewModel by viewModel<ConversationViewModel>()
    private val dataStore: DataStore<Preferences> by inject(clazz = DataStore::class.java)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PolyglootTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    if (!isRecordAudioPermissionGranted(context) || !isLocationPermissionGranted(
                            context
                        )
                    ) {
                        requestAppPermissions(this@TranslatorActivity)
                    } else {
                        LocationUtils(activity = this@TranslatorActivity,
                            {
                                it?.let {
                                    viewModel.targetLanguage.value =
                                        AbstractMap.SimpleEntry(it.key, it.value)
                                    lifecycleScope.launch {
                                        dataStore.edit { preferences ->
                                            preferences[stringPreferencesKey(it.key)] = it.value
                                        }
                                    }
                                }
                            },
                            {
                                it?.let {
                                    viewModel.sourceLanguage.value =
                                        AbstractMap.SimpleEntry(it.key, it.value)
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
                    topBar = {
                        intent.extras?.getString(EXTRAS_LOGIN_USERNAME)?.let {
                            TopAppBar(title = { Text(stringResource(R.string.welcome, it)) })
                        }
                    },
                    bottomBar = { TabView(bottomNavItems, navController) }) { paddingValues ->
                    NavHost(
                        modifier = Modifier.padding(paddingValues),
                        navController = navController,
                        startDestination = bottomNavItems.first().title
                    ) {
                        composable(bottomNavItems[0].title) {
                            ConversationScreen(
                                activity = this@TranslatorActivity,
                                viewModel = viewModel,
                                dataStore = dataStore
                            )
                        }
                        composable(bottomNavItems[1].title) {
                            SettingsScreen(
                                dataStore = dataStore
                            )
                        }
                    }
                }
            }
        }
    }
}