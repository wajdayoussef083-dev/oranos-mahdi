package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ChatMessageEntity
import com.example.domain.model.AgentType
import com.example.domain.model.LanguageMode
import com.example.domain.model.ProfessionalMode
import com.example.ui.components.AlternativePaymentCard
import com.example.ui.components.ChatMessageItem
import com.example.ui.components.QuietEngineerCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.ImperialGoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    isOnline: Boolean,
    isKeyValid: Boolean,
    languageMode: LanguageMode,
    selectedAgent: AgentType,
    onSelectAgent: (AgentType) -> Unit,
    selectedMode: ProfessionalMode,
    onSelectMode: (ProfessionalMode) -> Unit,
    activeEngineLabel: String = "BRAIN V2 • جاهز للتوجيه الذكي",
    autoAgentRouting: Boolean = true,
    onToggleAutoAgentRouting: () -> Unit = {},
    executionStatus: String = "جاهز • سرعة قصوى ⚡",
    globalCheckoutUrl: String = "",
    payoneerTarget: String = "",
    savedProReference: String = "",
    proVerifyStatus: String? = null,
    onOpenGlobalCheckout: (String) -> Unit = {},
    onSaveProReference: (String) -> Unit = {},
    onSaveChannelConfig: (String, String) -> Unit = { _, _ -> },
    engineerTaskDraft: String = "",
    engineerOutput: String = "",
    isEngineerBusy: Boolean = false,
    engineerError: String? = null,
    onEngineerDraftChange: (String) -> Unit = {},
    onRunQuietEngineer: (String) -> Unit = {},
    onClearQuietEngineer: () -> Unit = {},
    messages: List<ChatMessageEntity>,
    isGenerating: Boolean,
    streamingText: String,
    chatError: String?,
    onSendMessage: (String) -> Unit,
    onNewChat: () -> Unit,
    onOpenKeyDialog: () -> Unit,
    onToggleMessageFavorite: (ChatMessageEntity) -> Unit,
    onSaveMessageToMemory: (ChatMessageEntity) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "thinkingPulse"
    )

    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty() || streamingText.isNotEmpty()) {
            val targetIdx = messages.size + (if (streamingText.isNotEmpty()) 1 else 0) + 1
            listState.animateScrollToItem(targetIdx.coerceAtLeast(0))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .imePadding()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Hero Eagle Crest Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_banner_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(
                                ImperialGold.copy(alpha = 0.5f),
                                RoyalPurplePrimary.copy(alpha = 0.4f),
                                DarkSurfaceBorder
                            )
                        )
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.hero_oranos),
                            contentDescription = "شعار النسر الملكي لأورانوس مهدي",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(145.dp),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(145.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            ObsidianBlack.copy(alpha = 0.5f),
                                            DarkSurface.copy(alpha = 0.96f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                                .align(Alignment.BottomStart)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ObsidianBlack.copy(alpha = 0.8f))
                                        .border(1.dp, ImperialGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = ImperialGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "المستشار الاستراتيجي الرجالي",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ImperialGoldLight,
                                        fontSize = 11.sp
                                    )
                                }

                                // Key Status Pill
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isKeyValid) SuccessGreen.copy(alpha = 0.15f)
                                            else ImperialGold.copy(alpha = 0.15f)
                                        )
                                        .clickable { onOpenKeyDialog() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(if (isKeyValid) SuccessGreen else ImperialGold)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isKeyValid) "Gemini متصل" else "إعداد المفتاح",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isKeyValid) SuccessGreen else ImperialGold,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "أورانوس مهدي | ORANOS MAHDI AI",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.3.sp
                                ),
                                color = TextPrimary,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            // Multi-Agent Architecture Bar
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = ImperialGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "منظومة الوكلاء المتخصصين (12 وكيلاً)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ImperialGoldLight,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = selectedAgent.agentName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = RoyalPurplePrimary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(AgentType.entries) { agent ->
                            val isSelected = agent == selectedAgent
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) ImperialGold.copy(alpha = 0.2f)
                                        else DarkSurface
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) ImperialGold else DarkSurfaceBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectAgent(agent) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = agent.badge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) ImperialGoldLight else TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = selectedAgent.roleDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ORANOS PROFESSIONAL BRAIN V2 — Multi-Engine Modes Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = RoyalPurplePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "ORANOS BRAIN V2",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = activeEngineLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImperialGold,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(ProfessionalMode.entries) { mode ->
                            val isModeSelected = mode == selectedMode
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isModeSelected) RoyalPurplePrimary.copy(alpha = 0.25f)
                                        else ObsidianBlack.copy(alpha = 0.6f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isModeSelected) RoyalPurplePrimary else DarkSurfaceBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onSelectMode(mode) }
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = mode.badge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isModeSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isModeSelected) TextPrimary else TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Auto-Agent Fast Execution Status & PayPal PRO Activation Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (autoAgentRouting) ImperialGold.copy(alpha = 0.18f)
                                    else ObsidianBlack.copy(alpha = 0.5f)
                                )
                                .border(
                                    1.dp,
                                    if (autoAgentRouting) ImperialGold else DarkSurfaceBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onToggleAutoAgentRouting() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "التوجيه التلقائي السريع",
                                tint = if (autoAgentRouting) ImperialGold else TextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (autoAgentRouting) "توجيه تلقائي ⚡" else "اختيار يدوي",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (autoAgentRouting) ImperialGoldLight else TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Text(
                            text = executionStatus,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Alternative Payment Card (Payoneer + Global Checkout — $9.99)
            item {
                AlternativePaymentCard(
                    globalCheckoutUrl = globalCheckoutUrl,
                    payoneerTarget = payoneerTarget,
                    savedProReference = savedProReference,
                    proVerifyStatus = proVerifyStatus,
                    onOpenGlobalCheckout = onOpenGlobalCheckout,
                    onSaveProReference = onSaveProReference,
                    onSaveChannelConfig = onSaveChannelConfig
                )
            }

            // Quiet Engineer Mode Card (المهندس — وضع التنفيذ الهادئ)
            item {
                QuietEngineerCard(
                    taskDraft = engineerTaskDraft,
                    outputText = engineerOutput,
                    isBusy = isEngineerBusy,
                    errorText = engineerError,
                    isKeyValid = isKeyValid,
                    onDraftChange = onEngineerDraftChange,
                    onRunEngineer = onRunQuietEngineer,
                    onClearEngineer = onClearQuietEngineer,
                    onOpenKeyDialog = onOpenKeyDialog
                )
            }

            // Notice if API key missing
            if (!isKeyValid) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenKeyDialog() },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = ImperialGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تفعيل الاتصال السحابي الحقيقي لـ Gemini",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ImperialGoldLight
                                )
                                Text(
                                    text = "اضغط هنا لإدخال مفتاح Google Gemini API المجاني الخاص بك للبدء فوراً.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Quick Starter Chips when messages are empty
            if (messages.isEmpty() && streamingText.isEmpty()) {
                item {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "بدايات مقترحة حسب بروتوكول العمل:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImperialGoldLight,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val chips = when (languageMode) {
                            LanguageMode.DARIJA -> listOf(
                                "كيفاش نقتنص أول عقد فريلانس بالدولار من المغرب؟",
                                "عطيني ستوري بورد كامل لفيديو ريلز فيروسي",
                                "شنو هما أحسن برامج التسويق بالعمولة للمبتدئين؟",
                                "حلل ليا فكرة مشروع تجارة إلكترونية بالمغرب مع دراسة جدوى"
                            )
                            LanguageMode.ENGLISH -> listOf(
                                "Build a complete digital business plan with unit economics",
                                "Write a high-converting Upwork proposal in USD",
                                "Create a viral Reels storyboard with B-roll prompts",
                                "Architect an Android Kotlin API integration workflow"
                            )
                            LanguageMode.FRENCH -> listOf(
                                "Construire un business plan e-commerce complet au Maroc",
                                "Stratégie pour décrocher des contrats freelance en USD",
                                "Scénario et storyboard complet pour une vidéo Reels",
                                "Analyser un marché et identifier les failles des concurrents"
                            )
                            else -> listOf(
                                "كيف أقتنص أول عقد عمل حر بالدولار على Upwork؟",
                                "سيناريو فيديو كامل مع ستوري بورد وموجهات بصرية",
                                "استراتيجية حملة إعلانات ممولة بميزانية 100 دولار",
                                "دراسة جدوى متكاملة لفرصة عمل رقمي مع حساب الأرباح"
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(chips) { chip ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkSurfaceElevated)
                                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                                        .clickable { onSendMessage(chip) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = chip,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Chat Messages
            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(
                    message = msg,
                    onToggleFavorite = { onToggleMessageFavorite(msg) },
                    onSaveToMemory = { onSaveMessageToMemory(msg) }
                )
            }

            // Streaming live response bubble
            if (streamingText.isNotEmpty()) {
                item {
                    ChatMessageItem(
                        message = ChatMessageEntity(
                            id = -99,
                            sessionId = 0,
                            sender = "oranos",
                            text = streamingText,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

            // Thinking & Analyzing Indicator
            if (isGenerating && streamingText.isEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(RoyalPurplePrimary.copy(alpha = 0.2f))
                                .border(1.dp, ImperialGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = ImperialGold,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${selectedAgent.agentName} يحلل ويفكر ويجهز الخطة التنفيذية...",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = ImperialGoldLight.copy(alpha = pulseAlpha),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Error notice with retry
            if (chatError != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تنبيه في الاتصال بالـ API:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ErrorRed
                                )
                                Text(
                                    text = chatError,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Home Bottom Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNewChat,
                    modifier = Modifier
                        .testTag("home_new_chat_button")
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "جلسة جديدة",
                        tint = ImperialGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (autoAgentRouting) {
                                when (languageMode) {
                                    LanguageMode.ENGLISH -> "Give the goal & Oranos auto-routes the best agent..."
                                    LanguageMode.FRENCH -> "Donnez l'objectif et Oranos choisit l'agent..."
                                    else -> "أعطه الهدف وهو يختار الوكيل ويجاوب بسرعة..."
                                }
                            } else {
                                when (languageMode) {
                                    LanguageMode.DARIJA -> "سول ${selectedAgent.agentName} بالدارجة..."
                                    LanguageMode.ENGLISH -> "Ask ${selectedAgent.agentName} in English..."
                                    LanguageMode.FRENCH -> "Demandez à ${selectedAgent.agentName} en français..."
                                    else -> "استشر ${selectedAgent.agentName} الآن..."
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_chat_input_field"),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = ImperialGold,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = ImperialGold
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank() && !isGenerating) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isGenerating) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank() && !isGenerating,
                    modifier = Modifier
                        .testTag("home_send_button")
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isGenerating) ImperialGold
                            else DarkSurfaceBorder
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "إرسال",
                        tint = if (inputText.isNotBlank() && !isGenerating) ObsidianBlack else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
