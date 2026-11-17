package com.mwagent.feature.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.mwagent.data.settings.ShellMode
import com.mwagent.feature.settings.DevicePermission
import com.mwagent.system.control.PhoneControl
import com.mwagent.system.files.DeviceFiles
import com.mwagent.system.notifications.AppNotifications
import com.mwagent.system.shell.AccessStatus
import com.mwagent.system.shell.ShellAccess

class PermissionProbe(
    private val context: Context,
    private val phoneControl: PhoneControl,
    private val files: DeviceFiles,
    private val access: ShellAccess,
) {
    suspend fun missing(needs: SurveyNeeds): List<DevicePermission> = buildList {
        if (!AppNotifications.canPost(context)) add(DevicePermission.NOTIFICATIONS)
        if (needs.microphone && !isGranted(Manifest.permission.RECORD_AUDIO)) add(DevicePermission.MICROPHONE)
        if (needs.phoneControl && !phoneControl.isReady()) add(DevicePermission.PHONE_CONTROL)
        if (needs.allFiles && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !files.hasSharedStorageAccess()) add(DevicePermission.ALL_FILES)
        if (needs.termux && access.status(ShellMode.TERMUX, probeRoot = false) != AccessStatus.READY) add(DevicePermission.TERMUX)
    }

    private fun isGranted(permission: String) = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}
