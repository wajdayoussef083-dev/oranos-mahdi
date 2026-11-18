package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VideoCameraBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TaskType
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.ImperialGoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TasksScreen(
    onSelectTask: (TaskType) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(ImperialGold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مركز مهام الذكاء الاصطناعي (11 مهمة مستقلة)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ImperialGoldLight
                        )
                    }
                    Text(
                        text = "أدوات تنفيذية حقيقية مدعومة بمحركات ORANOS PROFESSIONAL BRAIN V2. كل مهمة لها واجهة إدخال وسجل نتائج خاص بها.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        items(TaskType.entries) { task ->
            TaskItemCard(task = task, onClick = { onSelectTask(task) })
        }
    }
}

@Composable
fun TaskItemCard(
    task: TaskType,
    onClick: () -> Unit
) {
    val (icon, accentColor) = when (task) {
        TaskType.DOLLAR_JOBS -> Pair(Icons.Default.AttachMoney, Color(0xFF10B981))
        TaskType.VIDEO_CONTENT -> Pair(Icons.Default.VideoCameraBack, Color(0xFFEC4899))
        TaskType.AFFILIATE -> Pair(Icons.AutoMirrored.Filled.TrendingUp, Color(0xFFF59E0B))
        TaskType.WINNING_PRODUCTS -> Pair(Icons.Default.ShoppingBag, Color(0xFF8B5CF6))
        TaskType.MARKET_ANALYSIS -> Pair(Icons.Default.Analytics, Color(0xFF38BDF8))
        TaskType.TRANSLATION -> Pair(Icons.Default.Translate, Color(0xFFA855F7))
        TaskType.CAMPAIGNS -> Pair(Icons.Default.Campaign, Color(0xFFF97316))
        TaskType.PROFIT_TRACKING -> Pair(Icons.Default.Calculate, Color(0xFFEAB308))
        TaskType.DEV_LAB -> Pair(Icons.Default.Code, Color(0xFF06B6D4))
        TaskType.PROBLEM_SOLVER -> Pair(Icons.Default.Build, Color(0xFFEF4444))
        TaskType.IDEA_MATRIX -> Pair(Icons.Default.AutoAwesome, Color(0xFFC084FC))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.name.lowercase()}")
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(
                    accentColor.copy(alpha = 0.45f),
                    DarkSurfaceBorder,
                    DarkSurfaceBorder.copy(alpha = 0.2f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = accentColor,
                            fontSize = 9.sp
                        )
                    }
                }

                Text(
                    text = task.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 1.dp)
                )

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 3.dp),
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "فتح المهمة",
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
