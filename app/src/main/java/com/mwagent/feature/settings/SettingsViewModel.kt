package com.mwagent.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwagent.data.settings.AppSettings
import com.mwagent.data.settings.AssistantSettings
import com.mwagent.data.settings.ChatSettings
import com.mwagent.data.settings.InterfaceSettings
import com.mwagent.data.settings.NotificationSettings
import com.mwagent.data.settings.SettingsRepository
import com.mwagent.data.settings.ToolSettings
import com.mwagent.data.settings.VoiceSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val settings: StateFlow<AppSettings?> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), null)

    fun updateChat(transform: (ChatSettings) -> ChatSettings) {
        viewModelScope.launch { repository.updateChat(transform) }
    }

    fun updateInterface(transform: (InterfaceSettings) -> InterfaceSettings) {
        viewModelScope.launch { repository.updateInterface(transform) }
    }

    fun updateNotifications(transform: (NotificationSettings) -> NotificationSettings) {
        viewModelScope.launch { repository.updateNotifications(transform) }
    }

    fun updateAssistant(transform: (AssistantSettings) -> AssistantSettings) {
        viewModelScope.launch { repository.updateAssistant(transform) }
    }

    fun updateVoice(transform: (VoiceSettings) -> VoiceSettings) {
        viewModelScope.launch { repository.updateVoice(transform) }
    }

    fun updateTools(transform: (ToolSettings) -> ToolSettings) {
        viewModelScope.launch { repository.updateTools(transform) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
