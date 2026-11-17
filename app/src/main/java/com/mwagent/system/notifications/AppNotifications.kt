package com.mwagent.system.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.mwagent.MainActivity
import com.mwagent.R
import com.mwagent.conversation.ApprovalDecision
import com.mwagent.conversation.BackgroundWorkService

enum class AppChannel(val id: String, @StringRes val title: Int, val importance: Int) {
    BACKGROUND_WORK("background_work", R.string.notification_channel_work, NotificationManager.IMPORTANCE_LOW),
    APPROVALS("approvals", R.string.notification_channel_approvals, NotificationManager.IMPORTANCE_HIGH),
    REMINDERS("reminders", R.string.notification_channel_reminders, NotificationManager.IMPORTANCE_HIGH),
    AUTOMATIONS("automations", R.string.notification_channel_automations, NotificationManager.IMPORTANCE_HIGH),
    WATCHING("watching", R.string.notification_channel_watching, NotificationManager.IMPORTANCE_MIN),
    RESULTS("results", R.string.notification_channel_results, NotificationManager.IMPORTANCE_HIGH),
}

object NotificationIds {
    const val WORK = 1001
    const val APPROVAL = 1002
    const val RESULT = 1003
    const val OPEN_APP_REQUEST = 2
    const val STOP_ALL_REQUEST = 1
}

object AppNotifications {
    const val TAG = "Notifications"
    const val ACTION_APPROVAL = "com.mwagent.action.APPROVAL"
    const val EXTRA_CHAT_ID = "com.mwagent.extra.NOTIFICATION_CHAT_ID"
    const val EXTRA_CALL_ID = "com.mwagent.extra.NOTIFICATION_CALL_ID"
    const val EXTRA_DECISION = "com.mwagent.extra.NOTIFICATION_DECISION"
    const val ACTION_ANSWER = "com.mwagent.action.ANSWER"
    const val EXTRA_PROMPT_ID = "com.mwagent.extra.NOTIFICATION_PROMPT_ID"
    const val EXTRA_ANSWER = "com.mwagent.extra.NOTIFICATION_ANSWER"
    const val REPLY_KEY = "typed_answer"
    private const val APPROVAL_URI = "mwagent://approval/"
    private const val ANSWER_URI = "mwagent://answer/"

    fun registerChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannels(AppChannel.entries.map { NotificationChannel(it.id, context.getString(it.title), it.importance) })
    }

    fun canPost(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    fun openChatIntent(context: Context, chatId: String?, requestCode: Int): PendingIntent =
        PendingIntent.getActivity(context, requestCode, MainActivity.chatIntent(context, chatId), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    fun stopAllIntent(context: Context): PendingIntent =
        PendingIntent.getService(
            context, NotificationIds.STOP_ALL_REQUEST,
            Intent(context, BackgroundWorkService::class.java).setAction(BackgroundWorkService.ACTION_STOP_ALL),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    fun answerIntent(context: Context, chatId: String, promptId: String, option: String?, slot: Int): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java)
            .setAction(ACTION_ANSWER)
            .setData("$ANSWER_URI$promptId/$slot".toUri())
            .putExtra(EXTRA_CHAT_ID, chatId)
            .putExtra(EXTRA_PROMPT_ID, promptId)
            .apply { option?.let { putExtra(EXTRA_ANSWER, it) } }
        val mutability = if (option == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or mutability)
    }

    fun approvalIntent(context: Context, chatId: String, callId: String, decision: ApprovalDecision): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java)
            .setAction(ACTION_APPROVAL)
            .setData("$APPROVAL_URI$chatId/$callId/${decision.name}".toUri())
            .putExtra(EXTRA_CHAT_ID, chatId)
            .putExtra(EXTRA_CALL_ID, callId)
            .putExtra(EXTRA_DECISION, decision.name)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}
