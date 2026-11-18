package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun OranosNoticeCard(
    modifier: Modifier = Modifier,
    isOffline: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        RoyalPurplePrimary.copy(alpha = 0.12f),
                        DarkSurfaceElevated,
                        ImperialGold.copy(alpha = 0.08f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = if (isOffline) ImperialGold.copy(alpha = 0.5f) else RoyalPurplePrimary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = if (isOffline) Icons.Default.Info else Icons.Default.Shield,
                contentDescription = null,
                tint = ImperialGold,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isOffline) "تنبيه: وضع العمل غير المتصل (Offline Mode)" else "شخصية أورانوس مهدي - المحرك المعرفي الذكي",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ImperialGold,
                    fontSize = 13.sp
                )
                Text(
                    text = if (isOffline)
                        "أنت حالياً غير متصل بالإنترنت. ما زال بإمكانك تصفح سجل المحادثات والذاكرة، واستخدام أدوات التحليل الأساسية."
                    else
                        "مساعد رجالي ذكي وواثق، يتقن الفصحى والدارجة المغربية. للاستفادة من كامل إمكانيات النموذج السحابي، يتطلب ذلك ترقية إعدادات Firebase AI Logic بالمنصة.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
