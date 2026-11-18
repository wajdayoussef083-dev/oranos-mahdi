package com.example.domain.model

enum class LanguageMode(val title: String, val badge: String) {
    ARABIC("العربية الفصحى", "فصحى"),
    DARIJA("الدارجة المغربية", "دارجة"),
    HYBRID("مزيج تقني ذكي", "مزيج"),
    ENGLISH("English", "EN"),
    FRENCH("Français", "FR"),
    AUTO("تلقائي حسب سياقك", "تلقائي")
}

enum class ProfessionalMode(
    val title: String,
    val badge: String,
    val description: String
) {
    AUTO(
        title = "تلقائي ذكي (Auto Orchestrator)",
        badge = "تلقائي PRO",
        description = "تحليل نية الطلب وتفعيل الوضع والوكلاء المناسبين تلقائياً"
    ),
    BUSINESS(
        title = "وضع الأعمال (Business Mode)",
        badge = "الأعمال والجدوى",
        description = "أبحاث السوق، خطط العمل، التسعير، تحليل المنافسين وحساب الأرباح"
    ),
    DIGITAL_INCOME(
        title = "وضع الدخل الرقمي (Digital Income)",
        badge = "الدخل الرقمي",
        description = "التسويق بالعمولة، المنتجات الرقمية، الفريلانس والخدمات الربحية المشروعة"
    ),
    CREATIVE(
        title = "الوضع الإبداعي (Creative & Video Studio)",
        badge = "استوديو الإبداع",
        description = "السيناريوهات الاحترافية، القصص، الشعر، الفيديوهات وصناعة المحتوى"
    ),
    RESEARCH(
        title = "وضع البحث والتحقق (Research Mode)",
        badge = "البحث والتدقيق",
        description = "مقارنة المعطيات، التحقق من المنطق، وكشف الفرص والمخاطر بدقة"
    ),
    DEVELOPER(
        title = "وضع التطوير البرمجي (Developer Mode)",
        badge = "التطوير والأكواد",
        description = "كتابة وفحص الأكواد، تكامل الـ API، هندسة الأنظمة وبناء التطبيقات"
    )
}

enum class EngineProfile(
    val displayLabel: String,
    val temperature: Double,
    val topP: Double,
    val topK: Int
) {
    PRECISION_CODING(
        displayLabel = "محرك التطوير الدقيق",
        temperature = 0.25,
        topP = 0.85,
        topK = 24
    ),
    DEEP_RESEARCH(
        displayLabel = "محرك البحث والتحليل",
        temperature = 0.40,
        topP = 0.90,
        topK = 32
    ),
    STRATEGIC_PLANNER(
        displayLabel = "محرك التخطيط الاستراتيجي",
        temperature = 0.55,
        topP = 0.92,
        topK = 40
    ),
    CREATIVE_STUDIO(
        displayLabel = "محرك الإبداع والمحتوى",
        temperature = 0.85,
        topP = 0.95,
        topK = 40
    )
}

enum class NavigationTab(val title: String, val iconName: String) {
    HOME("الرئيسية", "home"),
    TASKS("المهام", "tasks"),
    FAVORITES("المفضلة", "favorites"),
    CHATS("المحادثات", "chats")
}

