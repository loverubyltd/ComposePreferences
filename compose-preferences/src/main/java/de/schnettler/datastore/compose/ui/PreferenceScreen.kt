package de.schnettler.datastore.compose.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.accompanist.insets.statusBarsPadding
import de.schnettler.datastore.compose.LocalPreferenceEnabledStatus
import de.schnettler.datastore.compose.model.Preference
import de.schnettler.datastore.compose.model.Preference.PreferenceGroup
import de.schnettler.datastore.compose.model.Preference.PreferenceItem
import de.schnettler.datastore.manager.DataStoreManager
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Preference Screen composable which contains a list of [Preference] items
 * @param items [Preference] items which should be displayed on the preference screen. An item can be a single [PreferenceItem] or a group ([PreferenceGroup])
 * @param dataStore a [DataStore] where the preferences will be saved
 * @param modifier [Modifier] to be applied to the preferenceScreen layout
 * @param statusBarPadding whether statusBar padding is needed. Set to true if your app is laid out edgeToEdge
 */
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun PreferenceScreen(
    items: List<Preference>,
    dataStore: DataStore<Preferences>,
    modifier: Modifier = Modifier,
    statusBarPadding: Boolean = false,
) {
    val dataStoreManager = remember {
        DataStoreManager(dataStore)
    }

    PreferenceScreen(
        items = items,
        modifier = modifier,
        dataStoreManager = dataStoreManager,
        statusBarPadding = statusBarPadding
    )
}

/**
 * Preference Screen composable which contains a list of [Preference] items
 * @param items [Preference] items which should be displayed on the preference screen. An item can be a single [PreferenceItem] or a group ([PreferenceGroup])
 * @param dataStoreManager a [DataStoreManager] responsible for the dataStore backing the preference screen
 * @param modifier [Modifier] to be applied to the preferenceScreen layout
 * @param statusBarPadding whether statusBar padding is needed. Set to true if your app is laid out edgeToEdge
 */
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun PreferenceScreen(
    items: List<Preference>,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
    statusBarPadding: Boolean = false,
) {
    val prefs by dataStoreManager.preferenceFlow.collectAsState(initial = null)
    LazyColumn(modifier = modifier) {
        if (statusBarPadding) {
            item { Spacer(modifier = Modifier.statusBarsPadding()) }
        }

        items.forEach { preference ->
            when (preference) {
                // Create Preference Group
                is PreferenceGroup -> {
                    item {
                        PreferenceGroupHeader(title = preference.title)
                    }
                    items(preference.preferenceItems) { item ->
                        CompositionLocalProvider(LocalPreferenceEnabledStatus provides preference.enabled) {
                            PreferenceItem(
                                item = item,
                                prefs = prefs,
                                dataStoreManager = dataStoreManager
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Create Preference Item
                is PreferenceItem<*> -> item {
                    PreferenceItem(
                        item = preference,
                        prefs = prefs,
                        dataStoreManager = dataStoreManager
                    )
                }
            }
        }
    }
}