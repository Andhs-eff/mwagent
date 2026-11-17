package com.mwagent.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mwagent.App
import com.mwagent.feature.onboarding.OnboardingScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mwagent.feature.chat.ChatScreen
import com.mwagent.feature.main.MainScreen
import com.mwagent.feature.mode.ModeScreen
import com.mwagent.feature.settings.AgentEditorScreen
import com.mwagent.feature.settings.AssistantInvokeSettingsScreen
import com.mwagent.feature.settings.AgentsScreen
import com.mwagent.data.files.LibraryLocation
import com.mwagent.data.files.LibraryRoot
import com.mwagent.feature.files.FolderScreen
import com.mwagent.feature.files.MediaScreen
import com.mwagent.feature.playground.PlaygroundScreen
import com.mwagent.feature.settings.AboutScreen
import com.mwagent.feature.settings.AppearanceScreen
import com.mwagent.feature.settings.ChatSettingsScreen
import com.mwagent.feature.settings.TelegramSettingsScreen
import com.mwagent.feature.settings.ModelsScreen
import com.mwagent.feature.settings.RemindersScreen
import com.mwagent.feature.settings.AutomationsScreen
import com.mwagent.feature.settings.DeviceAccessScreen
import com.mwagent.feature.settings.PhoneUseSettingsScreen
import com.mwagent.feature.settings.MemorySettingsScreen
import com.mwagent.feature.settings.NetworkSettingsScreen
import com.mwagent.feature.settings.NotificationSettingsScreen
import com.mwagent.feature.settings.ProviderEditorScreen
import com.mwagent.feature.settings.SettingsDestinations
import com.mwagent.feature.settings.ToolSettingsScreen
import com.mwagent.feature.settings.McpServerEditorScreen
import com.mwagent.feature.settings.McpServersScreen
import com.mwagent.feature.settings.VoiceProviderEditorScreen
import com.mwagent.feature.settings.VoiceSettingsScreen
import com.mwagent.ui.haptics.clickHaptics
import androidx.compose.ui.Modifier
import com.mwagent.feature.home.ChatFilter
import com.mwagent.feature.home.HomeScreen

@Composable
fun AppNavHost(
    chatToOpen: ChatRoute?,
    onChatOpened: () -> Unit,
    target: LaunchTarget? = null,
    onTargetHandled: () -> Unit = {},
) {
    val container = (LocalContext.current.applicationContext as App).container
    val onboarded by remember(container) { container.settings.settings.map { it.mode.onboarded }.distinctUntilChanged() }
        .collectAsStateWithLifecycle(initialValue = null)
    val start = remember(onboarded != null) { onboarded?.let { if (it) HomeRoute else OnboardingRoute() } }
    if (start == null) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface))
        return
    }
    AppNavGraph(start, chatToOpen, onChatOpened, target, onTargetHandled)
}

