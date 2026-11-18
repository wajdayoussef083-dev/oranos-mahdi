package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.NavigationTab
import com.example.domain.model.Screen
import com.example.ui.components.OranosBottomNav
import com.example.ui.components.OranosTopBar
import com.example.ui.screens.ChatsArchiveScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.TaskDetailScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.OranosTheme
import com.example.viewmodel.OranosViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OranosTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    OranosApp()
                }
            }
        }
    }
}

@Composable
fun OranosApp(
    viewModel: OranosViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val languageMode by viewModel.languageMode.collectAsState()
    val isKeyValid by viewModel.isKeyValid.collectAsState()

    val messages by viewModel.messages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val streamingText by viewModel.streamingText.collectAsState()
    val chatError by viewModel.chatError.collectAsState()
    val selectedAgent by viewModel.selectedAgent.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    val activeEngineLabel by viewModel.activeEngineLabel.collectAsState()
    val autoAgentRouting by viewModel.autoAgentRouting.collectAsState()
    val executionStatus by viewModel.executionStatus.collectAsState()

    val globalCheckoutUrl by viewModel.globalCheckoutUrl.collectAsState()
    val payoneerTarget by viewModel.payoneerTarget.collectAsState()
    val savedProReference by viewModel.savedProReference.collectAsState()
    val proVerifyStatus by viewModel.proVerifyStatus.collectAsState()

    val engineerTaskDraft by viewModel.engineerTaskDraft.collectAsState()
    val engineerOutput by viewModel.engineerOutput.collectAsState()
    val isEngineerBusy by viewModel.isEngineerBusy.collectAsState()
    val engineerError by viewModel.engineerError.collectAsState()

    val sessions by viewModel.sessions.collectAsState()
    val activeSessionId by viewModel.activeSessionId.collectAsState()

    val favorites by viewModel.favorites.collectAsState()
    val memories by viewModel.memories.collectAsState()

    val taskLoading by viewModel.taskLoading.collectAsState()
    val taskStreamingResult by viewModel.taskStreamingResult.collectAsState()
    val taskError by viewModel.taskError.collectAsState()
    val prefilledTaskParam1 by viewModel.prefilledTaskParam1.collectAsState()

    // Key settings modal control
    var showKeyDialog by remember { mutableStateOf(false) }

    // System Back Handler
    BackHandler(enabled = currentScreen !is Screen.MainTabs || selectedTab != NavigationTab.HOME) {
        viewModel.navigateBack()
    }

    val topBarTitle = when (val screen = currentScreen) {
        is Screen.MainTabs -> when (selectedTab) {
            NavigationTab.HOME -> "ORANOS MAHDI AI"
            NavigationTab.TASKS -> "مركز المهام الذكية"
            NavigationTab.FAVORITES -> "المفضلة والنتائج"
            NavigationTab.CHATS -> "سجل المحادثات"
        }
        is Screen.TaskDetail -> screen.taskType.title
    }

    val topBarSubtitle = when (val screen = currentScreen) {
        is Screen.MainTabs -> when (selectedTab) {
            NavigationTab.HOME -> "المستشار الاستراتيجي الرجالي"
            NavigationTab.TASKS -> "11 مهمة تنفيذية متخصصة"
            NavigationTab.FAVORITES -> "${favorites.size} عناصر محفوظة"
            NavigationTab.CHATS -> "${sessions.size} محادثات سابقة"
        }
        is Screen.TaskDetail -> screen.taskType.category
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ObsidianBlack,
        topBar = {
            OranosTopBar(
                title = topBarTitle,
                subtitle = topBarSubtitle,
                showBackButton = currentScreen !is Screen.MainTabs,
                onBackClick = { viewModel.navigateBack() },
                isOnline = isOnline,
                isKeyValid = isKeyValid,
                currentLanguageMode = languageMode,
                onLanguageChange = { viewModel.setLanguageMode(it) },
                onSaveApiKey = { key -> viewModel.saveApiKey(key) },
                onTestApiKey = { key, onDone -> viewModel.testApiKey(key, onDone) },
                externalShowKeyDialog = showKeyDialog,
                onKeyDialogDismissed = { showKeyDialog = false }
            )
        },
        bottomBar = {
            if (currentScreen is Screen.MainTabs) {
                OranosBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianBlack)
        ) {
            when (val screen = currentScreen) {
                is Screen.MainTabs -> {
                    when (selectedTab) {
                        NavigationTab.HOME -> {
                            HomeScreen(
                                isOnline = isOnline,
                                isKeyValid = isKeyValid,
                                languageMode = languageMode,
                                selectedAgent = selectedAgent,
                                onSelectAgent = { viewModel.selectAgent(it) },
                                selectedMode = selectedMode,
                                onSelectMode = { viewModel.selectMode(it) },
                                activeEngineLabel = activeEngineLabel,
                                autoAgentRouting = autoAgentRouting,
                                onToggleAutoAgentRouting = { viewModel.toggleAutoAgentRouting() },
                                executionStatus = executionStatus,
                                globalCheckoutUrl = globalCheckoutUrl,
                                payoneerTarget = payoneerTarget,
                                savedProReference = savedProReference,
                                proVerifyStatus = proVerifyStatus,
                                onOpenGlobalCheckout = { url ->
                                    runCatching {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(browserIntent)
                                    }
                                },
                                onSaveProReference = { ref -> viewModel.saveProReference(ref) },
                                onSaveChannelConfig = { url, target ->
                                    viewModel.savePaymentChannelConfig(url, target)
                                },
                                engineerTaskDraft = engineerTaskDraft,
                                engineerOutput = engineerOutput,
                                isEngineerBusy = isEngineerBusy,
                                engineerError = engineerError,
                                onEngineerDraftChange = { viewModel.updateEngineerTaskDraft(it) },
                                onRunQuietEngineer = { viewModel.runQuietEngineer(it) },
                                onClearQuietEngineer = { viewModel.clearQuietEngineer() },
                                messages = messages,
                                isGenerating = isGenerating,
                                streamingText = streamingText,
                                chatError = chatError,
                                onSendMessage = { text -> viewModel.sendMessage(text) },
                                onNewChat = { viewModel.createNewSession() },
                                onOpenKeyDialog = { showKeyDialog = true },
                                onToggleMessageFavorite = { msg -> viewModel.toggleMessageFavorite(msg) },
                                onSaveMessageToMemory = { msg -> viewModel.saveMessageToMemory(msg) }
                            )
                        }

                        NavigationTab.TASKS -> {
                            TasksScreen(
                                onSelectTask = { task -> viewModel.openTaskDetail(task) }
                            )
                        }

                        NavigationTab.FAVORITES -> {
                            FavoritesScreen(
                                favorites = favorites,
                                onDeleteFavorite = { id -> viewModel.deleteFavorite(id) },
                                memories = memories,
                                onAddMemory = { cat, title, content -> viewModel.addMemory(cat, title, content) },
                                onDeleteMemory = { id -> viewModel.deleteMemory(id) }
                            )
                        }

                        NavigationTab.CHATS -> {
                            ChatsArchiveScreen(
                                sessions = sessions,
                                activeSessionId = activeSessionId,
                                onSelectSession = { id ->
                                    viewModel.selectSession(id)
                                    viewModel.selectTab(NavigationTab.HOME)
                                },
                                onDeleteSession = { id -> viewModel.deleteSession(id) },
                                onNewChat = {
                                    viewModel.createNewSession()
                                    viewModel.selectTab(NavigationTab.HOME)
                                }
                            )
                        }
                    }
                }

                is Screen.TaskDetail -> {
                    TaskDetailScreen(
                        task = screen.taskType,
                        isLoading = taskLoading,
                        streamingResult = taskStreamingResult,
                        errorMessage = taskError,
                        taskHistoryFlow = viewModel.getHistoryForTask(screen.taskType),
                        initialP1 = prefilledTaskParam1,
                        onExecute = { p1, p2, p3, p4 ->
                            viewModel.executeTask(screen.taskType, p1, p2, p3, p4)
                        },
                        onSaveToFavorites = { title, cat, content ->
                            viewModel.addFavorite(title, cat, content)
                        },
                        onDeleteHistory = { id ->
                            viewModel.deleteTaskHistory(id)
                        },
                        onContinueInChat = { title, result ->
                            viewModel.continueResultInChat(title, result)
                        },
                        onTransferToVideoStudio = { result ->
                            viewModel.transferToVideoStudio(result)
                        }
                    )
                }
            }
        }
    }
}
