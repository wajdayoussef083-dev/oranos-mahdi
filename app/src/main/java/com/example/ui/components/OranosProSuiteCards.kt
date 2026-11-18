package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalmCyan
import com.example.ui.theme.CalmCyanBorder
import com.example.ui.theme.DarkDeepBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.ImperialGoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AlternativePaymentCard(
    globalCheckoutUrl: String,
    payoneerTarget: String,
    savedProReference: String,
    proVerifyStatus: String?,
    onOpenGlobalCheckout: (String) -> Unit,
    onSaveProReference: (String) -> Unit,
    onSaveChannelConfig: (String, String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var refInput by remember { mutableStateOf(savedProReference) }
    var payoneerCopyMsg by remember { mutableStateOf<String?>(null) }
    var globalActionMsg by remember { mutableStateOf<String?>(null) }
    var showChannelConfigDialog by remember { mutableStateOf(false) }

    LaunchedEffect(savedProReference) {
        if (refInput.isBlank() && savedProReference.isNotBlank()) {
            refInput = savedProReference
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("oranos_pro_alt_payment_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, ImperialGold.copy(alpha = 0.28f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = ImperialGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ORANOS MAHDI PRO — \$9.99",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(ImperialGold.copy(alpha = 0.16f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Payoneer + منصة عالمية",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImperialGoldLight,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { showChannelConfigDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("alt_payment_config_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "إعداد قنوات الاستلام",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "تم إيقاف PayPal واعتماد قنوات استلام مباشرة وخفيفة.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val useTwoColumns = maxWidth >= 460.dp
                if (useTwoColumns) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlobalPlatformBox(
                            modifier = Modifier.weight(1f),
                            globalCheckoutUrl = globalCheckoutUrl,
                            statusMessage = globalActionMsg,
                            onCheckoutClick = {
                                if (globalCheckoutUrl.isNotBlank()) {
                                    globalActionMsg = null
                                    onOpenGlobalCheckout(globalCheckoutUrl)
                                } else {
                                    globalActionMsg = "أضف رابط المنصة العالمية الصالح عبر أيقونة الضبط ⚙ أو Secrets."
                                    showChannelConfigDialog = true
                                }
                            }
                        )
                        PayoneerBox(
                            modifier = Modifier.weight(1f),
                            payoneerTarget = payoneerTarget,
                            copyStatus = payoneerCopyMsg,
                            onCopyClick = {
                                if (payoneerTarget.isNotBlank()) {
                                    clipboardManager.setText(AnnotatedString(payoneerTarget))
                                    payoneerCopyMsg = "تم نسخ بيانات Payoneer."
                                } else {
                                    payoneerCopyMsg = "أضف بريد أو معرف Payoneer عبر ⚙ أو Secrets."
                                    showChannelConfigDialog = true
                                }
                            }
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlobalPlatformBox(
                            modifier = Modifier.fillMaxWidth(),
                            globalCheckoutUrl = globalCheckoutUrl,
                            statusMessage = globalActionMsg,
                            onCheckoutClick = {
                                if (globalCheckoutUrl.isNotBlank()) {
                                    globalActionMsg = null
                                    onOpenGlobalCheckout(globalCheckoutUrl)
                                } else {
                                    globalActionMsg = "أضف رابط المنصة العالمية الصالح عبر أيقونة الضبط ⚙ أو Secrets."
                                    showChannelConfigDialog = true
                                }
                            }
                        )
                        PayoneerBox(
                            modifier = Modifier.fillMaxWidth(),
                            payoneerTarget = payoneerTarget,
                            copyStatus = payoneerCopyMsg,
                            onCopyClick = {
                                if (payoneerTarget.isNotBlank()) {
                                    clipboardManager.setText(AnnotatedString(payoneerTarget))
                                    payoneerCopyMsg = "تم نسخ بيانات Payoneer."
                                } else {
                                    payoneerCopyMsg = "أضف بريد أو معرف Payoneer عبر ⚙ أو Secrets."
                                    showChannelConfigDialog = true
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Payment Reference Confirmation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = refInput,
                    onValueChange = { refInput = it },
                    placeholder = {
                        Text(
                            text = "أدخل رقم المرجع أو البريد المستخدم في الدفع",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pro_ref_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkDeepBg,
                        unfocusedContainerColor = DarkDeepBg,
                        focusedBorderColor = ImperialGold,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = ImperialGold
                    )
                )

                Button(
                    onClick = { onSaveProReference(refInput) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImperialGold,
                        contentColor = ObsidianBlack
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                    modifier = Modifier.testTag("save_pro_ref_button")
                ) {
                    Text(
                        text = "حفظ طلب التفعيل",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            val displayStatus = proVerifyStatus ?: if (savedProReference.isNotBlank()) {
                "المرجع المحفوظ للمراجعة: $savedProReference"
            } else null

            if (!displayStatus.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = displayStatus,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (savedProReference.isNotBlank()) CalmCyan else ImperialGoldLight,
                    fontSize = 11.sp
                )
            }
        }
    }

    if (showChannelConfigDialog) {
        ChannelConfigDialog(
            initialGlobalUrl = globalCheckoutUrl,
            initialPayoneerTarget = payoneerTarget,
            onDismiss = { showChannelConfigDialog = false },
            onSave = { url, target ->
                onSaveChannelConfig(url, target)
                globalActionMsg = null
                payoneerCopyMsg = null
                showChannelConfigDialog = false
            }
        )
    }
}

@Composable
private fun GlobalPlatformBox(
    modifier: Modifier = Modifier,
    globalCheckoutUrl: String,
    statusMessage: String?,
    onCheckoutClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkDeepBg)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "الدفع عبر المنصة العالمية",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            fontSize = 13.sp
        )
        Text(
            text = "صفحة دفع جاهزة وآمنة لتفعيل PRO فوراً.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            fontSize = 11.sp
        )
        Button(
            onClick = onCheckoutClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ImperialGold,
                contentColor = ObsidianBlack
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("global_checkout_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "الانتقال للمنصة العالمية (\$9.99)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (globalCheckoutUrl.isBlank() && statusMessage == null) {
            Text(
                text = "بانتظار ضبط رابط المنصة العالمية في ⚙ أو GLOBAL_CHECKOUT_URL",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp
            )
        } else if (!statusMessage.isNullOrBlank()) {
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.labelSmall,
                color = CalmCyan,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun PayoneerBox(
    modifier: Modifier = Modifier,
    payoneerTarget: String,
    copyStatus: String?,
    onCopyClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkDeepBg)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "الاستلام عبر Payoneer",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            fontSize = 13.sp
        )
        Text(
            text = "انسخ بيانات Payoneer أو أرسل المرجع بعد التحويل.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            fontSize = 11.sp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DarkSurfaceElevated)
                .padding(horizontal = 9.dp, vertical = 7.dp)
        ) {
            Text(
                text = payoneerTarget.ifBlank { "لم يُضبط معرف Payoneer بعد (اضغط ⚙)" },
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = if (payoneerTarget.isNotBlank()) ImperialGoldLight else TextMuted,
                fontSize = 11.sp
            )
        }
        OutlinedButton(
            onClick = onCopyClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = DarkSurfaceElevated,
                contentColor = TextPrimary
            ),
            border = BorderStroke(1.dp, DarkSurfaceBorder),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("copy_payoneer_button")
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "نسخ بيانات Payoneer",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (!copyStatus.isNullOrBlank()) {
            Text(
                text = copyStatus,
                style = MaterialTheme.typography.labelSmall,
                color = CalmCyan,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ChannelConfigDialog(
    initialGlobalUrl: String,
    initialPayoneerTarget: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var urlInput by remember { mutableStateOf(initialGlobalUrl) }
    var payoneerInput by remember { mutableStateOf(initialPayoneerTarget) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "ضبط قنوات الاستلام المباشرة",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ImperialGoldLight
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "يمكنك ضبط الرابط والمعرف هنا محلياً أو عبر GLOBAL_CHECKOUT_URL و PAYONEER_TARGET في Secrets Panel.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text("رابط صفحة الدفع العالمية (https://...)", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ImperialGold,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                OutlinedTextField(
                    value = payoneerInput,
                    onValueChange = { payoneerInput = it },
                    label = { Text("بريد أو معرف Payoneer", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ImperialGold,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(urlInput, payoneerInput) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImperialGold,
                    contentColor = ObsidianBlack
                )
            ) {
                Text("حفظ", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextMuted)
            }
        }
    )
}

@Composable
fun QuietEngineerCard(
    taskDraft: String,
    outputText: String,
    isBusy: Boolean,
    errorText: String?,
    isKeyValid: Boolean,
    onDraftChange: (String) -> Unit,
    onRunEngineer: (String) -> Unit,
    onClearEngineer: () -> Unit,
    onOpenKeyDialog: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedFeedback by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiet_engineer_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, CalmCyanBorder.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = CalmCyan,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "المهندس — وضع التنفيذ الهادئ",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(CalmCyanBorder.copy(alpha = 0.16f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "مستقل • خفيف",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CalmCyan,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "يعمل في صمت وتركيز: يستقبل المهمة، يحدد المشكلة، ويعطي الحل أو الكود مباشرة.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = taskDraft,
                onValueChange = onDraftChange,
                placeholder = {
                    Text(
                        text = "اكتب للمهندس المهمة المطلوبة باختصار (مثال: إصلاح زر، ترتيب كود، تجهيز صفحة)...",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("engineer_task_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkDeepBg,
                    unfocusedContainerColor = DarkDeepBg,
                    focusedBorderColor = CalmCyanBorder,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = CalmCyan
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        copiedFeedback = false
                        onRunEngineer(taskDraft)
                    },
                    enabled = !isBusy,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImperialGold,
                        contentColor = ObsidianBlack
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
                    modifier = Modifier.testTag("run_engineer_button")
                ) {
                    if (isBusy) {
                        CircularProgressIndicator(
                            color = ObsidianBlack,
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = if (isBusy) "يعمل بهدوء..." else "تشغيل المهندس",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        copiedFeedback = false
                        onClearEngineer()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DarkSurfaceElevated,
                        contentColor = TextPrimary
                    ),
                    border = BorderStroke(1.dp, DarkSurfaceBorder),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
                    modifier = Modifier.testTag("clear_engineer_button")
                ) {
                    Text(
                        text = "تصفير هادئ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(outputText))
                        copiedFeedback = true
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("copy_engineer_output_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ مخرجات المهندس",
                        tint = if (copiedFeedback) SuccessGreen else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (errorText != null && !isKeyValid) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ImperialGold.copy(alpha = 0.14f))
                        .clickable { onOpenKeyDialog() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = ImperialGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "أدخل مفتاح Gemini API لتشغيل المهندس الهادئ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ImperialGoldLight,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkDeepBg)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .testTag("engineer_output_box")
            ) {
                Text(
                    text = outputText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