@Composable
private fun AppNavGraph(
    start: Any,
    chatToOpen: ChatRoute?,
    onChatOpened: () -> Unit,
    target: LaunchTarget?,
    onTargetHandled: () -> Unit,
) {
    val container = (LocalContext.current.applicationContext as App).container
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val backMotion = rememberBackMotion(navController)
    var homeRequest by remember { mutableStateOf<LaunchTarget?>(null) }
    LaunchedEffect(chatToOpen) {
        val route = chatToOpen ?: return@LaunchedEffect
        navController.navigate(route) { popUpTo(HomeRoute) }
        onChatOpened()
    }
    LaunchedEffect(target) {
        val requested = target ?: return@LaunchedEffect
        navController.popBackStack(HomeRoute, inclusive = false)
        homeRequest = requested
        onTargetHandled()
    }
    NavHost(
        navController = navController,
        startDestination = start,
        modifier = Modifier.backStage(backMotion).clickHaptics(),
        enterTransition = NavTransitions.enter,
        exitTransition = NavTransitions.exit,
        popEnterTransition = NavTransitions.popEnter,
        popExitTransition = NavTransitions.popExit,
        predictivePopEnterTransition = NavTransitions.predictivePopEnter,
        predictivePopExitTransition = NavTransitions.predictivePopExit,
    ) {
        screen<HomeRoute>(backMotion) {
            MainScreen(
                request = homeRequest,
                onRequestHandled = { homeRequest = null },
                onOpenChat = { navController.navigate(ChatRoute(it)) },
                onOpenMessage = { chatId, messageId -> navController.navigate(ChatRoute(chatId, focusMessageId = messageId)) },
                onOpenFolder = { root -> navController.navigate(FolderRoute(root.name)) },
                onOpenMedia = { navController.navigate(MediaRoute) },
                onOpenCollection = { filter ->
                    when (filter) {
                        is ChatFilter.Folder -> navController.navigate(ChatCollectionRoute(CollectionKind.FOLDER, filter.id))
                        ChatFilter.Archive -> navController.navigate(ChatCollectionRoute(CollectionKind.ARCHIVE))
                        ChatFilter.Trash -> navController.navigate(ChatCollectionRoute(CollectionKind.TRASH))
                        ChatFilter.All -> Unit
                    }
                },
                settingsDestinations = SettingsDestinations(
                    models = { navController.navigate(ModelsRoute) },
                    appearance = { navController.navigate(AppearanceRoute) },
                    chat = { navController.navigate(ChatSettingsRoute) },
                    about = { navController.navigate(AboutRoute) },
                    agent = { navController.navigate(AgentsRoute) },
                    memory = { navController.navigate(MemorySettingsRoute) },
                    network = { navController.navigate(NetworkSettingsRoute) },
                    tools = { navController.navigate(ToolSettingsRoute) },
                    reminders = { navController.navigate(RemindersRoute) },
                    automations = { navController.navigate(AutomationsRoute) },
                    telegram = { navController.navigate(TelegramRoute) },
                    voice = { navController.navigate(VoiceSettingsRoute) },
                    access = { navController.navigate(DeviceAccessRoute) },
                    notifications = { navController.navigate(NotificationSettingsRoute) },
                    assistantInvoke = { navController.navigate(AssistantInvokeRoute) },
                    phoneUse = { navController.navigate(PhoneUseRoute) },
                    mcp = { navController.navigate(McpServersRoute) },
                    mode = { navController.navigate(ModeRoute) },
                    editAgent = { navController.navigate(AgentEditorRoute(it)) },
                ),
            )
        }
        screen<ChatCollectionRoute>(backMotion) { entry ->
            val route = entry.toRoute<ChatCollectionRoute>()
            HomeScreen(
                onOpenChat = { navController.navigate(ChatRoute(it)) },
                onOpenMessage = { chatId, messageId -> navController.navigate(ChatRoute(chatId, focusMessageId = messageId)) },
                onOpenSettings = navController::popBackStack,
                collection = when (route.kind) {
                    CollectionKind.FOLDER -> ChatFilter.Folder(route.folderId.orEmpty())
                    CollectionKind.ARCHIVE -> ChatFilter.Archive
                    CollectionKind.TRASH -> ChatFilter.Trash
                },
                onBack = navController::popBackStack,
            )
        }
        screen<ChatRoute>(backMotion) { entry ->
            val route = entry.toRoute<ChatRoute>()
            ChatScreen(
                chatId = route.chatId,
                focusMessageId = route.focusMessageId,
                listen = route.listen,
                onBack = navController::popBackStack,
                onOpenChat = { navController.navigate(ChatRoute(it)) },
            )
        }
        screen<ProviderEditorRoute>(backMotion) { entry ->
            ProviderEditorScreen(providerId = entry.toRoute<ProviderEditorRoute>().providerId, onDone = navController::popBackStack)
        }
        screen<AgentsRoute>(backMotion) {
            AgentsScreen(onBack = navController::popBackStack, onEditAgent = { navController.navigate(AgentEditorRoute(it)) })
        }
        screen<AgentEditorRoute>(backMotion) { entry ->
            AgentEditorScreen(agentId = entry.toRoute<AgentEditorRoute>().agentId, onDone = navController::popBackStack)
        }
        screen<ModeRoute>(backMotion) {
            ModeScreen(
                onBack = navController::popBackStack,
                onRetakeSurvey = { navController.navigate(OnboardingRoute(retake = true)) },
                onRetakeOnboarding = { navController.navigate(OnboardingRoute(replay = true)) },
            )
        }
        screen<AutomationsRoute>(backMotion) { AutomationsScreen(onBack = navController::popBackStack) }
        screen<TelegramRoute>(backMotion) { TelegramSettingsScreen(onBack = navController::popBackStack) }
        screen<OnboardingRoute>(backMotion) { entry ->
            val route = entry.toRoute<OnboardingRoute>()
            OnboardingScreen(
                retake = route.retake,
                onCustomProvider = { navController.navigate(ProviderEditorRoute()) },
                onFinished = { openChat ->
                    if (route.retake || route.replay) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(HomeRoute) { popUpTo<OnboardingRoute> { inclusive = true } }
                        if (openChat) scope.launch { if (container.settings.current().providers.isNotEmpty()) navController.navigate(ChatRoute()) }
                    }
                },
            )
        }
        screen<MemorySettingsRoute>(backMotion) { MemorySettingsScreen(onBack = navController::popBackStack) }
        screen<NetworkSettingsRoute>(backMotion) { NetworkSettingsScreen(onBack = navController::popBackStack) }
        screen<ToolSettingsRoute>(backMotion) { ToolSettingsScreen(onBack = navController::popBackStack) }
        screen<RemindersRoute>(backMotion) { RemindersScreen(onBack = navController::popBackStack) }
        screen<DeviceAccessRoute>(backMotion) { DeviceAccessScreen(onBack = navController::popBackStack) }
        screen<NotificationSettingsRoute>(backMotion) { NotificationSettingsScreen(onBack = navController::popBackStack) }
        screen<AssistantInvokeRoute>(backMotion) { AssistantInvokeSettingsScreen(onBack = navController::popBackStack) }
        screen<PhoneUseRoute>(backMotion) { PhoneUseSettingsScreen(onBack = navController::popBackStack) }
        screen<AppearanceRoute>(backMotion) { AppearanceScreen(onBack = navController::popBackStack) }
        screen<ChatSettingsRoute>(backMotion) { ChatSettingsScreen(onBack = navController::popBackStack) }
        screen<AboutRoute>(backMotion) {
            AboutScreen(
                onBack = navController::popBackStack,
                onSecret = { navController.navigate(PlaygroundRoute) },
            )
        }
        screen<PlaygroundRoute>(backMotion) { PlaygroundScreen() }
        screen<MediaRoute>(backMotion) { MediaScreen(onBack = navController::popBackStack) }
        screen<FolderRoute>(backMotion) { entry ->
            val route = entry.toRoute<FolderRoute>()
            FolderScreen(
                location = route.toLocation(),
                onOpen = { navController.navigate(it.toRoute()) },
                onBack = navController::popBackStack,
            )
        }
        screen<ModelsRoute>(backMotion) {
            ModelsScreen(
                onBack = navController::popBackStack,
                onEditProvider = { navController.navigate(ProviderEditorRoute(it)) },
            )
        }
        screen<VoiceSettingsRoute>(backMotion) {
            VoiceSettingsScreen(
                onBack = navController::popBackStack,
                onEditProvider = { navController.navigate(VoiceProviderEditorRoute(it)) },
            )
        }
        screen<McpServersRoute>(backMotion) {
            McpServersScreen(
                onBack = navController::popBackStack,
                onEditServer = { navController.navigate(McpServerEditorRoute(it)) },
            )
        }
        screen<McpServerEditorRoute>(backMotion) { entry ->
            McpServerEditorScreen(serverId = entry.toRoute<McpServerEditorRoute>().serverId, onDone = navController::popBackStack)
        }
        screen<VoiceProviderEditorRoute>(backMotion) { entry ->
            VoiceProviderEditorScreen(providerId = entry.toRoute<VoiceProviderEditorRoute>().providerId, onDone = navController::popBackStack)
        }
    }
}

private fun FolderRoute.toLocation() = LibraryLocation(
    root = LibraryRoot.valueOf(root),
    segments = path.split(PATH_SEPARATOR).filter { it.isNotEmpty() },
    fromChats = fromChats,
)

private fun LibraryLocation.toRoute() = FolderRoute(root.name, segments.joinToString(PATH_SEPARATOR), fromChats)

private const val PATH_SEPARATOR = "/"

private inline fun <reified T : Any> NavGraphBuilder.screen(
    motion: BackMotion,
    crossinline content: @Composable (NavBackStackEntry) -> Unit,
) = composable<T> { entry -> BackScreen(entry, motion) { content(entry) } }
