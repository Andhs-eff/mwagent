package com.mwagent.conversation

import com.mwagent.mcp.McpTool
import com.mwagent.tools.apps.AddCalendarEventTool
import com.mwagent.tools.apps.ComposeMessageTool
import com.mwagent.tools.apps.DialTool
import com.mwagent.tools.apps.ShareTool
import com.mwagent.tools.automation.AddWatchTool
import com.mwagent.tools.automation.CancelTaskTool
import com.mwagent.tools.automation.CheckSubagentTool
import com.mwagent.tools.automation.ListTasksTool
import com.mwagent.tools.automation.ListWatchesTool
import com.mwagent.tools.automation.RemoveWatchTool
import com.mwagent.tools.automation.ScheduleTaskTool
import com.mwagent.tools.automation.StartSubagentTool
import com.mwagent.tools.chats.ExportChatTool
import com.mwagent.tools.chats.ListChatsTool
import com.mwagent.tools.chats.ReadChatTool
import com.mwagent.tools.chats.SearchMessagesTool
import com.mwagent.tools.contacts.CallLogTool
import com.mwagent.tools.contacts.FindContactsTool
import com.mwagent.tools.files.ListFilesTool
import com.mwagent.tools.files.ReadFileTool
import com.mwagent.tools.files.SearchLocalTool
import com.mwagent.tools.files.SendFilesTool
import com.mwagent.tools.files.WriteFileTool
import com.mwagent.tools.location.LocationTool
import com.mwagent.tools.media.FindPhotosTool
import com.mwagent.tools.media.GetPhotoTool
import com.mwagent.tools.media.SaveToDownloadsTool
import com.mwagent.tools.memory.MemoryDeleteTool
import com.mwagent.tools.memory.MemorySaveTool
import com.mwagent.tools.memory.MemorySearchTool
import com.mwagent.tools.memory.MemoryUpdateTool
import com.mwagent.tools.notifications.ReadNotificationsTool
import com.mwagent.tools.phone.PhoneActionTool
import com.mwagent.tools.phone.PhoneScreenTool
import com.mwagent.tools.phone.PhoneScreenshotTool
import com.mwagent.tools.skills.SkillDeleteTool
import com.mwagent.tools.skills.SkillExportTool
import com.mwagent.tools.skills.SkillImportTool
import com.mwagent.tools.skills.SkillListTool
import com.mwagent.tools.skills.SkillRunTool
import com.mwagent.tools.skills.SkillSaveTool
import com.mwagent.tools.system.CopyToClipboardTool
import com.mwagent.tools.system.ReadClipboardTool
import com.mwagent.tools.system.RunCommandTool
import com.mwagent.tools.system.RunPythonTool
import com.mwagent.tools.telegram.TelegramChatsTool
import com.mwagent.tools.telegram.TelegramMeTool
import com.mwagent.tools.telegram.TelegramMessagesTool
import com.mwagent.tools.telegram.TelegramSearchTool
import com.mwagent.tools.telegram.TelegramSendTool

object ApprovalRisk {
    private val SENSITIVE_TOOLS = setOf(
        RunCommandTool.NAME,
        RunPythonTool.NAME,
        WriteFileTool.NAME,
        PhoneScreenTool.NAME,
        PhoneScreenshotTool.NAME,
        PhoneActionTool.NAME,
        SkillSaveTool.NAME,
        SkillRunTool.NAME,
        SkillDeleteTool.NAME,
        SkillImportTool.NAME,
        TelegramSendTool.NAME,
        ScheduleTaskTool.NAME,
        AddWatchTool.NAME,
        StartSubagentTool.NAME,
        SaveToDownloadsTool.NAME,
        CopyToClipboardTool.NAME,
        ShareTool.NAME,
        ComposeMessageTool.NAME,
        DialTool.NAME,
        AddCalendarEventTool.NAME,
    )

    private val PRIVATE_TOOLS = setOf(
        ListFilesTool.NAME,
        ReadFileTool.NAME,
        SendFilesTool.NAME,
        SearchLocalTool.NAME,
        MemorySaveTool.NAME,
        MemoryUpdateTool.NAME,
        MemoryDeleteTool.NAME,
        MemorySearchTool.NAME,
        ListChatsTool.NAME,
        ReadChatTool.NAME,
        SearchMessagesTool.NAME,
        ExportChatTool.NAME,
        TelegramMeTool.NAME,
        TelegramChatsTool.NAME,
        TelegramMessagesTool.NAME,
        TelegramSearchTool.NAME,
        ReadNotificationsTool.NAME,
        FindContactsTool.NAME,
        CallLogTool.NAME,
        FindPhotosTool.NAME,
        GetPhotoTool.NAME,
        ReadClipboardTool.NAME,
        LocationTool.NAME,
        SkillListTool.NAME,
        SkillExportTool.NAME,
        ListTasksTool.NAME,
        CancelTaskTool.NAME,
        ListWatchesTool.NAME,
        RemoveWatchTool.NAME,
        CheckSubagentTool.NAME,
    )

    fun requiresUnlock(toolName: String): Boolean = toolName in SENSITIVE_TOOLS || McpTool.isMcpName(toolName)

    fun requiresConfirmationWhenLocked(toolName: String): Boolean = requiresUnlock(toolName) || toolName in PRIVATE_TOOLS
}
