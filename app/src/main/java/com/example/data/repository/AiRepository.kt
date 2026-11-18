package com.example.data.repository

import com.example.data.local.ChatMessageEntity
import com.example.data.local.MemoryEntity
import com.example.data.network.GeminiApiClient
import com.example.domain.model.AgentType
import com.example.domain.model.LanguageMode
import com.example.domain.model.ProfessionalMode
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    fun generateChatResponse(
        history: List<ChatMessageEntity>,
        userPrompt: String,
        languageMode: LanguageMode,
        memories: List<MemoryEntity>,
        activeAgent: AgentType? = null,
        professionalMode: ProfessionalMode = ProfessionalMode.AUTO
    ): Flow<String>

    fun executeDollarJobs(
        skillType: String,
        platform: String,
        experience: String,
        languageMode: LanguageMode
    ): Flow<String>

    fun executeVideoContent(
        topic: String,
        platform: String,
        style: String,
        languageMode: LanguageMode
    ): Flow<String>

    fun executeAffiliateMarketing(
        productNiche: String,
        trafficSource: String,
        commissionTarget: String,
        languageMode: LanguageMode
    ): Flow<String>

    fun executeWinningProducts(
        niche: String,
        targetMarket: String,
        priceRange: String,
        languageMode: LanguageMode
    ): Flow<String>

    fun executeMarketAnalysis(
        industry: String,
        competitor: String,
        differentiation: String,
        languageMode: LanguageMode
    ): Flow<String>

    fun executeTranslationLocalization(
        sourceText: String,
        direction: String,
        tone: String,
        languageMode: LanguageMode
    ): Flow<String>

    fun executeMarketingCampaigns(
        campaignGoal: String,
        budget: String,
        targetAudience: String,
        platform: String,
        languageMode: LanguageMode
    ): Flow<String>

    fun executeProfitTracking(
        salesRevenue: String,
        productCost: String,
        adSpend: String,
        shippingFees: String,
        languageMode: LanguageMode
    ): Flow<String>

    fun executeDevLab(
        objective: String,
        stackAndLanguage: String,
        constraints: String,
        languageMode: LanguageMode
    ): Flow<String>

    fun executeProblemSolver(
        errorDescription: String,
        logsOrTrace: String,
        environmentContext: String,
        languageMode: LanguageMode
    ): Flow<String>

    fun executeQuietEngineer(
        task: String,
        languageMode: LanguageMode
    ): Flow<String>

    fun executeIdeaMatrix(
        coreIdea: String,
        domainContext: String,
        excludedAngles: String,
        languageMode: LanguageMode
    ): Flow<String>
}

class AiRepositoryImpl : AiRepository {

    override fun generateChatResponse(
        history: List<ChatMessageEntity>,
        userPrompt: String,
        languageMode: LanguageMode,
        memories: List<MemoryEntity>,
        activeAgent: AgentType?,
        professionalMode: ProfessionalMode
    ): Flow<String> {
        return GeminiApiClient.streamGenerate(
            userPrompt = userPrompt,
            languageMode = languageMode,
            history = history,
            memories = memories,
            activeAgent = activeAgent ?: AgentType.COMMANDER,
            professionalMode = professionalMode
        )
    }

