package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TaskHistoryEntity
import com.example.domain.model.TaskType
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.ImperialGoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskDetailScreen(
    task: TaskType,
    isLoading: Boolean,
    streamingResult: String,
    errorMessage: String?,
    taskHistoryFlow: StateFlow<List<TaskHistoryEntity>>,
    initialP1: String = "",
    onExecute: (p1: String, p2: String, p3: String, p4: String) -> Unit,
    onSaveToFavorites: (title: String, category: String, content: String) -> Unit,
    onDeleteHistory: (Long) -> Unit,
    onContinueInChat: (title: String, result: String) -> Unit,
    onTransferToVideoStudio: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val history by taskHistoryFlow.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: تنفيذ المهمة, 1: سجل النتائج

    var p1 by remember(task) { mutableStateOf(initialP1) }
    var p2 by remember(task) { mutableStateOf("") }
    var p3 by remember(task) { mutableStateOf("") }
    var p4 by remember(task) { mutableStateOf("") }

    LaunchedEffect(task, initialP1) {
        if (initialP1.isNotBlank()) {
            p1 = initialP1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Tab switcher: تنفيذ المهمة vs سجل النتائج
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = ImperialGold,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp)
                        .background(ImperialGold)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "تنفيذ المهمة",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "سجل النتائج (${history.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            )
        }

        if (selectedTab == 0) {
            // Execution Tab
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header info
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ImperialGold
                            )
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                // Input Parameters Form
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            when (task) {
                                TaskType.DOLLAR_JOBS -> {
                                    FormField(
                                        value = p1,
                                        onValueChange = { p1 = it },
                                        label = "المهارة ونوع الخدمة (Skill & Service)",
                                        placeholder = "مثال: كتابة إعلانات، تصميم متاجر Shopify، ترجمة فرنسية، مونتاج ريلز",
                                        testTag = "input_dollar_skill"
                                    )
                                    FormField(
                                        value = p2,
                                        onValueChange = { p2 = it },
                                        label = "المنصة المستهدفة (Platform)",
                                        placeholder = "مثال: Upwork، Fiverr، لينكد إن، أو عملاء مباشرين عبر البريد",
                                        testTag = "input_dollar_platform"
                                    )
                                    FormField(
                                        value = p3,
                                        onValueChange = { p3 = it },
                                        label = "المعرض والخبرة السابقة",
                                        placeholder = "مثال: مبتدئ مع نماذج تجريبية، أو خبرة سنتين في السوق المحلي",
                                        testTag = "input_dollar_experience"
                                    )
                                }

                                TaskType.VIDEO_CONTENT -> {
                                    FormField(
                                        value = p1,
                                        onValueChange = { p1 = it },
                                        label = "موضوع الفكرة أو الفيديو",
                                        placeholder = "مثال: 3 أسرار لزيادة المبيعات عبر الواتساب بدون إعلانات",
                                        testTag = "input_video_topic"
                                    )
                                    FormField(
                                        value = p2,
                                        onValueChange = { p2 = it },
                                        label = "المنصة المستهدفة (Platform)",
                                        placeholder = "مثال: Instagram Reels، TikTok، أو YouTube Shorts",
                                        testTag = "input_video_platform"
                                    )
                                    FormField(
                                        value = p3,
                                        onValueChange = { p3 = it },
                                        label = "أسلوب ونبرة التقديم (Tone & Style)",
                                        placeholder = "مثال: دارجة مغربية ذكية ومحفزة، أو فصحى مهنية سريعة الإيقاع",
                                        testTag = "input_video_style"
                                    )
                                }

                                TaskType.AFFILIATE -> {
                                    FormField(
                                        value = p1,
                                        onValueChange = { p1 = it },
                                        label = "مجال ونوعية المنتجات (Niche)",
                                        placeholder = "مثال: برامج وأدوات الذكاء الاصطناعي، استضافات المواقع، دورات رقمية",
                                        testTag = "input_affiliate_niche"
                                    )
                                    FormField(
                                        value = p2,
                                        onValueChange = { p2 = it },
                                        label = "مصدر الزيارات (Traffic Source)",
                                        placeholder = "مثال: صفحة تيك توك، مقالات سيو، إعلانات فيسبوك، قناة تيليغرام",
                                        testTag = "input_affiliate_traffic"
                                    )
                                    FormField(
                                        value = p3,
                                        onValueChange = { p3 = it },
                                        label = "الهدف المالي والعمولة المتوقعة",
                                        placeholder = "مثال: 50 دولار عمولة متكررة شهرياً عن كل عميل",
                                        testTag = "input_affiliate_goal"
                                    )
                                }

                                TaskType.WINNING_PRODUCTS -> {
                                    FormField(
                                        value = p1,
                                        onValueChange = { p1 = it },
                                        label = "تصنيف وتخصص المنتج (Niche)",
                                        placeholder = "مثال: أدوات المطبخ الذكية، مستحضرات العناية الطبيعية، إلكترونيات السيارات",
                                        testTag = "input_product_niche"
                                    )
                                    FormField(
                                        value = p2,
                                        onValueChange = { p2 = it },
                                        label = "السوق المستهدف وطريقة البيع",
                                        placeholder = "مثال: السوق المغربي (الدفع عند الاستلام COD)، أو الخليج، أو عالمي",
                                        testTag = "input_product_market"
                                    )
                                    FormField(
                                        value = p3,
                                        onValueChange = { p3 = it },
                                        label = "نطاق سعر البيع المقترح",
                                        placeholder = "مثال: بين 199 و299 درهم مغربي",
                                        testTag = "input_product_price"
                                    )
                                }

                                TaskType.MARKET_ANALYSIS -> {
                                    FormField(
                                        value = p1,
                                        onValueChange = { p1 = it },
                                        label = "قطاع ومجال العمل (Industry)",
                                        placeholder = "مثال: بيع الملابس الرياضية أونلاين، أو تقديم خدمات تحسين محركات البحث",
                                        testTag = "input_market_industry"
                                    )
                                    FormField(
                                        value = p2,
                                        onValueChange = { p2 = it },
                                        label = "المنافسون المباشرون أو نموذجهم",
                                        placeholder = "مثال: المتاجر المنافسة الكبرى التي تعتمد إعلانات تيك توك بكثافة",
                                        testTag = "input_market_competitors"
                                    )
                                    FormField(
                                        value = p3,
                                        onValueChange = { p3 = it },
                                        label = "نقطة التمايز التي تريد التركيز عليها",
                                        placeholder = "مثال: سرعة التوصيل في نفس اليوم + خدمة ما بعد البيع وضمان الاستبدال",
                                        testTag = "input_market_diff"
                                    )
                                }

                                TaskType.TRANSLATION -> {
                                    FormField(
                                        value = p1,
                                        onValueChange = { p1 = it },
                                        label = "النص المراد ترجمته وتوطينه",
                                        placeholder = "الصق النص بالفرنسية، الإنجليزية، أو العربية هنا...",
                                        minLines = 3,
                                        testTag = "input_trans_text"
                                    )
                                    FormField(
                                        value = p2,
                                        onValueChange = { p2 = it },
                                        label = "مسار التوطين المطلوب",
                                        placeholder = "مثال: من الفرنسية إلى الدارجة المغربية الراقية / من الإنجليزية للفصحى",
                                        testTag = "input_trans_dir"
                                    )
                                    FormField(
                                        value = p3,
                                        onValueChange = { p3 = it },
                                        label = "سياق ونبرة النص",
                                        placeholder = "مثال: مراسلة رسمية للشركات، أو إعلان تسويقي حماسي في السوشيال ميديا",
                                        testTag = "input_trans_tone"
                                    )
                                }

                                TaskType.CAMPAIGNS -> {
                                    FormField(
                                        value = p1,
                                        onValueChange = { p1 = it },
                                        label = "هدف الحملة الإعلانية (Campaign Goal)",
                                        placeholder = "مثال: رسائل واتساب لإغلاق الطلبيات، أو تحويلات مباشرة على المتجر",
                                        testTag = "input_camp_goal"
                                    )
                                    FormField(
                                        value = p2,
                                        onValueChange = { p2 = it },
                                        label = "الميزانية المخصصة للاختبار",
                                        placeholder = "مثال: 50 دولار للاختبار الأولي لمدة 3 أيام",
                                        testTag = "input_camp_budget"
                                    )
                                    FormField(
                                        value = p3,
                                        onValueChange = { p3 = it },
                                        label = "الجمهور المستهدف بدقة",
                                        placeholder = "مثال: النساء 22-45 سنة المهتمات بالديكور المنزلي في كبرى المدن",
                                        testTag = "input_camp_audience"
                                    )
                                    FormField(
                                        value = p4,
                                        onValueChange = { p4 = it },
                                        label = "المنصة الإعلانية",
                                        placeholder = "مثال: Facebook & Instagram Ads أو TikTok Ads",
                                        testTag = "input_camp_platform"
                                    )
                                }

                                TaskType.PROFIT_TRACKING -> {
                                    FormField(
                                        value = p1,
                                        onValueChange = { p1 = it },
                                        label = "إجمالي الإيرادات أو المبيعات (Revenue)",
                                        placeholder = "مثال: 15,000 درهم أو 1500 دولار",
                                        testTag = "input_profit_rev"
                                    )
                                    FormField(
                                        value = p2,
                                        onValueChange = { p2 = it },
                                        label = "تكلفة شراء المنتجات (COGS)",
                                        placeholder = "مثال: 4,000 درهم",
                                        testTag = "input_profit_cogs"
                                    )
                                    FormField(
                                        value = p3,
                                        onValueChange = { p3 = it },
                                        label = "الإنفاق الإعلاني الإجمالي (Ad Spend)",
                                        placeholder = "مثال: 3,500 درهم",
                                        testTag = "input_profit_ads"
                                    )
                                    FormField(
                                        value = p4,
                                        onValueChange = { p4 = it },
                                        label = "تكاليف الشحن ونسب الاسترجاع (Shipping & Returns)",
                                        placeholder = "مثال: 2,000 درهم مع نسبة تسليم 85%",
                                        testTag = "input_profit_shipping"
                                    )
                                }

                                TaskType.DEV_LAB -> {
                                    FormField(
                                        value = p1,
                                        onValueChange = { p1 = it },
                                        label = "الهدف والميزة البرمجية المطلوبة",
                                        placeholder = "مثال: بناء نظام دفع أو واجهة API آمنة، أو معمارية تطبيق أندرويد نظيفة",
                                        minLines = 2,
                                        testTag = "input_devlab_objective"
                                    )
                                    FormField(
                                        value = p2,
                                        onValueChange = { p2 = it },
                                        label = "التقنيات ولغة البرمجة (Tech Stack)",
                                        placeholder = "مثال: Kotlin / Jetpack Compose / Room، أو Python FastAPI، أو React / Node.js",
                                        testTag = "input_devlab_stack"
                                    )
                                    FormField(
                                        value = p3,
                                        onValueChange = { p3 = it },
                                        label = "القيود والمتطلبات الأمنية والأدائية",
                                        placeholder = "مثال: تقليل استهلاك API، حماية المفاتيح، وكتابة اختبارات وحدة كاملة",
                                        testTag = "input_devlab_constraints"
                                    )
                                }

                                TaskType.PROBLEM_SOLVER -> {
                                    FormField(
                                        value = p1,
                                        onValueChange = { p1 = it },
                                        label = "وصف العطل أو الخطأ (Error Description)",
                                        placeholder = "مثال: التطبيق ينهار عند فتح شاشة الدفع أو يرجع رمز خطأ 404/429",
                                        testTag = "input_solver_error"
                                    )
                                    FormField(
                                        value = p2,
                                        onValueChange = { p2 = it },
                                        label = "السجلات أو رسالة الخطأ الكاملة (Logs / Stacktrace)",
                                        placeholder = "الصق سطر الخطأ أو Stacktrace هنا (يتم حجب المفاتيح السرية تلقائياً)...",
                                        minLines = 3,
                                        testTag = "input_solver_logs"
                                    )
                                    FormField(
                                        value = p3,
                                        onValueChange = { p3 = it },
                                        label = "بيئة التشغيل والجزء المرتبط من الكود",
                                        placeholder = "مثال: Android SDK 35 + Retrofit + OkHttp أو Gradle 8",
                                        testTag = "input_solver_env"
                                    )
                                }

                                TaskType.IDEA_MATRIX -> {
                                    FormField(
                                        value = p1,
                                        onValueChange = { p1 = it },
                                        label = "الفكرة الأساسية / الكلمة المفتاحية",
                                        placeholder = "مثال: التجارة الإلكترونية بالمغرب، الذكاء الاصطناعي للمطاعم، العمل الحر للمصممين",
                                        minLines = 2,
                                        testTag = "input_matrix_idea"
                                    )
                                    FormField(
                                        value = p2,
                                        onValueChange = { p2 = it },
                                        label = "السياق أو الهدف الإضافي (اختياري)",
                                        placeholder = "مثال: إطلاق قناة محتوى تعليمي وبيع منتج رقمي خلال 30 يوماً",
                                        testTag = "input_matrix_context"
                                    )
                                    FormField(
                                        value = p3,
                                        onValueChange = { p3 = it },
                                        label = "زوايا سابقة يُمنع تكرارها (اختياري)",
                                        placeholder = "تُملأ تلقائياً عند توليد دفعة متشعبة جديدة لضمان عدم تكرار أي زاوية",
                                        minLines = 2,
                                        testTag = "input_matrix_exclusions"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Execute Button
                            Button(
                                onClick = {
                                    onExecute(p1, p2, p3, p4)
                                },
                                enabled = !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("execute_task_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ImperialGold,
                                    contentColor = ObsidianBlack
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = ObsidianBlack,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "أورانوس مهدي يحلل ويصيغ المخرجات...",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "تشغيل المهمة عبر أورانوس مهدي",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }

                // Error Card
                if (errorMessage != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "⚠️ $errorMessage",
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }

                // Streaming / Output Card
                if (streamingResult.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("task_result_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(ImperialGold)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "مخرجات ${task.title}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ImperialGoldLight
                                        )
                                    }

                                    Row {
                                        // Save to favorites
                                        IconButton(
                                            onClick = {
                                                onSaveToFavorites(task.title, "مهمة", streamingResult)
                                                Toast.makeText(context, "تم حفظ النتيجة في المفضلة ⭐", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.BookmarkBorder,
                                                contentDescription = "حفظ بالمفضلة",
                                                tint = ImperialGoldLight,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        // Copy
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Task Result", streamingResult))
                                                Toast.makeText(context, "تم نسخ النتيجة بالكامل", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "نسخ",
                                                tint = ImperialGoldLight,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = streamingResult,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        lineHeight = 22.sp,
                                        letterSpacing = 0.2.sp
                                    ),
                                    color = TextPrimary
                                )

                                if (task == TaskType.IDEA_MATRIX && !isLoading) {
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            val compactPrev = streamingResult
                                                .lineSequence()
                                                .filter { it.isNotBlank() }
                                                .take(8)
                                                .joinToString(" | ")
                                                .take(360)
                                            val updatedExclusions = if (p3.isBlank()) {
                                                compactPrev
                                            } else {
                                                "$p3\n---\n$compactPrev".takeLast(900)
                                            }
                                            p3 = updatedExclusions
                                            onExecute(p1, p2, updatedExclusions, p4)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .testTag("matrix_next_batch_button"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = RoyalPurplePrimary,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "توليد دفعة متشعبة جديدة (بدون تكرار)",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedButton(
                                        onClick = { onTransferToVideoStudio(streamingResult) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .testTag("transfer_to_studio_button"),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ImperialGoldLight)
                                    ) {
                                        Text(
                                            text = "نقل الأفكار والأوصاف إلى ستوديو الفيديو",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedButton(
                                    onClick = {
                                        onContinueInChat(task.title, streamingResult)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("continue_in_chat_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.7f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ImperialGold)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "متابعة النقاش والتطوير في الدردشة",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Task History Tab
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (history.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "لا يوجد سجل نتائج سابق لهذه المهمة بعد.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }

                items(history, key = { it.id }) { item ->
                    TaskHistoryCard(
                        historyItem = item,
                        onDelete = { onDeleteHistory(item.id) },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("History Result", item.resultText))
                            Toast.makeText(context, "تم نسخ النتيجة", Toast.LENGTH_SHORT).show()
                        },
                        onSaveFav = {
                            onSaveToFavorites(item.taskTitle, "مهمة سابقة", item.resultText)
                            Toast.makeText(context, "تمت الإضافة للمفضلة", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskHistoryCard(
    historyItem: TaskHistoryEntity,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onSaveFav: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormatted = remember(historyItem.timestamp) {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        sdf.format(Date(historyItem.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = historyItem.inputSummary.ifBlank { historyItem.taskTitle },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Row {
                    IconButton(onClick = onSaveFav, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = ImperialGoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(onClick = onCopy, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = ImperialGoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = historyItem.resultText,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = if (expanded) 100 else 3,
                lineHeight = 17.sp
            )

            Text(
                text = if (expanded) "▲ طي النتيجة" else "▼ عرض النتيجة كاملة",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = ImperialGold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    testTag: String,
    minLines: Int = 1
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 3.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(10.dp),
            minLines = minLines,
            maxLines = if (minLines > 1) 5 else 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurfaceElevated,
                unfocusedContainerColor = DarkSurfaceElevated,
                focusedBorderColor = ImperialGold,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = ImperialGold
            )
        )
    }
}