enum class AgentType(
    val title: String,
    val agentName: String,
    val badge: String,
    val roleDescription: String
) {
    COMMANDER(
        title = "القائد المنسق (Commander)",
        agentName = "أورانوس القائد",
        badge = "تنسيق شامل",
        roleDescription = "تنسيق عمل كافة الوكلاء، توزيع المهام، ومراقبة جودة النتائج الاستراتيجية"
    ),
    RESEARCH(
        title = "استخبارات السوق والبحث (Research)",
        agentName = "أورانوس الباحث",
        badge = "بحث واستقصاء",
        roleDescription = "تحليل الأسواق، تدقيق المنافسين، واقتناص الاتجاهات والفرص الصاعدة"
    ),
    BUSINESS(
        title = "مستشار الأعمال والجدوى (Business)",
        agentName = "أورانوس الأعمال",
        badge = "دراسات جدوى",
        roleDescription = "بناء خطط العمل، حساب هوامش الأرباح والتكاليف، وهندسة استراتيجيات الإطلاق"
    ),
    DIGITAL_INCOME(
        title = "صائد الدخل الرقمي (Digital Income)",
        agentName = "أورانوس الفريلانس",
        badge = "فرص بالدولار",
        roleDescription = "اقتناص عقود العمل الحر الدولية، التسويق بالعمولة، وتطوير الخدمات الرقمية"
    ),
    CONTENT_STUDIO(
        title = "استوديو الفيديو والمحتوى (Content Studio)",
        agentName = "أورانوس ستوديو",
        badge = "استوديو الفيديو",
        roleDescription = "صناعة سيناريوهات فيروسية، ستوري بورد كامل مشهد بمشهد، وتوجيهات التصوير والمونتاج"
    ),
    MARKETING(
        title = "مهندس التسويق والحملات (Marketing)",
        agentName = "أورانوس المسوق",
        badge = "إعلانات وحملات",
        roleDescription = "تصميم الحملات الإعلانية الممولة، مسارات تحويل المبيعات (Funnels)، واستهداف الجمهور"
    ),
    PRODUCT_INTEL(
        title = "استخبارات المنتجات (Product Intel)",
        agentName = "أورانوس المنتجات",
        badge = "منتجات رابحة",
        roleDescription = "كشف المنتجات ذات الطلب المرتفع، مقارنة الموردين والأسعار، واختبار هوامش الربح"
    ),
    TRANSLATION(
        title = "المترجم والموطن الثقافي (Translation)",
        agentName = "أورانوس المترجم",
        badge = "ترجمة وتوطين",
        roleDescription = "ترجمة وتوطين المحتوى بين الإنجليزية، الفرنسية، الفصحى، والدارجة المغربية بذكاء لغوي فائق"
    ),
    DEVELOPER(
        title = "المطور والمهندس البرمجي (Developer)",
        agentName = "أورانوس المطور",
        badge = "تطوير وأتمتة",
        roleDescription = "هندسة البرمجيات، كتابة وفحص الأكواد، وتصميم حلول الأتمتة وسير العمل الرقمي"
    ),
    FINANCIAL_ANALYST(
        title = "المحلل المالي وتتبع الأرباح (Finance)",
        agentName = "أورانوس المالي",
        badge = "تحليل مالي",
        roleDescription = "تتبع الإيرادات والمصروفات، قياس كفاءة الإعلانات (ROAS/CAC)، وحساب الأرباح الصافية"
    ),
    MEMORY(
        title = "حارس الذاكرة والسياق (Memory)",
        agentName = "أورانوس الذاكرة",
        badge = "إدارة الذاكرة",
        roleDescription = "حفظ سياق المشروع، تفضيلات المستخدم، القرارات السابقة والمهام غير المكتملة"
    ),
    QUALITY_CONTROL(
        title = "مدقق الجودة والاعتماد (Quality Control)",
        agentName = "أورانوس المدقق",
        badge = "فحص وضمان الجودة",
        roleDescription = "تدقيق المخرجات، كشف الثغرات والادعاءات غير الدقيقة، واقتراح التحسينات الفورية"
    )
}

