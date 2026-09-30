package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.AnnouncementCategory
import com.example.data.AttendanceStatus
import com.example.data.EntryCategory
import com.example.data.PreschoolClass
import com.example.data.PreschoolEntry
import com.example.data.PreschoolStudent
import com.example.data.SchoolAnnouncement
import com.example.data.SchoolPortalSeedData
import com.example.data.StudentAttendanceRecord
import com.example.data.StudyResourceItem

@Composable
fun SchoolPortalDashboardSection(
    uiState: PreschoolUiState,
    onSelectSubTab: (SchoolPortalSubTab) -> Unit,
    onToggleRoleMode: (Boolean) -> Unit,
    onSelectAnnouncementCategory: (AnnouncementCategory?) -> Unit,
    onPostAnnouncement: (
        title: String,
        description: String,
        category: AnnouncementCategory,
        targetClassCode: String,
        scheduledDate: String,
        isPinned: Boolean
    ) -> Unit,
    onDeleteAnnouncement: (Int) -> Unit,
    onSelectAttendanceDate: (String) -> Unit,
    onLogStudentAttendance: (PreschoolStudent, AttendanceStatus) -> Unit,
    onMarkAllClassPresent: () -> Unit,
    onQuickRecordHomework: (title: String, description: String, subject: String, dueDate: String) -> Unit,
    onToggleHomeworkSubmitted: (PreschoolEntry) -> Unit,
    onSelectTimetableDay: (String) -> Unit,
    onUploadStudyResource: (
        subjectName: String,
        title: String,
        description: String,
        resourceLink: String,
        resourceType: String
    ) -> Unit,
    onDeleteStudyResource: (Int) -> Unit,
    onSelectStudent: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val navyPrimary = Color(0xFF1E3A8A)
    val slateSecondary = Color(0xFF334155)
    val classPalette = uiState.selectedClass.palette()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Role-Based View Header Card (Navy Blue & Slate Modern Dashboard Header)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            border = BorderStroke(1.dp, navyPrimary.copy(alpha = 0.18f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                navyPrimary.copy(alpha = 0.09f),
                                slateSecondary.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RainbowSchoolLogoBadge(size = 58.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Rainbow Pre-School Supa • Smart Portal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (uiState.isTeacherMode) {
                                    "Admin View: Post Announcements, Attendance, Homework & Resources"
                                } else {
                                    "Student / Parent View: Announcements, Exams, Timetable & Profile"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Role-Based View Segmented Switcher (Admin View vs Student/Parent View)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val isAdminSelected = uiState.isTeacherMode
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isAdminSelected) navyPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onToggleRoleMode(true) }
                                .testTag("portal_role_admin_tab")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = "Admin View",
                                    tint = if (isAdminSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "1. Admin / School View",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAdminSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isAdminSelected) navyPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onToggleRoleMode(false) }
                                .testTag("portal_role_student_parent_tab")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Student/Parent View",
                                    tint = if (!isAdminSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "2. Student / Parent View",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isAdminSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3 Interactive Portal Sub-Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SchoolPortalSubTab.entries.forEach { tab ->
                        val selected = uiState.selectedPortalSubTab == tab
                        val icon = when (tab) {
                            SchoolPortalSubTab.ANNOUNCEMENTS_EXAMS -> Icons.Default.Campaign
                            SchoolPortalSubTab.ATTENDANCE_TIMETABLE -> Icons.Default.HowToReg
                            SchoolPortalSubTab.RESOURCES_PROFILE -> Icons.Default.FolderSpecial
                        }
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectSubTab(tab) },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab.label,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = classPalette.accent,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            modifier = Modifier.testTag("portal_subtab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Render active Sub-Tab content
        when (uiState.selectedPortalSubTab) {
            SchoolPortalSubTab.ANNOUNCEMENTS_EXAMS -> {
                AnnouncementsAndExamsPanel(
                    uiState = uiState,
                    onSelectCategory = onSelectAnnouncementCategory,
                    onPostAnnouncement = onPostAnnouncement,
                    onDeleteAnnouncement = onDeleteAnnouncement
                )
            }

            SchoolPortalSubTab.ATTENDANCE_TIMETABLE -> {
                AttendanceTimetableAndHomeworkPanel(
                    uiState = uiState,
                    onSelectAttendanceDate = onSelectAttendanceDate,
                    onLogStudentAttendance = onLogStudentAttendance,
                    onMarkAllClassPresent = onMarkAllClassPresent,
                    onQuickRecordHomework = onQuickRecordHomework,
                    onToggleHomeworkSubmitted = onToggleHomeworkSubmitted,
                    onSelectTimetableDay = onSelectTimetableDay
                )
            }

            SchoolPortalSubTab.RESOURCES_PROFILE -> {
                StudyResourcesAndStudentProfilePanel(
                    uiState = uiState,
                    onUploadStudyResource = onUploadStudyResource,
                    onDeleteStudyResource = onDeleteStudyResource,
                    onSelectStudent = onSelectStudent
                )
            }
        }
    }
}

@Composable
private fun AnnouncementsAndExamsPanel(
    uiState: PreschoolUiState,
    onSelectCategory: (AnnouncementCategory?) -> Unit,
    onPostAnnouncement: (
        title: String,
        description: String,
        category: AnnouncementCategory,
        targetClassCode: String,
        scheduledDate: String,
        isPinned: Boolean
    ) -> Unit,
    onDeleteAnnouncement: (Int) -> Unit
) {
    val navyPrimary = Color(0xFF1E3A8A)
    var showCreateForm by remember { mutableStateOf(false) }
    var annTitle by remember { mutableStateOf("") }
    var annDesc by remember { mutableStateOf("") }
    var annCategory by remember { mutableStateOf(AnnouncementCategory.EVENTS) }
    var annDate by remember { mutableStateOf("15 Oct 2026") }
    var annTargetClass by remember { mutableStateOf("ALL") }
    var annPinned by remember { mutableStateOf(true) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Upcoming Exam Dates Highlight Banner (for Student/Parent & Admin)
        if (uiState.upcomingExams.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = navyPrimary
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = "Upcoming Exams",
                                tint = Color(0xFFFACC15),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upcoming Exam & Assessment Dates (${uiState.selectedClass.displayName})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = "${uiState.upcomingExams.size} Scheduled",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    uiState.upcomingExams.take(2).forEach { exam ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "📝 ${exam.title}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = exam.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFACC15)
                                ) {
                                    Text(
                                        text = exam.scheduledDate,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0F172A),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Admin View: Post General School Announcement Form
        if (uiState.isTeacherMode) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Post General School Announcement",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Broadcast Events, Exams, or Holidays to parents & students",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = { showCreateForm = !showCreateForm },
                            colors = ButtonDefaults.buttonColors(containerColor = navyPrimary),
                            modifier = Modifier.testTag("toggle_announcement_form_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (showCreateForm) "Hide Form" else "New Post")
                        }
                    }

                    AnimatedVisibility(visible = showCreateForm) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Select Announcement Category:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AnnouncementCategory.entries.forEach { cat ->
                                    FilterChip(
                                        selected = annCategory == cat,
                                        onClick = { annCategory = cat },
                                        label = { Text("${cat.badgeEmoji} ${cat.label}") },
                                        modifier = Modifier.testTag("post_cat_${cat.code.lowercase()}")
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = annTargetClass == "ALL",
                                    onClick = { annTargetClass = "ALL" },
                                    label = { Text("All Classes") }
                                )
                                FilterChip(
                                    selected = annTargetClass == uiState.selectedClass.code,
                                    onClick = { annTargetClass = uiState.selectedClass.code },
                                    label = { Text("${uiState.selectedClass.displayName} Only") }
                                )
                            }

                            OutlinedTextField(
                                value = annTitle,
                                onValueChange = { annTitle = it },
                                label = { Text("Announcement Title *") },
                                placeholder = { Text("e.g., Annual Sports Meet / Mid-Term Oral Exam") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("announcement_title_input")
                            )

                            OutlinedTextField(
                                value = annDate,
                                onValueChange = { annDate = it },
                                label = { Text("Event / Exam / Holiday Date") },
                                placeholder = { Text("e.g., 20 Oct 2026") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("announcement_date_input")
                            )

                            OutlinedTextField(
                                value = annDesc,
                                onValueChange = { annDesc = it },
                                label = { Text("Announcement Details") },
                                placeholder = { Text("Enter timings, instructions, or dress code...") },
                                minLines = 2,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("announcement_desc_input")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = annPinned,
                                        onCheckedChange = { annPinned = it }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Pin to top of Noticeboard",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (annTitle.isNotBlank()) {
                                            onPostAnnouncement(
                                                annTitle,
                                                annDesc,
                                                annCategory,
                                                annTargetClass,
                                                annDate,
                                                annPinned
                                            )
                                            annTitle = ""
                                            annDesc = ""
                                            showCreateForm = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = navyPrimary),
                                    modifier = Modifier.testTag("publish_announcement_button")
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Publish Notice")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Filter Row (All, Events, Exams, Holidays)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = uiState.selectedAnnouncementCategory == null,
                onClick = { onSelectCategory(null) },
                label = { Text("All Notices (${uiState.allAnnouncements.size})") },
                modifier = Modifier.testTag("filter_announcement_all")
            )
            AnnouncementCategory.entries.forEach { cat ->
                FilterChip(
                    selected = uiState.selectedAnnouncementCategory == cat,
                    onClick = { onSelectCategory(cat) },
                    label = { Text("${cat.badgeEmoji} ${cat.label}") },
                    modifier = Modifier.testTag("filter_announcement_${cat.code.lowercase()}")
                )
            }
        }

        // Announcements List
        uiState.filteredAnnouncements.forEach { announcement ->
            AnnouncementItemCard(
                announcement = announcement,
                isAdmin = uiState.isTeacherMode,
                onDelete = { onDeleteAnnouncement(announcement.id) }
            )
        }
    }
}

@Composable
private fun AnnouncementItemCard(
    announcement: SchoolAnnouncement,
    isAdmin: Boolean,
    onDelete: () -> Unit
) {
    val categoryColor = when (announcement.category) {
        AnnouncementCategory.EVENTS -> Color(0xFF0284C7) // Sky Blue
        AnnouncementCategory.EXAMS -> Color(0xFFD97706) // Amber
        AnnouncementCategory.HOLIDAYS -> Color(0xFF059669) // Emerald
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            width = if (announcement.isPinned) 1.5.dp else 1.dp,
            color = if (announcement.isPinned) categoryColor.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = categoryColor.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = "${announcement.category.badgeEmoji} ${announcement.category.label}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (announcement.targetClassCode == "ALL") "All Classes" else announcement.targetClassCode,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (announcement.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = categoryColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = categoryColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = announcement.scheduledDate,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = categoryColor
                            )
                        }
                    }

                    if (isAdmin) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Notice",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = announcement.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AttendanceTimetableAndHomeworkPanel(
    uiState: PreschoolUiState,
    onSelectAttendanceDate: (String) -> Unit,
    onLogStudentAttendance: (PreschoolStudent, AttendanceStatus) -> Unit,
    onMarkAllClassPresent: () -> Unit,
    onQuickRecordHomework: (title: String, description: String, subject: String, dueDate: String) -> Unit,
    onToggleHomeworkSubmitted: (PreschoolEntry) -> Unit,
    onSelectTimetableDay: (String) -> Unit
) {
    val navyPrimary = Color(0xFF1E3A8A)
    var hwTitle by remember { mutableStateOf("") }
    var hwSubject by remember { mutableStateOf("Literacy & Tracing") }
    var hwDue by remember { mutableStateOf("Tomorrow • 9:30 AM") }
    var hwDesc by remember { mutableStateOf("") }
    var showHwSavedBanner by remember { mutableStateOf(false) }

    val homeworkEntries = remember(uiState.allEntries, uiState.selectedClass) {
        uiState.allEntries.filter {
            it.classCode == uiState.selectedClass.code &&
                it.categoryCode == EntryCategory.HOMEWORK.code
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // 1. Admin View: Daily Student Attendance Roll-Call Logger & Quick Homework Form
        if (uiState.isTeacherMode) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, navyPrimary.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HowToReg,
                                contentDescription = "Attendance Roll Call",
                                tint = navyPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Daily Student Attendance (${uiState.selectedClass.displayName})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap Present, Absent, or Late for each student",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onMarkAllClassPresent,
                            modifier = Modifier.testTag("mark_all_present_button")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("All Present", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Date selector chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SchoolPortalSeedData.availableAttendanceDates.forEach { dateLabel ->
                            FilterChip(
                                selected = uiState.selectedAttendanceDate == dateLabel,
                                onClick = { onSelectAttendanceDate(dateLabel) },
                                label = { Text(dateLabel, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    // Attendance summary bar
                    val presentCount = uiState.classStudents.count {
                        (uiState.classAttendanceForDate[it.id]?.status ?: AttendanceStatus.PRESENT) == AttendanceStatus.PRESENT
                    }
                    val absentCount = uiState.classStudents.count {
                        uiState.classAttendanceForDate[it.id]?.status == AttendanceStatus.ABSENT
                    }
                    val lateCount = uiState.classStudents.count {
                        uiState.classAttendanceForDate[it.id]?.status == AttendanceStatus.LATE
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "✓ Present: $presentCount",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                            Text(
                                text = "⏰ Late: $lateCount",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                            Text(
                                text = "✕ Absent: $absentCount",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }

                    // Student Roll-Call Rows
                    uiState.classStudents.forEach { student ->
                        val currentStatus = uiState.classAttendanceForDate[student.id]?.status
                            ?: AttendanceStatus.PRESENT
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${student.rollNumber} • ${student.fullName}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Parent: ${student.parentName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AttendanceStatus.entries.forEach { st ->
                                    val isSelected = currentStatus == st
                                    val statusColor = when (st) {
                                        AttendanceStatus.PRESENT -> Color(0xFF059669)
                                        AttendanceStatus.LATE -> Color(0xFFD97706)
                                        AttendanceStatus.ABSENT -> Color(0xFFDC2626)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) statusColor else statusColor.copy(alpha = 0.1f),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onLogStudentAttendance(student, st) }
                                            .testTag("att_${student.rollNumber}_${st.code.lowercase()}")
                                    ) {
                                        Text(
                                            text = st.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else statusColor,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    }
                }
            }

            // Admin Form to Record Homework Assignment
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = "Record Homework",
                            tint = navyPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Record Homework Assignment (${uiState.selectedClass.displayName})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Assign daily home practice for students & parents",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = hwTitle,
                            onValueChange = {
                                hwTitle = it
                                showHwSavedBanner = false
                            },
                            label = { Text("Homework Title *") },
                            placeholder = { Text("e.g., Trace Letters A–D in Workbook") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_hw_title_input")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = hwSubject,
                            onValueChange = { hwSubject = it },
                            label = { Text("Subject") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = hwDue,
                            onValueChange = { hwDue = it },
                            label = { Text("Submission Due") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = hwDesc,
                        onValueChange = { hwDesc = it },
                        label = { Text("Instructions for Parents") },
                        placeholder = { Text("Page number, crayons needed, or oral revision notes...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showHwSavedBanner) {
                            Text(
                                text = "✓ Homework assigned to ${uiState.selectedClass.displayName}!",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF059669),
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        Button(
                            onClick = {
                                if (hwTitle.isNotBlank()) {
                                    onQuickRecordHomework(hwTitle, hwDesc, hwSubject, hwDue)
                                    hwTitle = ""
                                    hwDesc = ""
                                    showHwSavedBanner = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = navyPrimary),
                            modifier = Modifier.testTag("quick_save_homework_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Assign Homework")
                        }
                    }
                }
            }
        }

        // 2. Class Timetable Card (Interactive Monday–Friday Schedule)
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Class Timetable",
                        tint = navyPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${uiState.selectedClass.displayName} Class Timetable",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.selectedClass.roomName} • Select day to check schedule",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SchoolPortalSeedData.availableDays.forEach { day ->
                        FilterChip(
                            selected = uiState.selectedTimetableDay == day,
                            onClick = { onSelectTimetableDay(day) },
                            label = { Text(day) },
                            modifier = Modifier.testTag("timetable_day_${day.lowercase()}")
                        )
                    }
                }

                val slots = remember(uiState.selectedClass, uiState.selectedTimetableDay) {
                    SchoolPortalSeedData.getTimetableForClass(
                        uiState.selectedClass,
                        uiState.selectedTimetableDay
                    )
                }

                slots.forEach { slot ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = slot.subject,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = navyPrimary
                                )
                                Text(
                                    text = slot.activityTitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = slot.timeSlot,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = slot.roomLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Homework Submissions & Status Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FactCheck,
                            contentDescription = "Homework Submissions",
                            tint = Color(0xFF059669)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Homework Submissions (${uiState.selectedClass.displayName})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap status button to mark homework submitted",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFF059669).copy(alpha = 0.12f)
                    ) {
                        val submittedCount = homeworkEntries.count { it.isCompleted }
                        Text(
                            text = "$submittedCount / ${homeworkEntries.size} Submitted",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                homeworkEntries.forEach { hw ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (hw.isCompleted) {
                            Color(0xFF059669).copy(alpha = 0.08f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (hw.isCompleted) Color(0xFF059669).copy(alpha = 0.4f)
                            else Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = hw.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${hw.subjectTag} • Due: ${hw.scheduleOrDue}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (hw.isCompleted) Color(0xFF059669) else navyPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onToggleHomeworkSubmitted(hw) }
                            ) {
                                Text(
                                    text = if (hw.isCompleted) "✓ Submitted" else "Submit HW",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyResourcesAndStudentProfilePanel(
    uiState: PreschoolUiState,
    onUploadStudyResource: (
        subjectName: String,
        title: String,
        description: String,
        resourceLink: String,
        resourceType: String
    ) -> Unit,
    onDeleteStudyResource: (Int) -> Unit,
    onSelectStudent: (Int) -> Unit
) {
    val context = LocalContext.current
    val navyPrimary = Color(0xFF1E3A8A)
    var resSubject by remember { mutableStateOf("Phonics & Literacy") }
    var resTitle by remember { mutableStateOf("") }
    var resDesc by remember { mutableStateOf("") }
    var resLink by remember { mutableStateOf("https://rainbowpreschoolsupa.edu.in/resources/worksheet") }
    var resType by remember { mutableStateOf("PDF Worksheet") }

    val activeStudent = uiState.activeStudentForPractice ?: uiState.classStudents.firstOrNull()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // 1. Admin View: Upload Subject-Wise Study Resources or Links
        if (uiState.isTeacherMode) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, navyPrimary.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = "Upload Study Resource",
                            tint = navyPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Upload Subject-Wise Study Resource (${uiState.selectedClass.displayName})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Share PDF worksheets, rhyme audio links, or study guides",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Pre-Writing & Alphabet",
                            "Phonics & Literacy",
                            "Numbers & Math",
                            "EVS & General Awareness",
                            "Rhymes & Story"
                        ).forEach { subj ->
                            FilterChip(
                                selected = resSubject == subj,
                                onClick = { resSubject = subj },
                                label = { Text(subj, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = resTitle,
                        onValueChange = { resTitle = it },
                        label = { Text("Resource Title *") },
                        placeholder = { Text("e.g., Letter A–Z Tracing Practice PDF") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("resource_title_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = resLink,
                            onValueChange = { resLink = it },
                            label = { Text("Resource URL / Link") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("resource_link_input")
                        )
                        OutlinedTextField(
                            value = resType,
                            onValueChange = { resType = it },
                            label = { Text("Format") },
                            singleLine = true,
                            modifier = Modifier.width(140.dp)
                        )
                    }

                    OutlinedTextField(
                        value = resDesc,
                        onValueChange = { resDesc = it },
                        label = { Text("Resource Description") },
                        placeholder = { Text("Short note on how parents can use this study link...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (resTitle.isNotBlank()) {
                                onUploadStudyResource(resSubject, resTitle, resDesc, resLink, resType)
                                resTitle = ""
                                resDesc = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = navyPrimary),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("upload_resource_button")
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Resource")
                    }
                }
            }
        }

        // 2. Subject-Wise Study Resources List
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Study Resources",
                        tint = navyPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${uiState.selectedClass.displayName} Subject-Wise Study Resources",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.classStudyResources.size} study links & worksheets available",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                uiState.classStudyResources.forEach { res ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = navyPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "${res.subjectName} • ${res.resourceType}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = navyPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            runCatching {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(res.resourceLink))
                                                context.startActivity(intent)
                                            }
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                            contentDescription = "Open Link",
                                            tint = navyPrimary,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }
                                    if (uiState.isTeacherMode) {
                                        IconButton(
                                            onClick = { onDeleteStudyResource(res.id) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete Resource",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = res.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )

                            if (res.description.isNotBlank()) {
                                Text(
                                    text = res.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = navyPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = res.resourceLink,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = navyPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Student Profile Card: Attendance History & Subject-Wise Grade Reports
        if (activeStudent != null) {
            val studentSummary = uiState.studentMonthlySummaries.find { it.student.id == activeStudent.id }
            val gradeItems = remember(activeStudent, studentSummary) {
                SchoolPortalSeedData.getGradeReportsForStudent(
                    student = activeStudent,
                    solvedCount = studentSummary?.totalSolved ?: 2,
                    accuracyPercent = studentSummary?.accuracyPercent ?: 92
                )
            }

            val totalAttDays = uiState.studentAttendanceHistory.size.coerceAtLeast(1)
            val presentDays = uiState.studentAttendanceHistory.count {
                it.status == AttendanceStatus.PRESENT || it.status == AttendanceStatus.LATE
            }
            val attendancePercent = ((presentDays * 100f) / totalAttDays).toInt()

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, navyPrimary.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Grade,
                                contentDescription = "Student Profile",
                                tint = navyPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Student Profile: Attendance & Grade Report",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${activeStudent.rollNumber} • ${activeStudent.fullName} (${uiState.selectedClass.displayName})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Student switcher chips (if multiple students visible)
                    if (uiState.classStudents.size > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.classStudents.forEach { st ->
                                FilterChip(
                                    selected = st.id == activeStudent.id,
                                    onClick = { onSelectStudent(st.id) },
                                    label = { Text("${st.rollNumber} • ${st.fullName}") }
                                )
                            }
                        }
                    }

                    // Attendance History Summary Strip
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = navyPrimary.copy(alpha = 0.08f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Attendance History (${activeStudent.fullName})",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$attendancePercent% Attendance",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF059669)
                                )
                            }

                            LinearProgressIndicator(
                                progress = { (attendancePercent / 100f).coerceIn(0f, 1f) },
                                color = Color(0xFF059669),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(50))
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiState.studentAttendanceHistory.forEach { rec ->
                                    val badgeColor = when (rec.status) {
                                        AttendanceStatus.PRESENT -> Color(0xFF059669)
                                        AttendanceStatus.LATE -> Color(0xFFD97706)
                                        AttendanceStatus.ABSENT -> Color(0xFFDC2626)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = badgeColor.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "${rec.attendanceDate}: ${rec.status.label}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Subject-Wise Grade Reports
                    Text(
                        text = "Subject-Wise Grade Report Card",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    gradeItems.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.subject,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = item.assessmentTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = navyPrimary
                                    )
                                    Text(
                                        text = item.remarks,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = navyPrimary
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = item.gradeLetter,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${item.scorePercent}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