    override fun executeDollarJobs(
        skillType: String,
        platform: String,
        experience: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val prompt = """
مهمة الوكيل [DIGITAL INCOME]: اقتناص فرص العمل الحر الدولية بالدولار.
- المهارة ونوع الخدمة: $skillType
- المنصة المستهدفة: $platform
- مستوى الخبرة والمعرض السابق: $experience

المطلوب وفق بروتوكول أورانوس مهدي:
1. صياغة نموذج عرض تقديمي (Cover Letter / Proposal) احترافي عالي الإقناع ومخصص لهذه المنصة.
2. استراتيجية التسعير بالدولار للمبتدئ والمتوسط.
3. 3 نصائح ذهبية لتخطي المنافسة وجذب انتباه العميل في أول سطرين.
4. صياغة السؤال الحاسم (Closing Question) الذي يحفز العميل على الرد الفوري وتحديد موعد الاتصال.
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = prompt,
            languageMode = languageMode,
            activeAgent = AgentType.DIGITAL_INCOME,
            professionalMode = ProfessionalMode.DIGITAL_INCOME
        )
    }

    override fun executeVideoContent(
        topic: String,
        platform: String,
        style: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val prompt = """
مهمة الوكيل [CONTENT STUDIO - VIDEO STUDIO PROTOCOL]: إنتاج مفهوم وسيناريو فيديو متكامل.
- موضوع وفكرة الفيديو: $topic
- المنصة: $platform (Reels / TikTok / YouTube Shorts)
- النبرة والأسلوب: $style

المطلوب وفق معايير VIDEO STUDIO الصارمة:
1. الخطافات البصرية واللفظية (Hooks): 3 خيارات قوية في أول 3 ثوانٍ لمنع التمرير.
2. الستوري بورد مشهد بمشهد (Scene-by-Scene Storyboard) في جدول أو نقاط:
   - المشهد والتوقيت (Timecode).
   - المشهد البصري وموجهات التوليد (Visual Prompts & B-Roll).
   - نص التعليق الصوتي (Voice-over Text) كلمة بكلمة.
   - المؤثرات الصوتية والموسيقى (Sound FX & Music Direction).
3. تعليمات المونتاج والإيقاع (Editing Instructions).
4. الدعوة لاتخاذ إجراء (CTA) وقائمة الهاشتاغات الاستراتيجية.
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = prompt,
            languageMode = languageMode,
            activeAgent = AgentType.CONTENT_STUDIO,
            professionalMode = ProfessionalMode.CREATIVE
        )
    }

    override fun executeAffiliateMarketing(
        productNiche: String,
        trafficSource: String,
        commissionTarget: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val prompt = """
مهمة الوكيل [BUSINESS & DIGITAL INCOME]: استراتيجية التسويق بالعمولة (Affiliate Marketing).
- مجال المنتجات (Niche): $productNiche
- مصدر الزيارات (Traffic Source): $trafficSource
- الهدف المالي والعمولة المستهدفة: $commissionTarget

المطلوب وفق بروتوكول أورانوس مهدي:
1. أفضل البرامج والشبكات المناسبة لهذا التخصص.
2. مسار القمع التسويقي (Bridge Page / Value Funnel) قبل إرسال العميل لرابط العمولة.
3. نص إعلاني أو منشور مراجعة مقنع يبرز الفائدة دون الظهور كبائع متطفل.
4. استراتيجية حماية الروابط وتتبع التحويلات بدقة.
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = prompt,
            languageMode = languageMode,
            activeAgent = AgentType.BUSINESS,
            professionalMode = ProfessionalMode.DIGITAL_INCOME
        )
    }

    override fun executeWinningProducts(
        niche: String,
        targetMarket: String,
        priceRange: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val prompt = """
مهمة الوكيل [PRODUCT INTELLIGENCE]: كشف وتدقيق منتجات التجارة الإلكترونية الرابحة (Winning Products).
- تصنيف التجارة: $niche
- السوق المستهدف: $targetMarket (مثلاً: المغرب COD، أو الخليج، أو Drop-shipping)
- نطاق السعر المتوقع: $priceRange

المطلوب وفق بروتوكول DIGITAL BUSINESS MODE:
1. معايير تأكيد الطلب في السوق (Market Demand).
2. تحليل هوامش الربح المتوقعة (سعر الشراء، تكلفة الشحن، تكلفة الإعلان، صافي الربح).
3. أبرز 3 زوايا تسويقية (Marketing Angles) لإقناع الزبون بالشراء فوراً.
4. توقع الاعتراضات الشائعة للزبائن في هذا السوق وكيفية الرد عليها.
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = prompt,
            languageMode = languageMode,
            activeAgent = AgentType.PRODUCT_INTEL,
            professionalMode = ProfessionalMode.RESEARCH
        )
    }

    override fun executeMarketAnalysis(
        industry: String,
        competitor: String,
        differentiation: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val prompt = """
مهمة الوكيل [RESEARCH & BUSINESS]: تحليل الأسواق والمنافسين وبناء الميزة التنافسية.
- قطاع العمل: $industry
- المنافسون أو نموذج المنافسة: $competitor
- نقطة التمايز المقترحة: $differentiation

المطلوب وفق بروتوكول أورانوس مهدي:
1. مصفوفة التحليل الرباعي الاستراتيجي (SWOT Analysis) المختصر والمركز.
2. كشف الثغرات التسويقية ونقاط ضعف المنافسين التي يمكن استغلالها.
3. صياغة عرض القيمة الفريد (Unique Value Proposition - UVP).
4. خطة التموضع التنافسي (Positioning Strategy) في السوق.
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = prompt,
            languageMode = languageMode,
            activeAgent = AgentType.RESEARCH,
            professionalMode = ProfessionalMode.RESEARCH
        )
    }

    override fun executeTranslationLocalization(
        sourceText: String,
        direction: String,
        tone: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val prompt = """
مهمة الوكيل [TRANSLATION]: الترجمة والتوطين اللغوي والسياقي (Translation & Cultural Localization).
- النص المراد توطينه:
"$sourceText"
- مسار الترجمة المطلوب: $direction
- نبرة وسياق النص: $tone

المطلوب وفق معايير أورانوس مهدي:
1. الترجمة الاحترافية المباشرة مع الحفاظ على روح المعنى وسلاسة الأسلوب.
2. الصياغة بالدارجة المغربية الحرة الراقية (إذا كان المسار يتضمن الدارجة).
3. شرح الفروق الثقافية وملاحظات استخدام التعبيرات لتفادي الترجمة الحرفية الضعيفة.
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = prompt,
            languageMode = languageMode,
            activeAgent = AgentType.TRANSLATION,
            professionalMode = ProfessionalMode.CREATIVE
        )
    }

    override fun executeMarketingCampaigns(
        campaignGoal: String,
        budget: String,
        targetAudience: String,
        platform: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val prompt = """
مهمة الوكيل [MARKETING]: إنشاء وإطلاق حملة تسويقية وإعلانية ممولة.
- هدف الحملة: $campaignGoal
- الميزانية المخصصة: $budget
- الجمهور المستهدف بدقة: $targetAudience
- المنصة الإعلانية: $platform

المطلوب وفق معايير أورانوس مهدي:
1. هيكل الحملة المقترح ومسار الاختبار (Testing Phase).
2. صياغة نموذجين لنص الإعلان الإبداعي (Ad Copy: الصيغة العاطفية + الصيغة المنطقية).
3. استهداف الاهتمامات والفئات العمرية والمناطق الجغرافية.
4. مؤشرات الأداء الحيوية (KPIs) ومعايير اتخاذ قرار إيقاف أو زيادة ميزانية الإعلان (Scaling).
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = prompt,
            languageMode = languageMode,
            activeAgent = AgentType.MARKETING,
            professionalMode = ProfessionalMode.BUSINESS
        )
    }

    override fun executeProfitTracking(
        salesRevenue: String,
        productCost: String,
        adSpend: String,
        shippingFees: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val prompt = """
مهمة الوكيل [FINANCIAL ANALYST]: التحليل المالي وتتبع الأرباح ومؤشرات الأداء.
- إجمالي الإيرادات أو المبيعات المتوقعة: $salesRevenue
- تكلفة شراء المنتجات أو تقديم الخدمة: $productCost
- الإنفاق الإعلاني والتسويقي: $adSpend
- تكاليف الشحن، التوصيل، ونسب الاسترجاع: $shippingFees

المطلوب وفق بروتوكول DIGITAL BUSINESS MODE:
1. جدول حساب صافي الربح (Net Profit) وهامش الربح الصافي (Net Margin %).
2. حساب نقطة التعادل (Break-even ROAS) وتكلفة الاستحواذ القصوى المقبولة (Target CAC).
3. تشخيص صحة الأرقام المالية: هل الحملة مربحة وقابلة للتوسع؟
4. 3 توصيات حاسمة لتحسين هامش الربح وتخفيض الهدر المالي.
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = prompt,
            languageMode = languageMode,
            activeAgent = AgentType.FINANCIAL_ANALYST,
            professionalMode = ProfessionalMode.BUSINESS
        )
    }

    override fun executeDevLab(
        objective: String,
        stackAndLanguage: String,
        constraints: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val prompt = """
مهمة الوكيل [DEVELOPER - ADVANCED DEVELOPMENT LAB]: بناء وهندسة الحل البرمجي النظيف.
- الهدف والميزة البرمجية المطلوبة: $objective
- اللغة والتقنيات المستعملة (Stack): $stackAndLanguage
- القيود ومتطلبات الأداء والأمان: $constraints

المطلوب وفق معمارية ORANOS PROFESSIONAL BRAIN V2:
1. الهيكلة المعمارية المقترحة (Clean / Modular Architecture) وتقسيم الملفات والمسؤوليات.
2. كتابة الكود النظيف والآمن (Production-ready code) دون ثغرات أو أسرار مكشوفة.
3. تحليل استهلاك الأداء والـ APIs وكيفية التحسين.
4. خطوات الاختبار والتحقق (Unit / Integration Tests) للتأكد من عمل الكود.
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = prompt,
            languageMode = languageMode,
            activeAgent = AgentType.DEVELOPER,
            professionalMode = ProfessionalMode.DEVELOPER
        )
    }

    override fun executeProblemSolver(
        errorDescription: String,
        logsOrTrace: String,
        environmentContext: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val prompt = """
مهمة الوكيل [QUALITY CONTROL & DEVELOPER - AUTONOMOUS PROBLEM SOLVER]: تشخيص وإصلاح الأعطال التقنية.
- وصف الخطأ أو العطل الظاهر: $errorDescription
- السجلات أو رسالة الخطأ (Logs / Stacktrace):
$logsOrTrace
- بيئة التشغيل والسياق البرمجي: $environmentContext

المطلوب وفق بروتوكول [4] AUTONOMOUS PROBLEM SOLVER:
1. تحديد السبب الجذري للخطأ (Root Cause Analysis) بدقة دون تخمين.
2. مقارنة 2-3 استراتيجيات إصلاح مع تقييم الأمان والمخاطر.
3. تقديم الإصلاح العملي النهائي (Step-by-step Fix / Code Patch).
4. قائمة تحقق (Verification Checklist) لاختبار الإصلاح ومنع تكرار الخطأ.
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = prompt,
            languageMode = languageMode,
            activeAgent = AgentType.QUALITY_CONTROL,
            professionalMode = ProfessionalMode.DEVELOPER
        )
    }

    override fun executeQuietEngineer(
        task: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val calmSystemPrompt = """
أنت 'المهندس الهادئ' داخل منظومة ORANOS MAHDI AI — مستقل، خفيف، عملي، وتعمل في صمت وتركيز.
قواعد الإخراج الصارمة:
- بدون مقدمات، بدون حشو، بدون ضجيج، وبدون اختلاق نتائج أو أدوات لم يتم تنفيذها.
- اعرض ردك دائماً في 3 بنود واضحة ومرتبة فقط:
1. التشخيص: حدد جوهر المهمة أو المشكلة بدقة في جمل مركزة.
2. الخطة: خطوات التنفيذ المباشر بأقل عدد ممكن من الخطوات.
3. النتيجة: الحل النهائي، أو الكود النظيف الجاهز للاستعمال مباشرة، مع توضيح أي متطلب عملي ناقص إن وجد.
- التزم بلغة المستخدم (${languageMode.title}) مع مصطلحات برمجية دقيقة.
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = "المهمة المطلوبة:\n$task",
            languageMode = languageMode,
            activeAgent = AgentType.DEVELOPER,
            professionalMode = ProfessionalMode.DEVELOPER,
            systemOverride = calmSystemPrompt
        )
    }

    override fun executeIdeaMatrix(
        coreIdea: String,
        domainContext: String,
        excludedAngles: String,
        languageMode: LanguageMode
    ): Flow<String> {
        val exclusionBlock = if (excludedAngles.isNotBlank()) {
            "\n- زوايا وأفكار سابقة يُمنع تكرارها نهائياً:\n$excludedAngles"
        } else ""

        val contextBlock = if (domainContext.isNotBlank()) {
            "\n- السياق أو المجال والهدف الإضافي: $domainContext"
        } else ""

        val matrixSystemPrompt = """
أنت محرك إبداع واستراتيجية داخل منظومة ORANOS MAHDI AI.
المطلوب: خذ الفكرة الأساسية / الكلمة المفتاحية وولّد منها سلسلة أفكار متشعبة وقابلة للتنفيذ الفوري، بحيث لا تتكرر الزاوية مرتين.

قسّم مخرجاتك دائماً إلى هذه الطبقات الـ 6 بالضبط وبنفس الترقيم:
1. زوايا غير متوقعة (5 زوايا مختلفة ومحددة تنظر للفكرة من زاوية جديدة).
2. صيغ فيديو (فيديو قصير خاطف، فيديو طويل تحليلي، قصة بصرية، مشهد بدون كلام).
3. زوايا الجمهور (مبتدئ، متقدم، عملي سريع، صاحب مشروع، فضولي — فكرة عملية مخصصة لكل شريحة).
4. صيغ محتوى وعروض (شرح، مقارنة، تجربة، سيناريو، خطة عمل).
5. أوصاف مشاهد جاهزة للستوديو (Prompt بصري مختصر وجاهز للنسخ لكل فكرة مختارة).
6. خطوة التنفيذ الأولى (أقصر خطوة عملية واضحة لبدء أول فكرة اليوم).

القواعد الصارمة:
- اجعل كل فكرة محددة وواضحة وقابلة للتطبيق، لا عبارات عامة فضفاضة.
- بعد الدفعة، أضف قسماً أخيراً بعنوان: [فروع التوسع الـ 3 الجديدة] واقترح فيه 3 فروع جديدة محددة يمكن التوسع فيها لتوليد دفعات إضافية بلا حدود.
- أبقِ العرض مرتباً، خفيفاً، وجاهزاً للنسخ أو النقل للستوديو، بلغة (${languageMode.title}).
        """.trimIndent()

        val userPrompt = """
الفكرة الأساسية / الكلمة المفتاحية: $coreIdea$contextBlock$exclusionBlock
        """.trimIndent()

        return GeminiApiClient.streamGenerate(
            userPrompt = userPrompt,
            languageMode = languageMode,
            activeAgent = AgentType.CONTENT_STUDIO,
            professionalMode = ProfessionalMode.CREATIVE,
            systemOverride = matrixSystemPrompt
        )
    }
}
