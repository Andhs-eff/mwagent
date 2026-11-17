package com.mwagent.feature.mode

import com.mwagent.data.settings.ModeSettings
import com.mwagent.data.settings.ToolPermission
import com.mwagent.data.settings.ToolSettings
import com.mwagent.tools.phone.PhoneActionTool
import com.mwagent.tools.phone.PhoneHandoffTool
import com.mwagent.tools.phone.PhoneScreenTool
import com.mwagent.tools.phone.PhoneScreenshotTool
import com.mwagent.tools.skills.SkillDeleteTool
import com.mwagent.tools.skills.SkillExportTool
import com.mwagent.tools.skills.SkillImportTool
import com.mwagent.tools.skills.SkillListTool
import com.mwagent.tools.skills.SkillRunTool
import com.mwagent.tools.skills.SkillSaveTool
import com.mwagent.tools.system.RunCommandTool
import com.mwagent.tools.system.RunPythonTool

private val PHONE_CONTROL_TOOLS = setOf(PhoneScreenTool.NAME, PhoneScreenshotTool.NAME, PhoneActionTool.NAME, PhoneHandoffTool.NAME)
private val SHELL_AND_FILES_TOOLS = setOf(
    RunCommandTool.NAME,
    RunPythonTool.NAME,
    SkillSaveTool.NAME,
    SkillRunTool.NAME,
    SkillListTool.NAME,
    SkillDeleteTool.NAME,
    SkillExportTool.NAME,
    SkillImportTool.NAME,
)

fun ToolSettings.followingMode(previous: ModeSettings, current: ModeSettings): ToolSettings =
    following(PHONE_CONTROL_TOOLS, previous.phoneControl, current.phoneControl)
        .following(SHELL_AND_FILES_TOOLS, previous.shellAndFiles, current.shellAndFiles)

private fun ToolSettings.following(tools: Set<String>, wasOn: Boolean, isOn: Boolean): ToolSettings = when {
    wasOn == isOn -> this
    isOn -> copy(permissions = permissions.filterNot { (name, permission) -> name in tools && permission == ToolPermission.OFF })
    else -> copy(permissions = permissions + tools.filter { it !in permissions }.associateWith { ToolPermission.OFF })
}
