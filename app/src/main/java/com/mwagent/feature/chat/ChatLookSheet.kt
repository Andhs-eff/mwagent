package com.mwagent.feature.chat

import com.mwagent.ui.components.AppBottomSheet
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.mwagent.R
import com.mwagent.data.chat.ChatGlyph
import com.mwagent.data.chat.ChatLook
import com.mwagent.data.chat.ChatShape
import com.mwagent.data.chat.ChatTint
import com.mwagent.ui.components.ChatBadge
import com.mwagent.ui.components.SectionHeader
import com.mwagent.ui.components.GlyphGrid
import com.mwagent.ui.components.Option
import com.mwagent.ui.components.OptionGrid
import com.mwagent.ui.components.TintGrid
import com.mwagent.ui.components.icon
import com.mwagent.ui.components.polygon
import com.mwagent.ui.haptics.rememberHaptics
import com.mwagent.ui.haptics.clickHaptics

@Composable
fun ChatLookSheet(look: ChatLook, onChange: (ChatLook) -> Unit, onDismiss: () -> Unit) {
    val haptics = rememberHaptics()
    val pick = { updated: ChatLook ->
        haptics.tick()
        onChange(updated)
    }
    AppBottomSheet(onDismissRequest = onDismiss, modifier = Modifier.clickHaptics()) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).navigationBarsPadding(),
        ) {
            Box(Modifier.fillMaxWidth().padding(bottom = 8.dp), contentAlignment = Alignment.Center) {
                ChatBadge(look, size = PREVIEW_SIZE)
            }
            SectionHeader(stringResource(R.string.chat_look_icon))
            GlyphGrid(look.glyph) { pick(look.copy(glyph = it)) }
            SectionHeader(stringResource(R.string.chat_look_color))
            TintGrid(look.tint, look.glyph.icon) { pick(look.copy(tint = it)) }
            SectionHeader(stringResource(R.string.chat_look_shape))
            OptionGrid(Modifier.padding(bottom = 24.dp)) {
                ChatShape.entries.forEach { shape ->
                    Option(selected = shape == look.shape, onClick = { pick(look.copy(shape = shape)) }) {
                        Surface(
                            shape = shape.polygon.toShape(),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp),
                            content = {},
                        )
                    }
                }
            }
            Text(
                stringResource(R.string.chat_look_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp, bottom = 24.dp),
            )
        }
    }
}

private val PREVIEW_SIZE = 72.dp
