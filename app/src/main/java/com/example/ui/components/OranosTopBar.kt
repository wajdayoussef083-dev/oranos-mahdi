package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.network.GeminiApiClient
import com.example.domain.model.LanguageMode
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
fun OranosTopBar(
    title: String = "ORANOS MAHDI AI",
    subtitle: String? = "المساعد الذكي الاستشاري",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    isOnline: Boolean = true,
    isKeyValid: Boolean = GeminiApiClient.hasValidApiKey(),
    currentLanguageMode: LanguageMode = LanguageMode.ARABIC,
    onLanguageChange: (LanguageMode) -> Unit = {},
    onSaveApiKey: ((String) -> Unit)? = null,
    onTestApiKey: ((String, (Boolean, String) -> Unit) -> Unit)? = null,
    externalShowKeyDialog: Boolean = false,
    onKeyDialogDismissed: () -> Unit = {}
) {
    val context = LocalContext.current
    var showLangMenu by remember { mutableStateOf(false) }
    var internalShowApiKeyDialog by remember { mutableStateOf(false) }
    val showApiKeyDialog = internalShowApiKeyDialog || externalShowKeyDialog
    var apiKeyInput by remember { mutableStateOf(GeminiApiClient.getEffectiveApiKey()) }
    var isTestingKey by remember { mutableStateOf(false) }
    var testResultMsg by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    val isApiConnected = isKeyValid

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ObsidianBlack,
                        DarkSurface
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        DarkSurfaceBorder.copy(alpha = 0.2f),
                        ImperialGold.copy(alpha = 0.4f),
                        RoyalPurplePrimary.copy(alpha = 0.3f),
                        DarkSurfaceBorder.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .testTag("back_button")
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DarkSurface)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = ImperialGold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Eagle Crest Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, ImperialGold, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_oranos_eagle),
                    contentDescription = "شعار نسر أورانوس مهدي",
                    modifier = Modifier.size(42.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Connectivity dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isOnline) SuccessGreen else ErrorRed)
                    )
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            // API Key Status / Config Button
            IconButton(
                onClick = { internalShowApiKeyDialog = true },
                modifier = Modifier
                    .testTag("api_key_settings_button")
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isApiConnected) SuccessGreen.copy(alpha = 0.15f) else ImperialGold.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "إعداد مفتاح API",
                    tint = if (isApiConnected) SuccessGreen else ImperialGold,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Language Selector Pill
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(RoyalPurplePrimary.copy(alpha = 0.15f))
                        .border(1.dp, RoyalPurplePrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { showLangMenu = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "تغيير اللهجة واللغة",
                        tint = ImperialGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentLanguageMode.badge,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = ImperialGold
                    )
                }

                DropdownMenu(
                    expanded = showLangMenu,
                    onDismissRequest = { showLangMenu = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    LanguageMode.entries.forEach { mode ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = mode.title,
                                    color = if (mode == currentLanguageMode) ImperialGold else TextPrimary,
                                    fontWeight = if (mode == currentLanguageMode) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onLanguageChange(mode)
                                showLangMenu = false
                            }
                        )
                    }
                }
            }
        }
    }

    if (showApiKeyDialog) {
        AlertDialog(
            onDismissRequest = {
                internalShowApiKeyDialog = false
                onKeyDialogDismissed()
            },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = ImperialGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ربط مفتاح Google Gemini API",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "يرتبط أورانوس مهدي بنموذج Gemini 3.8 Flash السحابي لتقديم ردود وتحليلات ذكية وفورية. يمكنك لصق مفتاح API الخاص بك هنا أو ضبطه عبر Secrets Panel.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("Gemini API Key", color = ImperialGold) },
                        placeholder = { Text("AIzaSy...", color = TextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("api_key_input_field"),
                        shape = RoundedCornerShape(12.dp),
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isApiConnected) "✅ الاتصال السحابي بالـ API مفعل ونشط."
                        else "⚠️ لتشغيل محرك ORANOS ACTIVATOR PRO الفعلي، يرجى إدخال مفتاح Google Gemini API صالح.",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isApiConnected) SuccessGreen else ImperialGoldLight
                    )

                    if (onTestApiKey != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = {
                                if (!isTestingKey && apiKeyInput.isNotBlank()) {
                                    isTestingKey = true
                                    testResultMsg = null
                                    onTestApiKey(apiKeyInput) { ok, msg ->
                                        isTestingKey = false
                                        testResultMsg = Pair(ok, msg)
                                    }
                                }
                            },
                            enabled = !isTestingKey && apiKeyInput.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ImperialGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isTestingKey) "جارٍ اختبار الاتصال بالخادم..." else "اختبار الاتصال الفعلي بالـ API",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    testResultMsg?.let { (ok, msg) ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (ok) SuccessGreen else ErrorRed,
                            lineHeight = 16.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (onSaveApiKey != null) {
                            onSaveApiKey(apiKeyInput)
                        } else {
                            GeminiApiClient.saveApiKey(context, apiKeyInput)
                        }
                        internalShowApiKeyDialog = false
                        onKeyDialogDismissed()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImperialGold,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حفظ وتفعيل", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        internalShowApiKeyDialog = false
                        onKeyDialogDismissed()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("إغلاق")
                }
            }
        )
    }
}
