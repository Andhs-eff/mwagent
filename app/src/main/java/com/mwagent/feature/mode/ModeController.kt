package com.mwagent.feature.mode

import com.mwagent.data.settings.ModeSettings
import com.mwagent.data.settings.SettingsRepository

class ModeController(private val settings: SettingsRepository) {
    suspend fun update(transform: (ModeSettings) -> ModeSettings) {
        val before = settings.current().mode
        val after = transform(before)
        settings.updateMode { after }
        if (before.phoneControl != after.phoneControl || before.shellAndFiles != after.shellAndFiles) {
            settings.updateTools { it.followingMode(before, after) }
        }
    }
}
