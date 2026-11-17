package com.mwagent.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mwagent.App
import com.mwagent.R
import com.mwagent.data.settings.CustomSearch
import com.mwagent.data.settings.FetchBackend
import com.mwagent.data.settings.FetchSettings
import com.mwagent.data.settings.HeaderEntry
import com.mwagent.data.settings.SearchBackend
import com.mwagent.data.settings.SearchSettings
import com.mwagent.data.settings.ToolSettings
import com.mwagent.ui.components.rememberSectionFolds
import com.mwagent.ui.components.SaveAction
import com.mwagent.ui.components.SearchField
import com.mwagent.ui.components.SettingsScaffold
import com.mwagent.ui.components.ToolCategory
import kotlinx.coroutines.launch

@Composable
fun ToolSettingsScreen(onBack: () -> Unit) {
    val container = (LocalContext.current.applicationContext as App).container
    val scope = rememberCoroutineScope()
    var tools by remember { mutableStateOf<ToolSettings?>(null) }
    var saved by remember { mutableStateOf<ToolSettings?>(null) }

    LaunchedEffect(Unit) { container.settings.current().tools.let { saved = it.trimmed(); tools = it } }

    val save = tools?.let { current ->
        val draft = current.trimmed()
        SaveAction(isDirty = draft != saved, canSave = current.search.isComplete() && current.fetch.isComplete()) {
            scope.launch {
                container.settings.updateTools { persisted -> draft.copy(access = persisted.access, phoneUse = persisted.phoneUse) }
                saved = draft
                onBack()
            }
        }
    }

    val folds = rememberSectionFolds(*ToolCategory.entries.map { it.name }.toTypedArray())
    var query by rememberSaveable { mutableStateOf("") }
    val anchors = remember { SectionAnchors() }
    val scroll = rememberScrollState()
    val spyThreshold = with(LocalDensity.current) { SPY_OFFSET.toPx() }
    val currentGroup by remember { derivedStateOf { anchors.selected(scroll, spyThreshold) } }
    val mcpStates by container.mcp.states.collectAsStateWithLifecycle()
    val allTools = remember(mcpStates) { container.tools.all }

    SettingsScaffold(stringResource(R.string.settings_tools), onBack, save = save) { padding ->
        val current = tools ?: return@SettingsScaffold
        Column(Modifier.fillMaxSize().padding(padding)) {
            SearchField(query, { query = it }, stringResource(R.string.tools_search_hint), Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            ToolJumpBar(anchors.groups, currentGroup, onSelect = { group -> scope.launch { anchors.scrollTo(group, scroll) } })
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(anchors.viewport())
                    .verticalScroll(scroll)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
            ) {
                ToolPermissionGroups(
                    tools = allTools,
                    permissionOf = { container.tools.permission(it, current) },
                    onChange = { tool, permission -> tools = current.withPermission(tool.spec.name, permission) },
                    folds = folds,
                    query = query,
                    sectionModifier = { category -> anchors.anchor(category) },
                ) { category ->
                    if (category == ToolCategory.WEB) {
                        CardGroup(Modifier.padding(top = SERVICE_GROUP_GAP)) { searchServiceCards(current.search, folds) { tools = current.copy(search = it) } }
                        CardGroup(Modifier.padding(top = SERVICE_GROUP_GAP)) { fetchServiceCards(current.fetch) { tools = current.copy(fetch = it) } }
                        CardGroup(Modifier.padding(top = SERVICE_GROUP_GAP)) { weatherServiceCards(current.weather) { tools = current.copy(weather = it) } }
                    }
                }
            }
        }
    }
}

private fun FetchSettings.isComplete(): Boolean = backend != FetchBackend.CUSTOM || customUrl.isNotBlank()

private fun SearchSettings.isComplete(): Boolean = when (backend) {
    SearchBackend.HTML_SCRAPE -> true
    SearchBackend.SEARXNG -> searxngUrl.isNotBlank()
    SearchBackend.BRAVE_API -> apiKey.isNotBlank()
    SearchBackend.CUSTOM -> custom.isComplete
}

private fun ToolSettings.trimmed() = copy(
    search = search.copy(searxngUrl = search.searxngUrl.trim(), apiKey = search.apiKey.trim(), custom = search.custom.trimmed()),
    weather = weather.copy(forecastUrl = weather.forecastUrl.trim(), geocodingUrl = weather.geocodingUrl.trim()),
    fetch = fetch.copy(jinaApiKey = fetch.jinaApiKey.trim(), customUrl = fetch.customUrl.trim(), customApiKey = fetch.customApiKey.trim()),
)

private fun CustomSearch.trimmed() = copy(
    url = url.trim(),
    apiKey = apiKey.trim(),
    headers = headers.filter { it.name.isNotBlank() }.map { HeaderEntry(it.name.trim(), it.value.trim()) },
    body = body.trim(),
    resultsPath = resultsPath.trim(),
    titleField = titleField.trim(),
    linkField = linkField.trim(),
    snippetFields = snippetFields.trim(),
)

private val SPY_OFFSET = 96.dp
private val SERVICE_GROUP_GAP = 12.dp
