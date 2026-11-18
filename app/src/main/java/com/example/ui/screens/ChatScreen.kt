package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.DeleteSweep
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.domain.model.LanguageMode
import com.example.ui.components.ChatMessageItem
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.ImperialGoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoyalPurpleDark
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ChatScreen(
    messages: List<ChatMessageEntity>,
    isGenerating: Boolean,
    streamingText: String,
    languageMode: LanguageMode,
    onLanguageChange: (LanguageMode) -> Unit,
    onSendMessage: (String) -> Unit,
    onNewChat: () -> Unit,
    onClearChat: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom when messages or streaming text updates
    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty() || streamingText.isNotEmpty()) {
            listState.animateScrollToItem((messages.size + if (streamingText.isNotEmpty()) 1 else 0))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .imePadding()
    ) {
        // Chat Action Header Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Language selector tabs
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LanguageMode.entries.forEach { mode ->
                    val isSelected = mode == languageMode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) RoyalPurplePrimary.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .border(
                                1.dp,
                                if (isSelected) ImperialGold.copy(alpha = 0.6f) else DarkSurfaceBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onLanguageChange(mode) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = mode.badge,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isSelected) ImperialGold else TextMuted
                        )
                    }
                }
            }

            // Quick Actions: New Chat & Clear
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNewChat,
                    modifier = Modifier
                        .testTag("new_chat_button")
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "جلسة جديدة",
                        tint = ImperialGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier
                        .testTag("clear_chat_button")
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "مسح المحادثة",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            if (messages.isEmpty() && streamingText.isEmpty()) {
                item {
                    EmptyChatGreeting(
                        languageMode = languageMode,
                        onChipClick = { prompt ->
                            inputText = prompt
                            onSendMessage(prompt)
                            inputText = ""
                        }
                    )
                }
            }

            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(message = msg)
            }

            // Live streaming bubble
            if (streamingText.isNotEmpty()) {
                item {
                    ChatMessageItem(
                        message = ChatMessageEntity(
                            id = -1,
                            sessionId = 0,
                            sender = "oranos",
                            text = streamingText,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

            // Generating indicator
            if (isGenerating && streamingText.isEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = ImperialGold,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "أورانوس مهدي يفكر ويحلل صلب الموضوع...",
                            style = MaterialTheme.typography.bodySmall,
                            color = ImperialGoldLight,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (languageMode == LanguageMode.DARIJA) "اكتب سؤالك بالدارجة لأورانوس مهدي..."
                            else "اكتب استشارتك أو مسألتك لأورانوس مهدي...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = ImperialGold,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = ImperialGold
                    ),
                    maxLines = 4,
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
                        .testTag("send_message_button")
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isGenerating) ImperialGold
                            else DarkSurfaceBorder
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "إرسال الرسالة",
                        tint = if (inputText.isNotBlank() && !isGenerating) ObsidianBlack else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyChatGreeting(
    languageMode: LanguageMode,
    onChipClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(RoyalPurplePrimary.copy(alpha = 0.15f))
                .border(1.5.dp, ImperialGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = ImperialGold,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (languageMode == LanguageMode.DARIJA) "مرحبا بيك مع أورانوس مهدي" else "أهلاً بك مع أورانوس مهدي",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (languageMode == LanguageMode.DARIJA)
                "أنا واجد باش نجاوبك، نحللو أفكار مشاريعك، نكتبو محتوى، ونخدمو على استراتيجيات ذكية."
            else
                "أنا رهن إشارتك لاستشارات الأعمال، التحليل الاستراتيجي، وصناعة المحتوى عالي التأثير.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "اقتراحات لبدء النقاش فوراً:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = ImperialGoldLight,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val chips = if (languageMode == LanguageMode.DARIJA) {
            listOf(
                "عطيني خطة تسويق بالدارجة لمنتج في المغرب",
                "كيفاش نقنع كليان فـ الفريلانس يخدم معايا؟",
                "شنو هما أهم خطوات إطلاق متجر إلكتروني ناجح؟",
                "حلل ليا هاد الفكرة: بيع خدمات الذكاء الاصطناعي"
            )
        } else {
            listOf(
                "صغ لي استراتيجية تسويق رقمي لمشروع ناشئ",
                "كيف أحمي حقوقي المالية في عقود العمل الحر؟",
                "تحليل المنافسين وبناء ميزة تنافسية فريدة",
                "خطة عمل تنفيذية لمدة 90 يوماً لزيادة الإيرادات"
            )
        }

        chips.forEach { chip ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .clickable { onChipClick(chip) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = chip,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