enum class TaskType(
    val title: String,
    val subtitle: String,
    val iconName: String,
    val description: String,
    val category: String,
    val assignedAgent: AgentType
) {
    DOLLAR_JOBS(
        title = "فرص العمل بالدولار",
        subtitle = "عقود عمل حر وعروض تقديمية مقنعة",
        iconName = "attach_money",
        description = "صياغة عروض تقديمية (Proposals) واقتناص عقود العمل الحر بالعملة الصعبة في منصات Upwork وFiverr والمواقع العالمية",
        category = "العمل الحر",
        assignedAgent = AgentType.DIGITAL_INCOME
    ),
    VIDEO_CONTENT(
        title = "صناعة الفيديوهات والمحتوى",
        subtitle = "نصوص وسيناريوهات فيروسية مع ستوري بورد",
        iconName = "video_camera_back",
        description = "توليد سيناريوهات كاملة، خطافات انتباه قوية (Hooks)، ستوري بورد مشهد بمشهد، ونصوص لفيديوهات Reels، TikTok، وYouTube",
        category = "صناعة المحتوى",
        assignedAgent = AgentType.CONTENT_STUDIO
    ),
    AFFILIATE(
        title = "التسويق بالعمولة",
        subtitle = "مسارات تسويق وروابط تحويل",
        iconName = "trending_up",
        description = "بناء خطط تسويق المنتجات والبرامج بالعمولة، قنوات الترويج الفعالة، وخطابات بيع مقنعة للعملاء",
        category = "التسويق",
        assignedAgent = AgentType.MARKETING
    ),
    WINNING_PRODUCTS(
        title = "البحث عن المنتجات المطلوبة",
        subtitle = "منتجات رابحة للتجارة الإلكترونية",
        iconName = "shopping_bag",
        description = "كشف وتدقيق المنتجات ذات الطلب المرتفع، هوامش الربح المتوقعة، والتسعير المناسب للسوق المغربي والعربي",
        category = "التجارة الإلكترونية",
        assignedAgent = AgentType.PRODUCT_INTEL
    ),
    MARKET_ANALYSIS(
        title = "تحليل الأسواق والمنافسين",
        subtitle = "تحليل SWOT والميزة التنافسية",
        iconName = "analytics",
        description = "تشريح دقيق للمنافسين، كشف الثغرات التسويقية غير المستغلة، وتحديد الموقع التنافسي الأقوى لمشروعك",
        category = "الاستراتيجية",
        assignedAgent = AgentType.RESEARCH
    ),
    TRANSLATION(
        title = "الترجمة والتوطين",
        subtitle = "ترجمة دقيقة للدارجة والفصحى والفرنسية",
        iconName = "translate",
        description = "توطين احترافي للنصوص الإنجليزية والفرنسية إلى الدارجة المغربية الحرة أو العربية الفصحى البليغة",
        category = "اللغات",
        assignedAgent = AgentType.TRANSLATION
    ),
    CAMPAIGNS(
        title = "إنشاء الحملات التسويقية",
        subtitle = "استراتيجيات إعلانات ممولة مستهدفة",
        iconName = "campaign",
        description = "تخطيط حملات إعلانية متكاملة لـ Facebook، Instagram، وTikTok، مع تحديد الجماهير ونصوص الإعلانات الإبداعية",
        category = "التسويق",
        assignedAgent = AgentType.MARKETING
    ),
    PROFIT_TRACKING(
        title = "تتبع الأرباح والنتائج",
        subtitle = "حساب ROI والعائد المالي وصافي الأرباح",
        iconName = "calculate",
        description = "تحليل تكلفة الاستحواذ على العملاء (CAC)، العائد على الإنفاق الإعلاني (ROAS)، وحساب صافي الأرباح الدقيقة",
        category = "المالية",
        assignedAgent = AgentType.FINANCIAL_ANALYST
    ),
    DEV_LAB(
        title = "مختبر التطوير والأكواد",
        subtitle = "هندسة التطبيقات، الـ APIs والأكواد النظيفة",
        iconName = "code",
        description = "بناء هيكلة البرمجيات، كتابة أكواد نظيفة وقابلة للاختبار، تحسين الأداء والأمان وتكامل الـ APIs",
        category = "الهندسة البرمجية",
        assignedAgent = AgentType.DEVELOPER
    ),
    PROBLEM_SOLVER(
        title = "تشخيص وحل الأعطال التقنية",
        subtitle = "تحليل السجلات واكتشاف السبب الجذري للأخطاء",
        iconName = "build",
        description = "فحص رسائل الخطأ والسجلات (Logs)، تحديد السبب الجذري، واقتراح خطة إصلاح آمنة ومختبرة",
        category = "حل المشكلات",
        assignedAgent = AgentType.QUALITY_CONTROL
    ),
    IDEA_MATRIX(
        title = "محرك الإبداع والاستراتيجية",
        subtitle = "توليد سلسلة أفكار متشعبة في 6 طبقات تنفيذية",
        iconName = "auto_awesome",
        description = "تحويل الفكرة إلى 5 زوايا غير متوقعة، صيغ فيديو، شرائح جمهور، عروض، أوصاف ستوديو، وأقصر خطوة تنفيذ اليوم مع 3 فروع توسع",
        category = "الإبداع والاستراتيجية",
        assignedAgent = AgentType.CONTENT_STUDIO
    )
}

sealed interface Screen {
    data object MainTabs : Screen
    data class TaskDetail(val taskType: TaskType) : Screen
}
