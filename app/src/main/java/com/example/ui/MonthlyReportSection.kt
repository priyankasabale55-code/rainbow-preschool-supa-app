package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.PreschoolClass
import com.example.data.ReportMonthOption
import com.example.data.SyllabusSeedData
import com.example.ui.theme.HomeworkAmberContainer
import com.example.ui.theme.HomeworkAmberDark
import com.example.ui.theme.TaskMint
import com.example.ui.theme.TaskMintContainer
import com.example.ui.theme.TaskMintDark

@Composable
fun MonthlyReportOverviewCard(
    selectedClass: PreschoolClass,
    selectedMonth: ReportMonthOption,
    studentCount: Int,
    totalActivitiesSolved: Int,
    totalStarsEarned: Int,
    isTeacherMode: Boolean,
    onSelectMonth: (ReportMonthOption) -> Unit,
    onAddStudentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val classPalette = selectedClass.palette()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, classPalette.accent.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${selectedClass.displayName} Monthly Activity Report",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Monthly solved tracing, quizzes & star progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isTeacherMode) {
                    FilledTonalButton(
                        onClick = onAddStudentClick,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("report_add_student_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Student",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Student", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Month Picker Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Select Month",
                    tint = classPalette.accent,
                    modifier = Modifier.size(18.dp)
                )
                SyllabusSeedData.availableMonths.forEach { month ->
                    FilterChip(
                        selected = selectedMonth.key == month.key,
                        onClick = { onSelectMonth(month) },
                        label = { Text(month.label, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = classPalette.softContainer,
                            selectedLabelColor = classPalette.deepText
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3 Summary Metric Boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = classPalette.softContainer,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$studentCount",
                            style = MaterialTheme.typography.headlineSmall,
                            color = classPalette.deepText
                        )
                        Text(
                            text = "Students",
                            style = MaterialTheme.typography.labelSmall,
                            color = classPalette.deepText
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = TaskMintContainer,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$totalActivitiesSolved",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TaskMintDark
                        )
                        Text(
                            text = "Solved Activities",
                            style = MaterialTheme.typography.labelSmall,
                            color = TaskMintDark
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = HomeworkAmberContainer,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⭐ $totalStarsEarned",
                            style = MaterialTheme.typography.headlineSmall,
                            color = HomeworkAmberDark
                        )
                        Text(
                            text = "Total Stars",
                            style = MaterialTheme.typography.labelSmall,
                            color = HomeworkAmberDark
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentMonthlyReportCard(
    summary: StudentMonthlySummary,
    onViewDetailedReport: () -> Unit,
    onPracticeAsStudent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val student = summary.student
    val classPalette = student.preschoolClass.palette()
    val monthlyGoal = 5
    val goalProgress = (summary.totalSolved.toFloat() / monthlyGoal.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable { onViewDetailedReport() }
            .testTag("student_report_card_${student.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, classPalette.accent.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Student Initial Avatar
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(student.avatarColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.fullName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = student.fullName,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = classPalette.softContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = student.rollNumber,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = classPalette.deepText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${summary.badgeLabel} • Parent: ${student.parentName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Total Solved Counter Pill
                Surface(
                    color = TaskMintContainer,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${summary.totalSolved}",
                            style = MaterialTheme.typography.titleLarge,
                            color = TaskMintDark,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Solved",
                            style = MaterialTheme.typography.labelSmall,
                            color = TaskMintDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Breakdown Row: Tracing Solved, Quiz Solved, Accuracy, Stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Draw,
                            contentDescription = null,
                            tint = classPalette.accent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tracing: ${summary.tracingSolved}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = classPalette.accent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Quiz: ${summary.mcqSolved}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Surface(
                    color = Color(0xFFFFF3CD),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${summary.totalStars} Stars",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { goalProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(CircleShape),
                color = classPalette.accent,
                trackColor = classPalette.softContainer
            )

            if (summary.topicCounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    summary.topicCounts.forEach { (topic, count) ->
                        Surface(
                            color = classPalette.softContainer.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$topic: $count solved",
                                style = MaterialTheme.typography.labelSmall,
                                color = classPalette.deepText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onViewDetailedReport) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Report Card")
                }

                FilledTonalButton(
                    onClick = onPracticeAsStudent,
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Solve More Activities", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailedMonthlyReportSheet(
    summary: StudentMonthlySummary,
    onDismiss: () -> Unit,
    onPracticeNow: () -> Unit
) {
    BackHandler { onDismiss() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val student = summary.student
    val classPalette = student.preschoolClass.palette()

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
            // Official Rainbow Pre-School Supa Report Header
            Surface(
                color = Color(0xFF1D2847),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RainbowSchoolLogoBadge(size = 60.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rainbow Pre-School Supa",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = SCHOOL_MOTTO,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFF6C116)
                            )
                            Text(
                                text = "Ph: $SCHOOL_PHONE • $SCHOOL_EMAIL",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${student.fullName} (${student.rollNumber})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "Class: ${student.preschoolClass.displayName} • ${summary.monthOption.label}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            Surface(
                                color = Color(0xFFF6C116),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "${summary.totalSolved} Solved",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF1D2847),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Summary Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = classPalette.softContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${summary.tracingSolved}",
                            style = MaterialTheme.typography.titleLarge,
                            color = classPalette.deepText
                        )
                        Text(
                            text = "Tracing Tasks",
                            style = MaterialTheme.typography.labelSmall,
                            color = classPalette.deepText
                        )
                    }
                }
                Surface(
                    color = TaskMintContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${summary.mcqSolved}",
                            style = MaterialTheme.typography.titleLarge,
                            color = TaskMintDark
                        )
                        Text(
                            text = "Syllabus Quiz",
                            style = MaterialTheme.typography.labelSmall,
                            color = TaskMintDark
                        )
                    }
                }
                Surface(
                    color = HomeworkAmberContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⭐ ${summary.totalStars}",
                            style = MaterialTheme.typography.titleLarge,
                            color = HomeworkAmberDark
                        )
                        Text(
                            text = "Stars Earned",
                            style = MaterialTheme.typography.labelSmall,
                            color = HomeworkAmberDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Teacher Remark
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = classPalette.accent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Rainbow Pre-School Supa Monthly Progress Remark",
                            style = MaterialTheme.typography.labelMedium,
                            color = classPalette.accent
                        )
                        Text(
                            text = if (summary.totalSolved > 0) {
                                "${student.fullName} solved ${summary.totalSolved} syllabus activities (${summary.tracingSolved} tracing & ${summary.mcqSolved} option quizzes) with ${summary.accuracyPercent}% accuracy this month. Keep shining!"
                            } else {
                                "${student.fullName} has not logged any solved activities for ${summary.monthOption.label} yet. Tap 'Practice Now' below to start!"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Completed Activity Log (${summary.logs.size})",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (summary.logs.isEmpty()) {
                Text(
                    text = "No solved activities recorded for ${summary.monthOption.label} yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    summary.logs.forEach { log ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (log.isTracingQuestion) {
                                            Icons.Default.Draw
                                        } else {
                                            Icons.Default.CheckCircle
                                        },
                                        contentDescription = null,
                                        tint = TaskMint,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = log.questionTitle,
                                            style = MaterialTheme.typography.titleSmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = log.syllabusTopic,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFFFFF3CD),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "⭐ +${log.starsEarned}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF92400E),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Close")
                }
                Button(
                    onClick = {
                        onDismiss()
                        onPracticeNow()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = classPalette.accent),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Practice as ${student.fullName}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AddStudentDialog(
    selectedClass: PreschoolClass,
    onDismiss: () -> Unit,
    onSaveStudent: (rollNumber: String, fullName: String, parentName: String) -> Unit
) {
    var rollNumber by remember { mutableStateOf("${selectedClass.displayName.first()}-05") }
    var fullName by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add ${selectedClass.displayName} Student",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = rollNumber,
                    onValueChange = { rollNumber = it },
                    label = { Text("Roll Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Student Full Name *") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_name_input")
                )
                OutlinedTextField(
                    value = parentName,
                    onValueChange = { parentName = it },
                    label = { Text("Parent / Guardian Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isBlank()) {
                        nameError = true
                    } else {
                        onSaveStudent(rollNumber, fullName, parentName)
                        onDismiss()
                    }
                }
            ) {
                Text("Add Student")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
