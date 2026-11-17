package com.mwagent.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mwagent.R
import com.mwagent.data.db.MemoryTier
import com.mwagent.data.memory.MemoryStore
import com.mwagent.data.settings.EmbeddingKind
import com.mwagent.ui.components.SaveAction
import com.mwagent.ui.components.SectionHeader
import com.mwagent.ui.components.SettingsScaffold
import com.mwagent.ui.components.appViewModel

@Composable
fun MemorySettingsScreen(onBack: () -> Unit) {
    val viewModel = appViewModel { MemoryViewModel(it.settings, it.memories, it.modelCatalogClient) }
    val embedding by viewModel.embedding.collectAsStateWithLifecycle()
    val isDirty by viewModel.isDirty.collectAsStateWithLifecycle()
    val probe by viewModel.probe.collectAsStateWithLifecycle()
    val models by viewModel.models.collectAsStateWithLifecycle()
    val lists by viewModel.lists.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<MemoryDraft?>(null) }
    var pickingModel by rememberSaveable { mutableStateOf(false) }

    val save = embedding?.let { current ->
        SaveAction(isDirty, canSave = current.embeddingKind == EmbeddingKind.OFF || current.embeddingsReady) { viewModel.save(onBack) }
    }

    SettingsScaffold(stringResource(R.string.settings_memory), onBack, save = save) { padding ->
        val current = embedding ?: return@SettingsScaffold
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = settingsPagePadding(padding),
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
        ) {
            item { SectionHeader(stringResource(R.string.memory_section_search), topPadding = 8.dp) }
            item { SectionCaption(stringResource(R.string.memory_search_hint)) }
            item {
                CardGroup {
                    embeddingCards(
                        settings = current,
                        probe = probe,
                        onKind = viewModel::selectKind,
                        onEdit = viewModel::edit,
                        onPickModel = { pickingModel = true },
                        onProbe = viewModel::probeEmbeddings,
                    )
                }
            }
            memorySection(
                tier = MemoryTier.INSTANT,
                hint = { stringResource(R.string.memory_instant_hint, lists.instant.size, MemoryStore.MAX_INSTANT) },
                memories = lists.instant,
                onOpen = { editing = it },
            )
            memorySection(
                tier = MemoryTier.LONG_TERM,
                hint = { stringResource(R.string.memory_long_term_hint) },
                memories = lists.longTerm,
                onOpen = { editing = it },
            )
        }
        if (pickingModel) {
            LaunchedEffect(Unit) { viewModel.loadModels() }
            EmbeddingModelSheet(
                selectedId = current.model,
                models = models,
                onPick = { id -> viewModel.edit { it.copy(embeddingModel = id) } },
                onRefresh = { viewModel.loadModels(force = true) },
                onDismiss = { pickingModel = false },
            )
        }
    }

    editing?.let { draft ->
        MemoryEditorDialog(
            initial = draft,
            onSubmit = viewModel::submit,
            onDelete = draft.id?.let { id -> { viewModel.delete(id); editing = null } },
            onDismiss = { editing = null },
        )
    }
}
