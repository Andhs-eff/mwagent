package com.mwagent.feature.onboarding.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mwagent.R
import com.mwagent.feature.onboarding.AgentCloud
import com.mwagent.feature.onboarding.OnboardingMotion
import com.mwagent.feature.onboarding.OnboardingState
import com.mwagent.feature.onboarding.PageScaffold
import com.mwagent.feature.onboarding.reveal
import com.mwagent.data.chat.ChatTint
import com.mwagent.data.settings.AgentShape
import com.mwagent.feature.settings.AgentShapePicker
import com.mwagent.feature.settings.AgentTintPicker
import com.mwagent.ui.components.AgentAvatar
import com.mwagent.ui.components.FormField
import com.mwagent.ui.components.SectionHeader

@Composable
fun AgentsPage(state: OnboardingState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = OnboardingMotion.ContentPadding, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.onboarding_agents_title),
            style = MaterialTheme.typography.displaySmallEmphasized,
            textAlign = TextAlign.Center,
            modifier = Modifier.reveal(0),
        )
        Box(Modifier.weight(1f).fillMaxWidth().reveal(1), contentAlignment = Alignment.Center) {
            AgentCloud(state.cluster, interactive = true, modifier = Modifier.widthIn(max = CLOUD_MAX_WIDTH).fillMaxWidth().aspectRatio(1f))
        }
        Text(
            stringResource(R.string.onboarding_agents_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.reveal(2),
        )
    }
}

@Composable
fun FirstAgentPage(state: OnboardingState, onShape: (AgentShape) -> Unit, onTint: (ChatTint) -> Unit, onName: (String) -> Unit) {
    PageScaffold(
        title = stringResource(R.string.onboarding_first_title),
        body = stringResource(R.string.onboarding_first_body),
    ) {
        Box(Modifier.fillMaxWidth().padding(vertical = 8.dp).reveal(2), contentAlignment = Alignment.Center) {
            AgentAvatar(isWorking = false, size = PREVIEW_SIZE, look = state.look, interactive = true)
        }
        FormField(
            value = state.agentName,
            onValueChange = onName,
            label = stringResource(R.string.onboarding_first_name),
            modifier = Modifier.fillMaxWidth().reveal(3),
        )
        Column(Modifier.reveal(4), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionHeader(stringResource(R.string.agent_shape))
            AgentShapePicker(selected = { it == state.look.shape }, tint = state.look.tint, onToggle = onShape)
        }
        Column(Modifier.reveal(5), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionHeader(stringResource(R.string.agent_color))
            AgentTintPicker(selected = state.look.tint, onSelect = onTint)
        }
    }
}

private val CLOUD_MAX_WIDTH = 360.dp
private val PREVIEW_SIZE = 120.dp
