package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AlternativeBillingManager
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.FavoriteItemEntity
import com.example.data.local.MemoryEntity
import com.example.data.local.OranosDatabase
import com.example.data.local.TaskHistoryEntity
import com.example.data.network.GeminiApiClient
import com.example.data.network.NetworkMonitor
import com.example.data.repository.AiRepository
import com.example.data.repository.AiRepositoryImpl
import com.example.domain.model.LanguageMode
import com.example.domain.model.NavigationTab
import com.example.domain.model.ProfessionalMode
import com.example.domain.model.Screen
import com.example.domain.model.TaskType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OranosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = OranosDatabase.getDatabase(application)
    private val chatDao = db.chatDao()
    private val memoryDao = db.memoryDao()
    private val favoriteDao = db.favoriteDao()
    private val taskHistoryDao = db.taskHistoryDao()
    private val networkMonitor = NetworkMonitor(application)
    private val aiRepository: AiRepository = AiRepositoryImpl()

    // Navigation state
    private val _selectedTab = MutableStateFlow(NavigationTab.HOME)
    val selectedTab: StateFlow<NavigationTab> = _selectedTab.asStateFlow()

    private val _currentScreen = MutableStateFlow<Screen>(Screen.MainTabs)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun selectTab(tab: NavigationTab) {
        _selectedTab.value = tab
        _currentScreen.value = Screen.MainTabs
    }

    private val _prefilledTaskParam1 = MutableStateFlow("")
    val prefilledTaskParam1: StateFlow<String> = _prefilledTaskParam1.asStateFlow()

    fun openTaskDetail(taskType: TaskType) {
        _prefilledTaskParam1.value = ""
        _currentScreen.value = Screen.TaskDetail(taskType)
        _taskStreamingResult.value = ""
        _taskError.value = null
    }

    fun transferToVideoStudio(contentFromIdeaMatrix: String) {
        _prefilledTaskParam1.value = contentFromIdeaMatrix.take(900)
        _taskStreamingResult.value = ""
        _taskError.value = null
        _currentScreen.value = Screen.TaskDetail(TaskType.VIDEO_CONTENT)
    }

    fun navigateBack(): Boolean {
        if (_currentScreen.value !is Screen.MainTabs) {
            _currentScreen.value = Screen.MainTabs
            return true
        }
        if (_selectedTab.value != NavigationTab.HOME) {
            _selectedTab.value = NavigationTab.HOME
            return true
        }
        return false
    }

    // Network connectivity
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline

    // Language Mode
    private val _languageMode = MutableStateFlow(LanguageMode.ARABIC)
    val languageMode: StateFlow<LanguageMode> = _languageMode.asStateFlow()

    fun setLanguageMode(mode: LanguageMode) {
        _languageMode.value = mode
    }

    // Active Specialist Agent & Fast Auto-Agent Routing
    private val _selectedAgent = MutableStateFlow(com.example.domain.model.AgentType.COMMANDER)
    val selectedAgent: StateFlow<com.example.domain.model.AgentType> = _selectedAgent.asStateFlow()

    private val _autoAgentRouting = MutableStateFlow(true)
    val autoAgentRouting: StateFlow<Boolean> = _autoAgentRouting.asStateFlow()

    private val _executionStatus = MutableStateFlow("جاهز • سرعة قصوى ⚡")
    val executionStatus: StateFlow<String> = _executionStatus.asStateFlow()

    fun selectAgent(agent: com.example.domain.model.AgentType) {
        _selectedAgent.value = agent
        _autoAgentRouting.value = false
    }

    fun toggleAutoAgentRouting() {
        _autoAgentRouting.value = !_autoAgentRouting.value
    }

    // Alternative PRO Billing State (Payoneer + Global Checkout — $9.99)
    private val _globalCheckoutUrl = MutableStateFlow("")
    val globalCheckoutUrl: StateFlow<String> = _globalCheckoutUrl.asStateFlow()

    private val _payoneerTarget = MutableStateFlow("")
    val payoneerTarget: StateFlow<String> = _payoneerTarget.asStateFlow()

    private val _savedProReference = MutableStateFlow("")
    val savedProReference: StateFlow<String> = _savedProReference.asStateFlow()

    private val _proVerifyStatus = MutableStateFlow<String?>(null)
    val proVerifyStatus: StateFlow<String?> = _proVerifyStatus.asStateFlow()

    private fun refreshAlternativeBillingState() {
        _globalCheckoutUrl.value = AlternativeBillingManager.getEffectiveGlobalCheckoutUrl()
        _payoneerTarget.value = AlternativeBillingManager.getEffectivePayoneerTarget()
        _savedProReference.value = AlternativeBillingManager.getSavedProReference()
    }

    fun saveProReference(reference: String) {
        val clean = reference.trim()
        if (clean.isBlank()) {
            _proVerifyStatus.value = "أدخل رقم المرجع أو البريد أولاً."
            return
        }
        AlternativeBillingManager.saveProReference(getApplication(), clean)
        _savedProReference.value = clean
        _proVerifyStatus.value = "تم حفظ المرجع محلياً للمراجعة والتفعيل."
    }

    fun savePaymentChannelConfig(globalUrl: String, payoneerTarget: String) {
        AlternativeBillingManager.saveChannelConfig(getApplication(), globalUrl, payoneerTarget)
        refreshAlternativeBillingState()
    }

    // Quiet Engineer Mode State (المهندس — وضع التنفيذ الهادئ)
    private val _engineerTaskDraft = MutableStateFlow("")
    val engineerTaskDraft: StateFlow<String> = _engineerTaskDraft.asStateFlow()

    private val _engineerOutput = MutableStateFlow(DEFAULT_ENGINEER_OUTPUT)
    val engineerOutput: StateFlow<String> = _engineerOutput.asStateFlow()

    private val _isEngineerBusy = MutableStateFlow(false)
    val isEngineerBusy: StateFlow<Boolean> = _isEngineerBusy.asStateFlow()

    private val _engineerError = MutableStateFlow<String?>(null)
    val engineerError: StateFlow<String?> = _engineerError.asStateFlow()

    private var engineerJob: Job? = null

    init {
        // Initialize Gemini API, alternative billing, and Quiet Engineer state AFTER all StateFlows are initialized
        GeminiApiClient.initialize(application)
        AlternativeBillingManager.initialize(application)
        refreshAlternativeBillingState()

        val restoredDraft = AlternativeBillingManager.getSavedEngineerTask()
        if (restoredDraft.isNotBlank()) {
            _engineerTaskDraft.value = restoredDraft
        }

        // Seed initial memory context
        viewModelScope.launch {
            val existing = memoryDao.getAllMemoriesSync()
            if (existing.isEmpty()) {
                memoryDao.insertMemory(
                    MemoryEntity(
                        category = "الشخصية والأسلوب",
                        title = "هوية أورانوس مهدي",
                        content = "صوت رجالي استراتيجي، يتحدث بالعربية الفصحى البليغة والدارجة المغربية الحرة، يحلل ويفكر بعمق قبل الإجابة."
                    )
                )
                memoryDao.insertMemory(
                    MemoryEntity(
                        category = "مجالات التركيز",
                        title = "العمل الحر والتجارة الرقمية",
                        content = "التركيز على اقتناص فرص العمل بالدولار، التجارة الإلكترونية، صناعة المحتوى، وإعلانات المبيعات."
                    )
                )
            }
        }
    }

    fun updateEngineerTaskDraft(text: String) {
        _engineerTaskDraft.value = text
    }

    fun runQuietEngineer(taskText: String) {
        val cleanTask = taskText.trim()
        if (cleanTask.isBlank()) {
            _engineerError.value = null
            _engineerOutput.value = "تنبيه هادئ: اكتب المهمة أولاً ليبدأ المهندس."
            return
        }
        if (_isEngineerBusy.value) return

        AlternativeBillingManager.saveEngineerTask(getApplication(), cleanTask)
        _engineerTaskDraft.value = cleanTask
        _engineerError.value = null
        _isEngineerBusy.value = true
        _engineerOutput.value = "1. التشخيص: جاري استلام المهمة وتحليلها بهدوء...\n2. الخطة: تحديد أقصر مسار تنفيذي مباشر.\n3. النتيجة: قيد التحضير..."

        engineerJob?.cancel()
        engineerJob = viewModelScope.launch {
            try {
                var accumulated = ""
                aiRepository.executeQuietEngineer(cleanTask, _languageMode.value).collect { chunk ->
                    accumulated = chunk
                    _engineerOutput.value = chunk
                }
                if (accumulated.isBlank()) {
                    _engineerOutput.value = "1. التشخيص: لم يصل مخرج من المحرك.\n2. الخطة: أعد المحاولة بصياغة أوضح.\n3. النتيجة: في انتظار المحاولة."
                }
            } catch (e: Exception) {
                val safeError = GeminiApiClient.sanitizeSensitiveText(
                    e.localizedMessage ?: "تعذر تنفيذ المهمة عبر Gemini API."
                )
                _engineerError.value = safeError
                _engineerOutput.value = "1. التشخيص: تعذر الاتصال بمحرك الذكاء الاصطناعي.\n2. الخطة: تحقق من مفتاح Gemini API أو اتصال الشبكة.\n3. النتيجة: $safeError"
            } finally {
                _isEngineerBusy.value = false
            }
        }
    }

    fun clearQuietEngineer() {
        engineerJob?.cancel()
        _isEngineerBusy.value = false
        _engineerError.value = null
        _engineerTaskDraft.value = ""
        AlternativeBillingManager.clearEngineerTask(getApplication())
        _engineerOutput.value = CLEARED_ENGINEER_OUTPUT
    }

    // Active Professional Mode (ORANOS PROFESSIONAL BRAIN V2)
    private val _selectedMode = MutableStateFlow(ProfessionalMode.AUTO)
    val selectedMode: StateFlow<ProfessionalMode> = _selectedMode.asStateFlow()

    val activeEngineLabel: StateFlow<String> = GeminiApiClient.activeEngineLabel

    fun selectMode(mode: ProfessionalMode) {
        _selectedMode.value = mode
    }

    // API Key status & management
    private val _apiKey = MutableStateFlow(GeminiApiClient.getEffectiveApiKey())
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _isKeyValid = MutableStateFlow(GeminiApiClient.hasValidApiKey())
    val isKeyValid: StateFlow<Boolean> = _isKeyValid.asStateFlow()

    fun saveApiKey(newKey: String) {
        GeminiApiClient.saveApiKey(getApplication(), newKey)
        _apiKey.value = GeminiApiClient.getEffectiveApiKey()
        _isKeyValid.value = GeminiApiClient.hasValidApiKey()
    }

    fun testApiKey(testKey: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = GeminiApiClient.testConnection(testKey)
            if (result.isSuccess) {
                saveApiKey(testKey)
                onComplete(true, result.getOrNull() ?: "تم تأكيد الاتصال بنجاح!")
            } else {
                onComplete(false, result.exceptionOrNull()?.localizedMessage ?: "فشل الاتصال.")
            }
        }
    }

    // Chat sessions and messages
    val sessions: StateFlow<List<ChatSessionEntity>> = chatDao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeSessionId = MutableStateFlow(0L)
    val activeSessionId: StateFlow<Long> = _activeSessionId.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val messages: StateFlow<List<ChatMessageEntity>> = _messages.asStateFlow()

    private var messagesJob: Job? = null
    private var generationJob: Job? = null

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText: StateFlow<String> = _streamingText.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    fun selectSession(sessionId: Long) {
        _activeSessionId.value = sessionId
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            chatDao.getMessagesForSession(sessionId).collect { list ->
                _messages.value = list
            }
        }
    }

    fun createNewSession(initialTitle: String = "محادثة جديدة مع أورانوس"): Long {
        var newId = 0L
        viewModelScope.launch {
            val session = ChatSessionEntity(
                title = initialTitle,
                languageMode = _languageMode.value.name
            )
            newId = chatDao.insertSession(session)
            selectSession(newId)
            val greeting = if (_languageMode.value == LanguageMode.DARIJA) {
                "مرحباً بك أخي الكريم. أنا أورانوس مهدي، مستشارك ومساعدك الذكي. فاش نقدر نعاونك اليوم؟"
            } else {
                "مرحباً بك. أنا أورانوس مهدي، مساعدك الاستشاري فائق الذكاء. كيف أستطيع توجيه ذكائي لدعم أهدافك اليوم؟"
            }
            chatDao.insertMessage(
                ChatMessageEntity(
                    sessionId = newId,
                    sender = "oranos",
                    text = greeting
                )
            )
        }
        return newId
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _isGenerating.value) return

        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            _chatError.value = null
            var currentId = _activeSessionId.value

            if (currentId == 0L) {
                val title = if (userText.length > 25) userText.take(25) + "..." else userText
                val newSession = ChatSessionEntity(
                    title = title,
                    languageMode = _languageMode.value.name
                )
                currentId = chatDao.insertSession(newSession)
                selectSession(currentId)
            } else {
                val s = chatDao.getSessionById(currentId)
                if (s != null) {
                    chatDao.updateSession(s.copy(lastUpdatedAt = System.currentTimeMillis()))
                }
            }

            // Save user message
            val userMsg = ChatMessageEntity(
                sessionId = currentId,
                sender = "user",
                text = userText
            )
            chatDao.insertMessage(userMsg)

            val targetAgent = if (_autoAgentRouting.value) {
                GeminiApiClient.detectAgentFromPrompt(userText).also { _selectedAgent.value = it }
            } else {
                _selectedAgent.value
            }

            _isGenerating.value = true
            _streamingText.value = ""
            _executionStatus.value = "يعمل ذاتياً عبر (${targetAgent.agentName})..."

            try {
                val activeMems = memoryDao.getAllMemoriesSync()
                val history = chatDao.getRecentMessagesSync(currentId, 10)
                var accumulated = ""

                aiRepository.generateChatResponse(
                    history = history,
                    userPrompt = userText,
                    languageMode = _languageMode.value,
                    memories = activeMems,
                    activeAgent = targetAgent,
                    professionalMode = _selectedMode.value
                ).collect { chunk ->
                    _streamingText.value = chunk
                    accumulated = chunk
                }

                if (accumulated.isNotBlank()) {
                    chatDao.insertMessage(
                        ChatMessageEntity(
                            sessionId = currentId,
                            sender = "oranos",
                            text = accumulated
                        )
                    )
                    _executionStatus.value = "اكتمل عبر (${targetAgent.agentName}) ✅"
                }
            } catch (e: Exception) {
                val rawError = e.localizedMessage ?: "تعذر استلام الرد. تأكد من إعداد مفتاح API واتصال الإنترنت."
                val errorMsg = GeminiApiClient.sanitizeSensitiveText(rawError)
                _chatError.value = errorMsg
                _executionStatus.value = "وقع مشكل في الاتصال، عاود جرّب."
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sessionId = currentId,
                        sender = "oranos",
                        text = "⚠️ خطأ في الاستجابة: $errorMsg"
                    )
                )
            } finally {
                _isGenerating.value = false
                _streamingText.value = ""
            }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            chatDao.deleteMessagesForSession(sessionId)
            chatDao.deleteSession(sessionId)
            if (_activeSessionId.value == sessionId) {
                _activeSessionId.value = 0L
                _messages.value = emptyList()
            }
        }
    }

    // Tasks Center execution
    private val _taskLoading = MutableStateFlow(false)
    val taskLoading: StateFlow<Boolean> = _taskLoading.asStateFlow()

    private val _taskStreamingResult = MutableStateFlow("")
    val taskStreamingResult: StateFlow<String> = _taskStreamingResult.asStateFlow()

    private val _taskError = MutableStateFlow<String?>(null)
    val taskError: StateFlow<String?> = _taskError.asStateFlow()

    fun getHistoryForTask(taskType: TaskType): StateFlow<List<TaskHistoryEntity>> {
        return taskHistoryDao.getHistoryForTask(taskType.name)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun executeTask(
        taskType: TaskType,
        param1: String,
        param2: String,
        param3: String,
        param4: String = ""
    ) {
        viewModelScope.launch {
            _taskLoading.value = true
            _taskStreamingResult.value = ""
            _taskError.value = null

            val inputSummary = listOf(param1, param2, param3, param4)
                .filter { it.isNotBlank() }
                .joinToString(" | ")

            try {
                val flow = when (taskType) {
                    TaskType.DOLLAR_JOBS -> aiRepository.executeDollarJobs(param1, param2, param3, _languageMode.value)
                    TaskType.VIDEO_CONTENT -> aiRepository.executeVideoContent(param1, param2, param3, _languageMode.value)
                    TaskType.AFFILIATE -> aiRepository.executeAffiliateMarketing(param1, param2, param3, _languageMode.value)
                    TaskType.WINNING_PRODUCTS -> aiRepository.executeWinningProducts(param1, param2, param3, _languageMode.value)
                    TaskType.MARKET_ANALYSIS -> aiRepository.executeMarketAnalysis(param1, param2, param3, _languageMode.value)
                    TaskType.TRANSLATION -> aiRepository.executeTranslationLocalization(param1, param2, param3, _languageMode.value)
                    TaskType.CAMPAIGNS -> aiRepository.executeMarketingCampaigns(param1, param2, param3, param4, _languageMode.value)
                    TaskType.PROFIT_TRACKING -> aiRepository.executeProfitTracking(param1, param2, param3, param4, _languageMode.value)
                    TaskType.DEV_LAB -> aiRepository.executeDevLab(param1, param2, param3, _languageMode.value)
                    TaskType.PROBLEM_SOLVER -> aiRepository.executeProblemSolver(param1, param2, param3, _languageMode.value)
                    TaskType.IDEA_MATRIX -> aiRepository.executeIdeaMatrix(param1, param2, param3, _languageMode.value)
                }

                var finalResult = ""
                flow.collect { chunk ->
                    _taskStreamingResult.value = chunk
                    finalResult = chunk
                }

                if (finalResult.isNotBlank()) {
                    taskHistoryDao.insertTaskHistory(
                        TaskHistoryEntity(
                            taskType = taskType.name,
                            taskTitle = taskType.title,
                            inputSummary = inputSummary,
                            resultText = finalResult
                        )
                    )
                }
            } catch (e: Exception) {
                _taskError.value = e.localizedMessage ?: "حدث خطأ أثناء تنفيذ المهمة."
            } finally {
                _taskLoading.value = false
            }
        }
    }

    fun deleteTaskHistory(id: Long) {
        viewModelScope.launch {
            taskHistoryDao.deleteTaskHistory(id)
        }
    }

    fun continueResultInChat(title: String, resultText: String) {
        viewModelScope.launch {
            val session = ChatSessionEntity(
                title = "متابعة: $title",
                languageMode = _languageMode.value.name
            )
            val sessionId = chatDao.insertSession(session)
            selectSession(sessionId)
            chatDao.insertMessage(
                ChatMessageEntity(
                    sessionId = sessionId,
                    sender = "oranos",
                    text = "لقد قمت بتحليل واستخراج مخرجات: **$title**\n\n$resultText\n\nما هي الخطوة التالية التي تريد تنفيذها انطلاقاً من هذه النتيجة؟"
                )
            )
            selectTab(NavigationTab.HOME)
        }
    }

    // Favorites
    val favorites: StateFlow<List<FavoriteItemEntity>> = favoriteDao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFavorite(title: String, category: String, content: String) {
        viewModelScope.launch {
            favoriteDao.insertFavorite(
                FavoriteItemEntity(
                    title = title,
                    category = category,
                    content = content
                )
            )
        }
    }

    fun deleteFavorite(id: Long) {
        viewModelScope.launch {
            favoriteDao.deleteFavorite(id)
        }
    }

    fun toggleMessageFavorite(message: ChatMessageEntity) {
        viewModelScope.launch {
            val newFavState = !message.isFavorite
            chatDao.updateMessageFavorite(message.id, newFavState)
            if (newFavState) {
                favoriteDao.insertFavorite(
                    FavoriteItemEntity(
                        title = "رسالة من ${if (message.sender == "user") "أنت" else "أورانوس مهدي"}",
                        category = "دردشة",
                        content = message.text,
                        sourceRef = "msg_${message.id}"
                    )
                )
            }
        }
    }

    // Memory bank
    val memories: StateFlow<List<MemoryEntity>> = memoryDao.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMemory(category: String, title: String, content: String) {
        if (title.isBlank() || content.isBlank()) return
        viewModelScope.launch {
            memoryDao.insertMemory(
                MemoryEntity(
                    category = category.ifBlank { "عام" },
                    title = title,
                    content = content
                )
            )
        }
    }

    fun saveMessageToMemory(message: ChatMessageEntity) {
        viewModelScope.launch {
            val previewTitle = message.text
                .lineSequence()
                .firstOrNull { it.isNotBlank() }
                ?.take(36)
                ?.trim()
                ?: "قرار ومعلومة محفوظة"
            memoryDao.insertMemory(
                MemoryEntity(
                    category = "سياق المحادثات",
                    title = previewTitle,
                    content = message.text.take(600)
                )
            )
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            memoryDao.deleteMemory(id)
        }
    }

    companion object {
        const val DEFAULT_ENGINEER_OUTPUT =
            "1. التشخيص: جاهز في انتظار مهمتك.\n" +
                "2. الخطة: تنفيذ مباشر بأقل عدد من الخطوات.\n" +
                "3. النتيجة: ستظهر هنا مرتبة وجاهزة."

        const val CLEARED_ENGINEER_OUTPUT =
            "1. التشخيص: تم التصفير بهدوء.\n" +
                "2. الخطة: جاهز لمهمة جديدة.\n" +
                "3. النتيجة: في انتظار إدخالك."
    }
}
