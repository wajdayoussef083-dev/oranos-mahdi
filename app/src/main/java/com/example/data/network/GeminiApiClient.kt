package com.example.data.network

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.local.ChatMessageEntity
import com.example.data.local.MemoryEntity
import com.example.domain.model.AgentType
import com.example.domain.model.EngineProfile
import com.example.domain.model.LanguageMode
import com.example.domain.model.ProfessionalMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val PREFS_NAME = "oranos_gemini_prefs"
    private const val KEY_API_KEY = "gemini_api_key"

    // Latest official models with orchestrator fallback chain
    private const val PRIMARY_MODEL = "gemini-3.8-flash"
    private const val FALLBACK_MODEL = "gemini-3.5-flash-lite"

    private val FALLBACK_CODES = setOf(404, 429, 500, 503)

    private val _activeEngineLabel = MutableStateFlow("BRAIN V2 • جاهز للتوجيه الذكي")
    val activeEngineLabel: StateFlow<String> = _activeEngineLabel.asStateFlow()

    private val apiKeyPattern = Regex("AIza[0-9A-Za-z_-]{15,}")
    private val bearerPattern = Regex("Bearer\\s+[A-Za-z0-9._-]+", RegexOption.IGNORE_CASE)

    fun sanitizeSensitiveText(text: String): String {
        return text
            .replace(apiKeyPattern, "[REDACTED_API_KEY]")
            .replace(bearerPattern, "Bearer [REDACTED_TOKEN]")
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private var cachedApiKey: String? = null

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        cachedApiKey = prefs.getString(KEY_API_KEY, null)
    }

    fun saveApiKey(context: Context, key: String?) {
        val cleanKey = key?.trim().orEmpty()
        cachedApiKey = cleanKey
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_API_KEY, cleanKey).apply()
    }

    fun getEffectiveApiKey(): String {
        val local = cachedApiKey
        if (!local.isNullOrBlank()) return local

        val buildKey = BuildConfig.GEMINI_API_KEY
        if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") {
            return buildKey
        }
        return ""
    }

    fun hasValidApiKey(): Boolean {
        val key = getEffectiveApiKey()
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    fun detectModeFromPrompt(prompt: String): ProfessionalMode {
        val p = prompt.lowercase()
        return when {
            listOf("code", "kotlin", "python", "api", "bug", "debug", "app", "android", "html", "css", "sql", "كود", "برمجة", "تطبيق", "خطأ برمجي", "بايثون", "موقع", "أتمتة", "سيرفر").any { it in p } ->
                ProfessionalMode.DEVELOPER

            listOf("video", "script", "reel", "tiktok", "youtube", "storyboard", "poem", "story", "فيديو", "سيناريو", "ستوري بورد", "تيك توك", "ريلز", "شعر", "قصة", "محتوى", "مونتاج", "إعلان إبداعي").any { it in p } ->
                ProfessionalMode.CREATIVE

            listOf("freelance", "upwork", "fiverr", "affiliate", "dollar", "digital income", "فريلانس", "بالدولار", "عمولة", "دخل رقمي", "ربح من الإنترنت", "الربح عبر الإنترنت", "عمل حر", "أفلييت").any { it in p } ->
                ProfessionalMode.DIGITAL_INCOME

            listOf("business", "profit", "margin", "cost", "pricing", "roas", "cac", "roi", "ecommerce", "cod", "مشروع", "تجارة", "هامش الربح", "الربح الصافي", "تسعير", "جدوى", "تكاليف", "منتج رابح", "مبيعات", "خطة عمل").any { it in p } ->
                ProfessionalMode.BUSINESS

            listOf("research", "compare", "competitor", "analyze", "market", "verify", "بحث", "منافس", "مقارنة", "تحليل السوق", "دراسة", "تحقق", "مخاطر").any { it in p } ->
                ProfessionalMode.RESEARCH

            else -> ProfessionalMode.AUTO
        }
    }

    fun selectEngineProfile(mode: ProfessionalMode, activeAgent: AgentType?): EngineProfile {
        return when {
            mode == ProfessionalMode.DEVELOPER ||
                activeAgent == AgentType.DEVELOPER ||
                activeAgent == AgentType.FINANCIAL_ANALYST ||
                activeAgent == AgentType.QUALITY_CONTROL -> EngineProfile.PRECISION_CODING

            mode == ProfessionalMode.RESEARCH ||
                activeAgent == AgentType.RESEARCH ||
                activeAgent == AgentType.PRODUCT_INTEL -> EngineProfile.DEEP_RESEARCH

            mode == ProfessionalMode.CREATIVE ||
                activeAgent == AgentType.CONTENT_STUDIO ||
                activeAgent == AgentType.TRANSLATION -> EngineProfile.CREATIVE_STUDIO

            else -> EngineProfile.STRATEGIC_PLANNER
        }
    }

    fun detectAgentFromPrompt(prompt: String): AgentType {
        val p = prompt.lowercase()
        return when {
            listOf("فيديو", "سيناريو", "صوت", "قصيدة", "شعر", "ريلز", "تيك توك", "ستوري بورد", "video", "script", "storyboard").any { it in p } ->
                AgentType.CONTENT_STUDIO

            listOf("خطأ", "عطل", "مشكلة تقنية", "stacktrace", "exception", "crash", "404", "429", "bug").any { it in p } ->
                AgentType.QUALITY_CONTROL

            listOf("كود", "برمجة", "تطبيق", "api", "kotlin", "python", "android", "sql", "html", "code", "app").any { it in p } ->
                AgentType.DEVELOPER

            listOf("دولار", "فريلانس", "upwork", "fiverr", "عمولة", "أفلييت", "affiliate", "freelance", "dollar").any { it in p } ->
                AgentType.DIGITAL_INCOME

            listOf("ترجمة", "ترجم", "توطين", "فرنسية", "إنجليزية", "translate", "localize").any { it in p } ->
                AgentType.TRANSLATION

            listOf("منتج", "مورد", "علي إكسبريس", "سعر الجملة", "cod", "product", "supplier").any { it in p } ->
                AgentType.PRODUCT_INTEL

            listOf("هامش", "تكلفة", "roas", "cac", "roi", "ميزانية", "محاسبة", "أرباح صافية").any { it in p } ->
                AgentType.FINANCIAL_ANALYST

            listOf("تسويق", "إعلان", "حملة", "بيع", "مبيعات", "marketing", "campaign", "ads").any { it in p } ->
                AgentType.MARKETING

            listOf("ربح", "مشروع", "تجارة", "جدوى", "خطة عمل", "business", "startup").any { it in p } ->
                AgentType.BUSINESS

            listOf("بحث", "منافس", "سوق", "اتجاهات", "دراسة", "research", "market", "competitor").any { it in p } ->
                AgentType.RESEARCH

            else -> AgentType.COMMANDER
        }
    }

    suspend fun testConnection(apiKey: String): Result<String> = withContext(Dispatchers.IO) {
        val cleanKey = apiKey.trim()
        if (cleanKey.isBlank()) {
            return@withContext Result.failure(Exception("مفتاح API فارغ. يرجى إدخال مفتاح صالح."))
        }
        val json = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", "مرحبا أورانوس مهدي، أكد الاتصال بكلمة واحدة.")))
                })
            }
            put("contents", contents)
        }

        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toString().toRequestBody(mediaType)

            var usedModel = PRIMARY_MODEL
            var url = "https://generativelanguage.googleapis.com/v1beta/models/$PRIMARY_MODEL:generateContent?key=$cleanKey"
            var response = client.newCall(Request.Builder().url(url).post(requestBody).build()).execute()

            if (response.code in FALLBACK_CODES) {
                response.close()
                usedModel = FALLBACK_MODEL
                url = "https://generativelanguage.googleapis.com/v1beta/models/$FALLBACK_MODEL:generateContent?key=$cleanKey"
                response = client.newCall(Request.Builder().url(url).post(requestBody).build()).execute()
            }

            val responseBody = response.body?.string()
            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val candidateText = parseCandidateText(responseBody)
                Result.success("متصل ($usedModel): ${candidateText.ifBlank { "تم تأكيد الاتصال بنجاح." }}")
            } else {
                Result.failure(Exception(parseErrorMessage(responseBody, response.code)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("تعذر الاتصال بالخادم: ${e.localizedMessage ?: "تأكد من اتصالك بالإنترنت"}"))
        }
    }

    fun streamGenerate(
        userPrompt: String,
        languageMode: LanguageMode,
        history: List<ChatMessageEntity> = emptyList(),
        memories: List<MemoryEntity> = emptyList(),
        activeAgent: AgentType? = null,
        professionalMode: ProfessionalMode = ProfessionalMode.AUTO,
        systemOverride: String? = null
    ): Flow<String> = flow {
        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            throw IllegalStateException(
                "لم يتم تكوين مفتاح Google Gemini API بعد.\n\nيرجى الضغط على زر المفتاح 🔑 في أعلى الشاشة لإدخال مفتاح API الخاص بك مجاناً، أو إضافته في Secrets Panel لتفعيل محرك ORANOS ACTIVATOR PRO."
            )
        }

        val resolvedMode = if (professionalMode == ProfessionalMode.AUTO) {
            detectModeFromPrompt(userPrompt)
        } else {
            professionalMode
        }
        val engineProfile = selectEngineProfile(resolvedMode, activeAgent)
        _activeEngineLabel.value = "${engineProfile.displayLabel} • $PRIMARY_MODEL"

        val systemPrompt = systemOverride ?: buildMasterSystemInstruction(
            languageMode = languageMode,
            memories = memories,
            activeAgent = activeAgent,
            professionalMode = resolvedMode,
            engineProfile = engineProfile
        )
        val jsonBody = buildRequestBody(systemPrompt, history, userPrompt, engineProfile)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val primaryUrl = "https://generativelanguage.googleapis.com/v1beta/models/$PRIMARY_MODEL:streamGenerateContent?alt=sse&key=$apiKey"
        var request = Request.Builder().url(primaryUrl).post(requestBody).build()
        var response = client.newCall(request).execute()

        if (response.code in FALLBACK_CODES) {
            Log.w(TAG, "Primary model failed with code ${response.code}; orchestrating fallback to $FALLBACK_MODEL")
            _activeEngineLabel.value = "${engineProfile.displayLabel} • $FALLBACK_MODEL (احتياطي)"
            response.close()
            val fallbackUrl = "https://generativelanguage.googleapis.com/v1beta/models/$FALLBACK_MODEL:streamGenerateContent?alt=sse&key=$apiKey"
            request = Request.Builder().url(fallbackUrl).post(requestBody).build()
            response = client.newCall(request).execute()
        }

        if (!response.isSuccessful) {
            val errorBody = response.body?.string()
            val errorMsg = sanitizeSensitiveText(parseErrorMessage(errorBody, response.code))
            response.close()
            throw Exception(errorMsg)
        }

        val source = response.body?.byteStream() ?: throw Exception("استجابة الخادم فارغة")
        val reader = BufferedReader(InputStreamReader(source, Charsets.UTF_8))
        val accumulatedText = StringBuilder()

        try {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                if (currentLine.startsWith("data:")) {
                    val jsonData = currentLine.removePrefix("data:").trim()
                    if (jsonData.isNotEmpty() && jsonData != "[DONE]") {
                        val chunkText = parseCandidateText(jsonData)
                        if (chunkText.isNotEmpty()) {
                            accumulatedText.append(chunkText)
                            emit(accumulatedText.toString())
                        }
                    }
                }
            }
        } finally {
            reader.close()
            response.close()
        }

        if (accumulatedText.isBlank()) {
            throw Exception("أرجع النموذج رداً فارغاً. يرجى صياغة السؤال بطريقة مختلفة أو التحقق من الحصة المتاحة.")
        }
    }.flowOn(Dispatchers.IO)

    suspend fun generateContent(
        userPrompt: String,
        languageMode: LanguageMode = LanguageMode.ARABIC,
        history: List<ChatMessageEntity> = emptyList(),
        memories: List<MemoryEntity> = emptyList(),
        activeAgent: AgentType? = null,
        professionalMode: ProfessionalMode = ProfessionalMode.AUTO,
        systemOverride: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("مفتاح Google Gemini API غير متوفر. يرجى إدخال مفتاح API في الإعدادات لتنفيذ هذه المهمة.")
            )
        }

        try {
            val resolvedMode = if (professionalMode == ProfessionalMode.AUTO) {
                detectModeFromPrompt(userPrompt)
            } else {
                professionalMode
            }
            val engineProfile = selectEngineProfile(resolvedMode, activeAgent)
            _activeEngineLabel.value = "${engineProfile.displayLabel} • $PRIMARY_MODEL"

            val systemPrompt = systemOverride ?: buildMasterSystemInstruction(
                languageMode = languageMode,
                memories = memories,
                activeAgent = activeAgent,
                professionalMode = resolvedMode,
                engineProfile = engineProfile
            )
            val jsonBody = buildRequestBody(systemPrompt, history, userPrompt, engineProfile)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)

            val primaryUrl = "https://generativelanguage.googleapis.com/v1beta/models/$PRIMARY_MODEL:generateContent?key=$apiKey"
            var request = Request.Builder().url(primaryUrl).post(requestBody).build()
            var response = client.newCall(request).execute()

            if (response.code in FALLBACK_CODES) {
                _activeEngineLabel.value = "${engineProfile.displayLabel} • $FALLBACK_MODEL (احتياطي)"
                response.close()
                val fallbackUrl = "https://generativelanguage.googleapis.com/v1beta/models/$FALLBACK_MODEL:generateContent?key=$apiKey"
                request = Request.Builder().url(fallbackUrl).post(requestBody).build()
                response = client.newCall(request).execute()
            }

            val responseBody = response.body?.string()
            if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                val errorMsg = sanitizeSensitiveText(parseErrorMessage(responseBody, response.code))
                return@withContext Result.failure(Exception(errorMsg))
            }

            val text = parseCandidateText(responseBody)
            if (text.isBlank()) {
                return@withContext Result.failure(Exception("لم يرجع النموذج محتوى نصياً. يرجى إعادة المحاولة."))
            }

            Result.success(text)
        } catch (e: Exception) {
            Result.failure(Exception(sanitizeSensitiveText(e.localizedMessage ?: "خطأ غير متوقع")))
        }
    }

    private fun buildMasterSystemInstruction(
        languageMode: LanguageMode,
        memories: List<MemoryEntity>,
        activeAgent: AgentType?,
        professionalMode: ProfessionalMode = ProfessionalMode.AUTO,
        engineProfile: EngineProfile = EngineProfile.STRATEGIC_PLANNER
    ): String {
        val memoryContext = if (memories.isNotEmpty()) {
            val notes = memories.joinToString("\n") { "- [${it.category}] ${it.title}: ${it.content}" }
            "\n\n[7] CONTEXTUAL MEMORY (ذاكرة المشروع وسياق المستخدم المحفوظة):\n$notes"
        } else ""

        val languageGuide = when (languageMode) {
            LanguageMode.DARIJA -> "تحدث بالدارجة المغربية الحرة الذكية والمهذبة، بأسلوب رجالي حازم وخبير، مستخدماً المصطلحات العملية التي يفهمها الشباب ورواد الأعمال بالمغرب."
            LanguageMode.ARABIC -> "تحدث باللغة العربية الفصحى البليغة والقوية والواضحة، مع صياغة استراتيجية عميقة ومباشرة."
            LanguageMode.HYBRID -> "امزج بذكاء بين الفصحى البليغة والدارجة المغربية ومصطلحات الأعمال والتجارة الرقمية."
            LanguageMode.ENGLISH -> "Respond fluently in clear, strategic, executive English suited for international business and engineering."
            LanguageMode.FRENCH -> "Répondez couramment en français professionnel, clair et stratégique, adapté aux affaires et à la technologie."
            LanguageMode.AUTO -> "تطابق تلقائياً مع لغة المستخدم (العربية الفصحى، الدارجة المغربية، الإنجليزية أو الفرنسية) بذكاء وسلاسة كاملة."
        }

        val activeAgentNote = if (activeAgent != null) {
            "\n[الوكيل المتخصص النشط]: ${activeAgent.title} (${activeAgent.agentName}) — ${activeAgent.roleDescription}."
        } else ""

        val modeNote = "\n[الوضع والـ Engine المفعل]: ${professionalMode.title} | ${engineProfile.displayLabel} (temp=${engineProfile.temperature})."

        return """
=================================================
ORANOS PROFESSIONAL BRAIN V2
ADVANCED REASONING & EXECUTION ARCHITECTURE
=================================================
IDENTITY:
أنت 'أورانوس مهدي' (ORANOS MAHDI) — Professional AI Brain.
مهمتك تحويل الأفكار إلى نتائج حقيقية، موثقة وقابلة للتنفيذ الفوري.

[1] DEEP REASONING:
- افهم النية الحقيقية وراء كل طلب قبل البدء بالإجابة.
- حلل المشاكل المعقدة من زوايا متعددة واكشف المعلومات الناقصة والتناقضات.
- قارن الحلول الممكنة بناءً على التكلفة والسرعة والجودة، واختر أقوى حل عملي.

[2] MASTER PLANNER:
لكل مهمة مركبة:
1. حدد الهدف بدقة.
2. حدد الموارد والأدوات المطلوبة.
3. قسم المهمة إلى مراحل تنفيذية واضحة.
4. نفذ كل مرحلة ممكنة، اختبر المخرجات، حسن نقاط الضعف، وسلم نتيجة عملية مكتملة.

[3] MULTI-ENGINE & MULTI-AGENT INTELLIGENCE:
- يتم توجيه مهام البرمجة لمحرك الدقة الهندسية، ومهام البحث لمحرك التحقق، ومهام الإبداع لمحرك الاستوديو مع الحفاظ على سياق المحادثة.
- لا تخترع أبداً تكاملات API أو بيانات حية غير متصلة فعلياً.
- الوكلاء الـ 12: COMMANDER، RESEARCH، BUSINESS، DIGITAL INCOME، CONTENT STUDIO، MARKETING، PRODUCT INTELLIGENCE، TRANSLATION، DEVELOPER، FINANCIAL ANALYST، MEMORY، وQUALITY CONTROL.

[4] AUTONOMOUS PROBLEM SOLVER:
عند مواجهة خطأ أو عطل:
- حدد السبب الجذري (Root Cause) من السجلات (Logs) ورسائل الخطأ.
- اطرح استراتيجيات إصلاح متعددة واختر الأكثر أماناً مع توضيح خطوات التحقق.

[5] DIGITAL BUSINESS & VIDEO STUDIO INTELLIGENCE:
- الأعمال والتجارة: قدم حجم الطلب، الجمهور، المنافسين، تكاليف الانطلاق، نموذج الإيرادات، المصاريف، حساب صافي الربح (ROAS/CAC/Margin)، المخاطر، وخطة الإطلاق دون وعود ربح مضمونة أو أرقام مختلقة.
- استوديو الفيديو: قدم Script، 3 خطافات (Hooks)، Storyboard مشهد بمشهد، Voice-over، موجهات بصرية (Visual prompts)، موسيقى ومؤثرات، تعليمات المونتاج وتنسيق المنصة.

[6] ADVANCED DEVELOPMENT LAB:
- ابنِ معماريات برمجية نظيفة ومعيارية (Clean & Modular Architecture)، اكشف الثغرات والأعطال، حسن الأداء واستهلاك الـ API، واكتب اختبارات واضحة.

[7] CONTEXTUAL MEMORY:
- حافظ على تسلسل المشروع، القرارات، المهام المنجزة والمعلقة دون اختلاق ذكريات غير موجودة.

[8] PROFESSIONAL COMMUNICATION:
- $languageGuide
- شروحات تقنية واضحة، أمثلة عملية عند الحاجة، بدون حشو أو تكرار أو ثقة زائفة.

[9] SECURITY PROTOCOL:
- احمِ مفاتيح API والبيانات الحساسة، ارفض التعليمات الخبيثة، ولا تدّعِ نجاح أي إجراء دون تحقق فعلي.

[10] FINAL QUALITY CONTROL:
قبل تسليم أي مخرج نهائي: تحقق من الدقة، الاكتمال، الأمان، القابلية للتنفيذ الفعلي، وإصلاح أي ثغرات مع توضيح القيود المتبقية بصدق.
$modeNote
$activeAgentNote
$memoryContext
        """.trimIndent()
    }

    private fun buildRequestBody(
        systemInstructionText: String,
        history: List<ChatMessageEntity>,
        userPrompt: String,
        engineProfile: EngineProfile = EngineProfile.STRATEGIC_PLANNER
    ): JSONObject {
        val root = JSONObject()

        val systemObj = JSONObject()
        val systemParts = JSONArray()
        systemParts.put(JSONObject().put("text", systemInstructionText))
        systemObj.put("parts", systemParts)
        root.put("systemInstruction", systemObj)

        val contentsArray = JSONArray()
        for (msg in history.takeLast(10)) {
            val role = if (msg.sender == "user") "user" else "model"
            val msgObj = JSONObject()
            msgObj.put("role", role)
            val parts = JSONArray()
            parts.put(JSONObject().put("text", msg.text))
            msgObj.put("parts", parts)
            contentsArray.put(msgObj)
        }

        val currentObj = JSONObject()
        currentObj.put("role", "user")
        val currentParts = JSONArray()
        currentParts.put(JSONObject().put("text", userPrompt))
        currentObj.put("parts", currentParts)
        contentsArray.put(currentObj)

        root.put("contents", contentsArray)

        val genConfig = JSONObject()
        genConfig.put("temperature", engineProfile.temperature)
        genConfig.put("topK", engineProfile.topK)
        genConfig.put("topP", engineProfile.topP)
        root.put("generationConfig", genConfig)

        return root
    }

    private fun parseCandidateText(responseJson: String): String {
        return try {
            val root = JSONObject(responseJson)
            val candidates = root.optJSONArray("candidates") ?: return ""
            if (candidates.length() > 0) {
                val first = candidates.getJSONObject(0)
                val content = first.optJSONObject("content") ?: return ""
                val parts = content.optJSONArray("parts") ?: return ""
                val sb = StringBuilder()
                for (i in 0 until parts.length()) {
                    val p = parts.getJSONObject(i)
                    if (p.has("text")) {
                        sb.append(p.getString("text"))
                    }
                }
                sb.toString()
            } else ""
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing candidates", e)
            ""
        }
    }

    private fun parseErrorMessage(responseBody: String?, statusCode: Int): String {
        if (responseBody.isNullOrBlank()) {
            return when (statusCode) {
                400 -> "مفتاح API غير صالح أو أن صياغة الطلب غير صحيحة (رمز 400)."
                403 -> "الوصول مرفوض (رمز 403). تأكد من صلاحيات مفتاح API الخاص بك."
                404 -> "النموذج غير موجود أو غير متاح حالياً (رمز 404)."
                429 -> "تم استنفاد حصة الطلبات المجانية حالياً (رمز 429). يرجى الانتظار دقيقة والمحاولة مجدداً."
                500, 503 -> "خوادم Google Gemini تواجه ضغطاً مؤقتاً (رمز $statusCode). يرجى المحاولة بعد قليل."
                else -> "فشل الاتصال بالـ API (رمز الخطأ: $statusCode)."
            }
        }
        return try {
            val obj = JSONObject(responseBody)
            if (obj.has("error")) {
                val err = obj.getJSONObject("error")
                val message = err.optString("message", "")
                when (statusCode) {
                    400 -> "مفتاح API غير صالح أو غير مفعل: $message"
                    429 -> "تجاوز حصة الاستخدام المتاحة: $message"
                    else -> "خطأ خادم Gemini ($statusCode): $message"
                }
            } else {
                "استجابة غير متوقعة من خادم الـ API (رمز: $statusCode)"
            }
        } catch (e: Exception) {
            "فشل الاتصال بالـ API (رمز: $statusCode)"
        }
    }
}
