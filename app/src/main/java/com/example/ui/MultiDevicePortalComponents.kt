package com.example.ui

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PreschoolClass
import com.example.data.PreschoolEntry

enum class DeviceLayoutMode(
    val label: String,
    val subtitle: String,
    val icon: ImageVector
) {
    AUTO(
        label = "Auto Responsive",
        subtitle = "Adapts automatically to Phone, Tablet, or Desktop screen width",
        icon = Icons.Default.Devices
    ),
    PHONE_COMPACT(
        label = "Android / iPhone View",
        subtitle = "Single-column handheld layout with bottom navigation bar",
        icon = Icons.Default.PhoneAndroid
    ),
    TABLET_SPLIT(
        label = "Tablet / iPad View",
        subtitle = "Side Navigation Rail + 2-pane class overview & board",
        icon = Icons.Default.TabletMac
    ),
    DESKTOP_WIDE(
        label = "Laptop / Desktop View",
        subtitle = "Widescreen command center with side rail & multi-column cards",
        icon = Icons.Default.Computer
    )
}

object CrossDeviceShareHelper {

    fun shareClassBoardSummary(
        context: Context,
        preschoolClass: PreschoolClass,
        entries: List<PreschoolEntry>
    ) {
        val classEntries = entries.filter { it.classCode == preschoolClass.code }
        val sb = StringBuilder()
        sb.appendLine("🌈 RAINBOW PRE-SCHOOL SUPA")
        sb.appendLine(SCHOOL_MOTTO)
        sb.appendLine("📞 Phone: $SCHOOL_PHONE | ✉️ $SCHOOL_EMAIL")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("📚 CLASS: ${preschoolClass.displayName} (${preschoolClass.ageGroup})")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━")

        if (classEntries.isEmpty()) {
            sb.appendLine("No updates posted yet for ${preschoolClass.displayName}.")
        } else {
            classEntries.forEachIndexed { index, item ->
                val status = if (item.isCompleted) "✅" else "📌"
                sb.appendLine("${index + 1}. $status [${item.category.label}] ${item.title}")
                sb.appendLine("   • Topic: ${item.subjectTag} (${item.scheduleOrDue})")
                sb.appendLine("   • Details: ${item.description}")
                if (item.materialsNeeded.isNotBlank()) {
                    sb.appendLine("   • Bring/Use: ${item.materialsNeeded}")
                }
                sb.appendLine()
            }
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Rainbow Pre-School Supa - ${preschoolClass.displayName} Daily Updates"
            )
            putExtra(Intent.EXTRA_TEXT, sb.toString())
        }
        runCatching {
            context.startActivity(
                Intent.createChooser(sendIntent, "Share ${preschoolClass.displayName} Updates")
            )
        }
    }

    fun shareMonthlyClassReport(
        context: Context,
        preschoolClass: PreschoolClass,
        monthLabel: String,
        summaries: List<StudentMonthlySummary>
    ) {
        val sb = StringBuilder()
        sb.appendLine("🌈 RAINBOW PRE-SCHOOL SUPA")
        sb.appendLine("📊 MONTHLY ACTIVITY REPORT - $monthLabel")
        sb.appendLine("📞 Phone: $SCHOOL_PHONE | ✉️ $SCHOOL_EMAIL")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("🏫 Class: ${preschoolClass.displayName} (${preschoolClass.ageGroup})")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━")

        summaries.forEachIndexed { index, summary ->
            sb.appendLine(
                "${index + 1}. ${summary.student.fullName} (${summary.student.rollNumber})"
            )
            sb.appendLine(
                "   • Total Activities Solved: ${summary.totalSolved} (Tracing: ${summary.tracingSolved}, Quiz: ${summary.mcqSolved})"
            )
            sb.appendLine(
                "   • Stars Earned: ⭐ ${summary.totalStars} | Accuracy: ${summary.accuracyPercent}%"
            )
            sb.appendLine("   • Status: ${summary.badgeLabel}")
            sb.appendLine()
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Rainbow Pre-School Supa - ${preschoolClass.displayName} Monthly Report ($monthLabel)"
            )
            putExtra(Intent.EXTRA_TEXT, sb.toString())
        }
        runCatching {
            context.startActivity(
                Intent.createChooser(sendIntent, "Share Monthly Activity Report")
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MultiDeviceAndShareBottomSheet(
    selectedClass: PreschoolClass,
    currentLayoutMode: DeviceLayoutMode,
    entries: List<PreschoolEntry>,
    monthLabel: String,
    studentSummaries: List<StudentMonthlySummary>,
    onSelectLayoutMode: (DeviceLayoutMode) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val classPalette = selectedClass.palette()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RainbowSchoolLogoBadge(size = 54.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Multi-Device & Cross-Platform Hub",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Android • iPhone • iPad & Tablets • Laptop & Desktop",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Live Layout Mode Switcher (Test Phone, Tablet, Laptop/Desktop views)
            Text(
                text = "1. Screen Layout Mode (Phone / Tablet / Laptop / Desktop)",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Switch layout density anytime or keep Auto Responsive for automatic screen adaptation:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DeviceLayoutMode.entries.forEach { mode ->
                    val isSelected = currentLayoutMode == mode
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) {
                            classPalette.softContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        },
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) classPalette.accent else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectLayoutMode(mode) }
                            .testTag("layout_mode_${mode.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = mode.icon,
                                contentDescription = mode.label,
                                tint = if (isSelected) classPalette.accent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mode.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (isSelected) classPalette.deepText else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = mode.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            // 2. Universal Share to iPhone / WhatsApp / Email / Laptop
            Text(
                text = "2. Send Updates & Reports to Any Device (iPhone / Laptop / WhatsApp / Email)",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        CrossDeviceShareHelper.shareClassBoardSummary(
                            context = context,
                            preschoolClass = selectedClass,
                            entries = entries
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = classPalette.accent),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_class_updates_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share ${selectedClass.displayName} Tasks",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }

                Button(
                    onClick = {
                        CrossDeviceShareHelper.shareMonthlyClassReport(
                            context = context,
                            preschoolClass = selectedClass,
                            monthLabel = monthLabel,
                            summaries = studentSummaries
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D2847)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_monthly_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share Monthly Report",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. How to use across Android, iPhone, Tablet, Laptop & Desktop
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "How to Access on Every Device:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    DeviceGuideItem(
                        icon = Icons.Default.PhoneAndroid,
                        title = "Android Phones & Android Tablets",
                        detail = "Download the APK/AAB from the AI Studio Settings menu to install directly on any Android phone or tablet."
                    )
                    DeviceGuideItem(
                        icon = Icons.Default.Computer,
                        title = "Laptop & Desktop (Windows, Mac, Chromebook)",
                        detail = "Open the Shared App URL in Chrome/Edge on any laptop or desktop, or run the APK natively on Chromebooks & Windows 11."
                    )
                    DeviceGuideItem(
                        icon = Icons.Default.PhoneIphone,
                        title = "iPhone & iPad (iOS / iPadOS)",
                        detail = "Open the Shared App URL in Safari on iPhone/iPad (Add to Home Screen) or share formatted Daily Tasks & Monthly Reports via WhatsApp/Email above."
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }
        }
    }
}

@Composable
private fun DeviceGuideItem(
    icon: ImageVector,
    title: String,
    detail: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
