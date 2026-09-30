package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AccessRoleTier
import com.example.data.EntryCategory
import com.example.data.PreschoolEntry
import com.example.data.SyllabusQuestion

@Composable
fun RainbowPreschoolApp(
    viewModel: PreschoolViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddOrEditSheet by remember { mutableStateOf(false) }
    var sheetDefaultCategory by remember { mutableStateOf(EntryCategory.DAILY_TASK) }
    var editingEntry by remember { mutableStateOf<PreschoolEntry?>(null) }
    var selectedDetailEntry by remember { mutableStateOf<PreschoolEntry?>(null) }

    // Syllabus Tracing & Monthly Report modals
    var activePracticeQuestion by remember { mutableStateOf<SyllabusQuestion?>(null) }
    var showAddSyllabusQuestionSheet by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var selectedStudentReportSummary by remember { mutableStateOf<StudentMonthlySummary?>(null) }

    // Cross-Device / Adaptive Layout Mode (Auto, Phone, Tablet/iPad, Laptop/Desktop)
    var deviceLayoutMode by remember { mutableStateOf(DeviceLayoutMode.AUTO) }
    var showMultiDeviceHubSheet by remember { mutableStateOf(false) }

    // Parent Access Code & Play Store Purchase Sheet tab (0=Parent Code, 1=Play Store, 2=Admin)
    var accessSheetInitialTab by remember { mutableIntStateOf(0) }

    if (uiState.activeSection != MainPortalSection.CLASS_BOARD) {
        BackHandler {
            viewModel.selectPortalSection(MainPortalSection.CLASS_BOARD)
        }
    }

    val classPalette = uiState.selectedClass.palette()
    val isCurrentClassUnlocked = uiState.isCurrentClassUnlocked
    val isTeacherAdmin = uiState.accessConfig.roleTier == AccessRoleTier.TEACHER_ADMIN

    val onPrimaryActionClick = {
        if (!isCurrentClassUnlocked) {
            accessSheetInitialTab = 0
            viewModel.toggleClassPortalChooser(true)
        } else {
            when (uiState.activeSection) {
                MainPortalSection.CLASS_BOARD -> {
                    editingEntry = null
                    sheetDefaultCategory =
                        uiState.selectedFilter.category ?: EntryCategory.DAILY_TASK
                    showAddOrEditSheet = true
                }
                MainPortalSection.SYLLABUS_TRACING -> {
                    showAddSyllabusQuestionSheet = true
                }
                MainPortalSection.MONTHLY_REPORT -> {
                    showAddStudentDialog = true
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        val isWideScreen = when (deviceLayoutMode) {
            DeviceLayoutMode.AUTO -> maxWidth >= 600.dp
            DeviceLayoutMode.PHONE_COMPACT -> false
            DeviceLayoutMode.TABLET_SPLIT, DeviceLayoutMode.DESKTOP_WIDE -> true
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                PersistentSchoolTopHeader(
                    isTeacherMode = uiState.isTeacherMode,
                    lockedClass = uiState.lockedClass,
                    onOpenClassPortalChooser = {
                        accessSheetInitialTab = 0
                        viewModel.toggleClassPortalChooser(true)
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onPrimaryActionClick,
                    containerColor = classPalette.accent,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.testTag("add_entry_fab")
                ) {
                    Icon(
                        imageVector = if (!isCurrentClassUnlocked) {
                            Icons.Default.LockOpen
                        } else {
                            when (uiState.activeSection) {
                                MainPortalSection.CLASS_BOARD -> Icons.Default.Add
                                MainPortalSection.SYLLABUS_TRACING -> Icons.Default.Draw
                                MainPortalSection.MONTHLY_REPORT -> Icons.Default.PersonAdd
                            }
                        },
                        contentDescription = "Primary action"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!isCurrentClassUnlocked) {
                            "Unlock ${uiState.selectedClass.displayName}"
                        } else {
                            when (uiState.activeSection) {
                                MainPortalSection.CLASS_BOARD ->
                                    "Add ${uiState.selectedClass.displayName} Update"
                                MainPortalSection.SYLLABUS_TRACING ->
                                    "Add ${uiState.selectedClass.displayName} Question"
                                MainPortalSection.MONTHLY_REPORT ->
                                    "Add ${uiState.selectedClass.displayName} Student"
                            }
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        NavigationBarItem(
                            selected = uiState.activeSection == MainPortalSection.CLASS_BOARD,
                            onClick = { viewModel.selectPortalSection(MainPortalSection.CLASS_BOARD) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = "Class Board"
                                )
                            },
                            label = {
                                Text("Tasks & HW", style = MaterialTheme.typography.labelSmall)
                            },
                            modifier = Modifier.testTag("nav_section_board")
                        )

                        NavigationBarItem(
                            selected = uiState.activeSection == MainPortalSection.SYLLABUS_TRACING,
                            onClick = { viewModel.selectPortalSection(MainPortalSection.SYLLABUS_TRACING) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Draw,
                                    contentDescription = "Syllabus Tracing & Quiz"
                                )
                            },
                            label = {
                                Text("Tracing & Quiz", style = MaterialTheme.typography.labelSmall)
                            },
                            modifier = Modifier.testTag("nav_section_tracing")
                        )

                        NavigationBarItem(
                            selected = uiState.activeSection == MainPortalSection.MONTHLY_REPORT,
                            onClick = { viewModel.selectPortalSection(MainPortalSection.MONTHLY_REPORT) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Assessment,
                                    contentDescription = "Monthly Activity Report"
                                )
                            },
                            label = {
                                Text("Monthly Report", style = MaterialTheme.typography.labelSmall)
                            },
                            modifier = Modifier.testTag("nav_section_report")
                        )

                        NavigationBarItem(
                            selected = showMultiDeviceHubSheet,
                            onClick = { showMultiDeviceHubSheet = true },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Devices,
                                    contentDescription = "Devices & Share"
                                )
                            },
                            label = {
                                Text("Devices", style = MaterialTheme.typography.labelSmall)
                            },
                            modifier = Modifier.testTag("nav_section_devices")
                        )
                    }
                }
            }
        ) { innerPadding ->
            if (isWideScreen) {
                // Canonical Tablet / iPad / Laptop / Desktop Layout with NavigationRail + 2-Pane Split View
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        header = {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    ) {
                        NavigationRailItem(
                            selected = uiState.activeSection == MainPortalSection.CLASS_BOARD,
                            onClick = { viewModel.selectPortalSection(MainPortalSection.CLASS_BOARD) },
                            icon = {
                                Icon(Icons.Default.Dashboard, contentDescription = "Tasks & HW")
                            },
                            label = { Text("Tasks & HW") }
                        )
                        NavigationRailItem(
                            selected = uiState.activeSection == MainPortalSection.SYLLABUS_TRACING,
                            onClick = { viewModel.selectPortalSection(MainPortalSection.SYLLABUS_TRACING) },
                            icon = {
                                Icon(Icons.Default.Draw, contentDescription = "Tracing & Quiz")
                            },
                            label = { Text("Tracing") }
                        )
                        NavigationRailItem(
                            selected = uiState.activeSection == MainPortalSection.MONTHLY_REPORT,
                            onClick = { viewModel.selectPortalSection(MainPortalSection.MONTHLY_REPORT) },
                            icon = {
                                Icon(Icons.Default.Assessment, contentDescription = "Monthly Report")
                            },
                            label = { Text("Reports") }
                        )
                        NavigationRailItem(
                            selected = showMultiDeviceHubSheet,
                            onClick = { showMultiDeviceHubSheet = true },
                            icon = {
                                Icon(Icons.Default.Devices, contentDescription = "Devices & Share")
                            },
                            label = { Text("Devices") }
                        )
                    }

                    VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // Left Pane: Class Switcher + Class Overview
                    Column(
                        modifier = Modifier
                            .weight(0.42f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(top = 8.dp, bottom = 88.dp)
                    ) {
                        ParentChildActiveSessionBanner(
                            accessConfig = uiState.accessConfig,
                            onSwitchOrLogoutClick = {
                                accessSheetInitialTab = 0
                                viewModel.toggleClassPortalChooser(true)
                            }
                        )
                        ClassAccessSelectorRow(
                            selectedClass = uiState.selectedClass,
                            lockedClass = uiState.lockedClass,
                            statsMap = uiState.classStatsMap,
                            unlockedClasses = uiState.accessConfig.unlockedClasses,
                            isTeacherAdmin = isTeacherAdmin,
                            onSelectClass = { viewModel.selectClass(it) },
                            onOpenAccessSheet = {
                                accessSheetInitialTab = 0
                                viewModel.toggleClassPortalChooser(true)
                            }
                        )
                        if (isCurrentClassUnlocked) {
                            ClassOverviewAndQuickActionsCard(
                                selectedClass = uiState.selectedClass,
                                stats = uiState.currentClassStats,
                                isTeacherMode = uiState.isTeacherMode,
                                onQuickAddCategory = { category ->
                                    editingEntry = null
                                    sheetDefaultCategory = category
                                    showAddOrEditSheet = true
                                }
                            )
                        }
                    }

                    VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // Right Pane: Active Section Feed or Paywall if Class is Locked
                    LazyColumn(
                        modifier = Modifier
                            .weight(0.58f)
                            .fillMaxHeight()
                            .testTag("entries_lazy_column"),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
                    ) {
                        if (!isCurrentClassUnlocked) {
                            item {
                                LockedClassPaywallCard(
                                    selectedClass = uiState.selectedClass,
                                    accessConfig = uiState.accessConfig,
                                    onOpenUnlockSheet = { tab ->
                                        accessSheetInitialTab = tab
                                        viewModel.toggleClassPortalChooser(true)
                                    }
                                )
                            }
                        } else {
                            when (uiState.activeSection) {
                                MainPortalSection.CLASS_BOARD -> {
                                    item {
                                        BoardCategoryFilterAndSearch(
                                            selectedFilter = uiState.selectedFilter,
                                            onSelectFilter = { viewModel.selectCategoryFilter(it) },
                                            searchQuery = uiState.searchQuery,
                                            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                            showPendingOrPinnedOnly = uiState.showPendingOrPinnedOnly,
                                            onTogglePendingOrPinned = { viewModel.togglePendingOrPinnedOnly() },
                                            activeClassName = uiState.selectedClass.displayName,
                                            resultCount = uiState.filteredEntries.size,
                                            classAccent = classPalette.accent,
                                            classSoftBg = classPalette.softContainer,
                                            classDeepText = classPalette.deepText
                                        )
                                    }
                                    if (uiState.filteredEntries.isEmpty()) {
                                        item {
                                            EmptyClassFeedState(
                                                className = uiState.selectedClass.displayName,
                                                filterLabel = uiState.selectedFilter.label,
                                                onAddClick = onPrimaryActionClick
                                            )
                                        }
                                    } else {
                                        items(
                                            items = uiState.filteredEntries,
                                            key = { it.id }
                                        ) { entry ->
                                            PreschoolEntryCard(
                                                entry = entry,
                                                isTeacherMode = uiState.isTeacherMode,
                                                onCardClick = { selectedDetailEntry = entry },
                                                onToggleComplete = { viewModel.toggleCompleted(entry) },
                                                onTogglePin = { viewModel.togglePinned(entry) },
                                                onEditClick = {
                                                    editingEntry = entry
                                                    sheetDefaultCategory = entry.category
                                                    showAddOrEditSheet = true
                                                },
                                                onDeleteClick = { viewModel.deleteEntry(entry.id) }
                                            )
                                        }
                                    }
                                }

                                MainPortalSection.SYLLABUS_TRACING -> {
                                    item {
                                        SyllabusPracticeHeaderAndFilters(
                                            selectedClass = uiState.selectedClass,
                                            students = uiState.classStudents,
                                            activeStudent = uiState.activeStudentForPractice,
                                            selectedTopic = uiState.selectedSyllabusTopic,
                                            isTeacherMode = uiState.isTeacherMode,
                                            onSelectStudent = { viewModel.selectActiveStudent(it) },
                                            onSelectTopic = { viewModel.selectSyllabusTopic(it) },
                                            onAddQuestionClick = { showAddSyllabusQuestionSheet = true },
                                            onAddStudentClick = { showAddStudentDialog = true }
                                        )
                                    }
                                    items(
                                        items = uiState.filteredSyllabusQuestions,
                                        key = { it.id }
                                    ) { question ->
                                        SyllabusQuestionCard(
                                            question = question,
                                            activeStudentName = uiState.activeStudentForPractice?.fullName
                                                ?: "Select Student",
                                            isTeacherMode = uiState.isTeacherMode,
                                            onStartPractice = { activePracticeQuestion = question },
                                            onDeleteQuestion = { viewModel.deleteSyllabusQuestion(question.id) }
                                        )
                                    }
                                }

                                MainPortalSection.MONTHLY_REPORT -> {
                                    item {
                                        MonthlyReportOverviewCard(
                                            selectedClass = uiState.selectedClass,
                                            selectedMonth = uiState.selectedMonth,
                                            studentCount = uiState.classStudents.size,
                                            totalActivitiesSolved = uiState.classTotalSolvedThisMonth,
                                            totalStarsEarned = uiState.classTotalStarsThisMonth,
                                            isTeacherMode = uiState.isTeacherMode,
                                            onSelectMonth = { viewModel.selectReportMonth(it) },
                                            onAddStudentClick = { showAddStudentDialog = true }
                                        )
                                    }
                                    items(
                                        items = uiState.studentMonthlySummaries,
                                        key = { it.student.id }
                                    ) { summary ->
                                        StudentMonthlyReportCard(
                                            summary = summary,
                                            onViewDetailedReport = { selectedStudentReportSummary = summary },
                                            onPracticeAsStudent = {
                                                viewModel.selectActiveStudent(summary.student.id)
                                                viewModel.selectPortalSection(MainPortalSection.SYLLABUS_TRACING)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Compact Handheld Layout (Android & iPhone Portrait)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .testTag("entries_lazy_column"),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
                ) {
                    item {
                        ParentChildActiveSessionBanner(
                            accessConfig = uiState.accessConfig,
                            onSwitchOrLogoutClick = {
                                accessSheetInitialTab = 0
                                viewModel.toggleClassPortalChooser(true)
                            }
                        )
                    }

                    // 2. Class-Wise Access Cards (Nursery, LKG, UKG)
                    item {
                        ClassAccessSelectorRow(
                            selectedClass = uiState.selectedClass,
                            lockedClass = uiState.lockedClass,
                            statsMap = uiState.classStatsMap,
                            unlockedClasses = uiState.accessConfig.unlockedClasses,
                            isTeacherAdmin = isTeacherAdmin,
                            onSelectClass = { viewModel.selectClass(it) },
                            onOpenAccessSheet = {
                                accessSheetInitialTab = 0
                                viewModel.toggleClassPortalChooser(true)
                            }
                        )
                    }

                    if (!isCurrentClassUnlocked) {
                        item {
                            LockedClassPaywallCard(
                                selectedClass = uiState.selectedClass,
                                accessConfig = uiState.accessConfig,
                                onOpenUnlockSheet = { tab ->
                                    accessSheetInitialTab = tab
                                    viewModel.toggleClassPortalChooser(true)
                                }
                            )
                        }
                    } else {
                        when (uiState.activeSection) {
                            MainPortalSection.CLASS_BOARD -> {
                                item {
                                    ClassOverviewAndQuickActionsCard(
                                        selectedClass = uiState.selectedClass,
                                        stats = uiState.currentClassStats,
                                        isTeacherMode = uiState.isTeacherMode,
                                        onQuickAddCategory = { category ->
                                            editingEntry = null
                                            sheetDefaultCategory = category
                                            showAddOrEditSheet = true
                                        }
                                    )
                                }

                                item {
                                    BoardCategoryFilterAndSearch(
                                        selectedFilter = uiState.selectedFilter,
                                        onSelectFilter = { viewModel.selectCategoryFilter(it) },
                                        searchQuery = uiState.searchQuery,
                                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                        showPendingOrPinnedOnly = uiState.showPendingOrPinnedOnly,
                                        onTogglePendingOrPinned = { viewModel.togglePendingOrPinnedOnly() },
                                        activeClassName = uiState.selectedClass.displayName,
                                        resultCount = uiState.filteredEntries.size,
                                        classAccent = classPalette.accent,
                                        classSoftBg = classPalette.softContainer,
                                        classDeepText = classPalette.deepText
                                    )
                                }

                                if (uiState.filteredEntries.isEmpty()) {
                                    item {
                                        EmptyClassFeedState(
                                            className = uiState.selectedClass.displayName,
                                            filterLabel = uiState.selectedFilter.label,
                                            onAddClick = onPrimaryActionClick
                                        )
                                    }
                                } else {
                                    items(
                                        items = uiState.filteredEntries,
                                        key = { it.id }
                                    ) { entry ->
                                        PreschoolEntryCard(
                                            entry = entry,
                                            isTeacherMode = uiState.isTeacherMode,
                                            onCardClick = { selectedDetailEntry = entry },
                                            onToggleComplete = { viewModel.toggleCompleted(entry) },
                                            onTogglePin = { viewModel.togglePinned(entry) },
                                            onEditClick = {
                                                editingEntry = entry
                                                sheetDefaultCategory = entry.category
                                                showAddOrEditSheet = true
                                            },
                                            onDeleteClick = { viewModel.deleteEntry(entry.id) }
                                        )
                                    }
                                }
                            }

                            MainPortalSection.SYLLABUS_TRACING -> {
                                item {
                                    SyllabusPracticeHeaderAndFilters(
                                        selectedClass = uiState.selectedClass,
                                        students = uiState.classStudents,
                                        activeStudent = uiState.activeStudentForPractice,
                                        selectedTopic = uiState.selectedSyllabusTopic,
                                        isTeacherMode = uiState.isTeacherMode,
                                        onSelectStudent = { viewModel.selectActiveStudent(it) },
                                        onSelectTopic = { viewModel.selectSyllabusTopic(it) },
                                        onAddQuestionClick = { showAddSyllabusQuestionSheet = true },
                                        onAddStudentClick = { showAddStudentDialog = true }
                                    )
                                }

                                items(
                                    items = uiState.filteredSyllabusQuestions,
                                    key = { it.id }
                                ) { question ->
                                    SyllabusQuestionCard(
                                        question = question,
                                        activeStudentName = uiState.activeStudentForPractice?.fullName
                                            ?: "Select Student",
                                        isTeacherMode = uiState.isTeacherMode,
                                        onStartPractice = { activePracticeQuestion = question },
                                        onDeleteQuestion = { viewModel.deleteSyllabusQuestion(question.id) }
                                    )
                                }
                            }

                            MainPortalSection.MONTHLY_REPORT -> {
                                item {
                                    MonthlyReportOverviewCard(
                                        selectedClass = uiState.selectedClass,
                                        selectedMonth = uiState.selectedMonth,
                                        studentCount = uiState.classStudents.size,
                                        totalActivitiesSolved = uiState.classTotalSolvedThisMonth,
                                        totalStarsEarned = uiState.classTotalStarsThisMonth,
                                        isTeacherMode = uiState.isTeacherMode,
                                        onSelectMonth = { viewModel.selectReportMonth(it) },
                                        onAddStudentClick = { showAddStudentDialog = true }
                                    )
                                }

                                items(
                                    items = uiState.studentMonthlySummaries,
                                    key = { it.student.id }
                                ) { summary ->
                                    StudentMonthlyReportCard(
                                        summary = summary,
                                        onViewDetailedReport = { selectedStudentReportSummary = summary },
                                        onPracticeAsStudent = {
                                            viewModel.selectActiveStudent(summary.student.id)
                                            viewModel.selectPortalSection(MainPortalSection.SYLLABUS_TRACING)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Daily Task, Homework, or Instruction Sheet
    if (showAddOrEditSheet) {
        AddOrEditEntryBottomSheet(
            initialClass = uiState.selectedClass,
            initialCategory = sheetDefaultCategory,
            editingEntry = editingEntry,
            onDismiss = {
                showAddOrEditSheet = false
                editingEntry = null
            },
            onSaveEntry = { id, cls, cat, title, desc, subject, due, materials, teacher, pinned, completed ->
                viewModel.saveEntry(
                    id = id,
                    preschoolClass = cls,
                    category = cat,
                    title = title,
                    description = desc,
                    subjectTag = subject,
                    scheduleOrDue = due,
                    materialsNeeded = materials,
                    teacherName = teacher,
                    isPinned = pinned,
                    isCompleted = completed
                )
            }
        )
    }

    // Entry Detail Modal Bottom Sheet
    selectedDetailEntry?.let { currentEntry ->
        val liveEntry = uiState.allEntries.find { it.id == currentEntry.id } ?: currentEntry
        EntryDetailBottomSheet(
            entry = liveEntry,
            isTeacherMode = uiState.isTeacherMode,
            onDismiss = { selectedDetailEntry = null },
            onToggleComplete = { viewModel.toggleCompleted(liveEntry) },
            onTogglePin = { viewModel.togglePinned(liveEntry) },
            onEdit = {
                selectedDetailEntry = null
                editingEntry = liveEntry
                sheetDefaultCategory = liveEntry.category
                showAddOrEditSheet = true
            },
            onDelete = {
                viewModel.deleteEntry(liveEntry.id)
                selectedDetailEntry = null
            }
        )
    }

    // Interactive Finger Tracing & Multiple-Choice Question Sheet
    activePracticeQuestion?.let { question ->
        val practicingStudent = uiState.activeStudentForPractice ?: uiState.classStudents.firstOrNull()
        if (practicingStudent != null) {
            InteractiveTracingAndQuizBottomSheet(
                question = question,
                student = practicingStudent,
                onDismiss = { activePracticeQuestion = null },
                onCompletedActivity = { isCorrect ->
                    viewModel.recordQuestionSolvedByStudent(
                        student = practicingStudent,
                        question = question,
                        isCorrect = isCorrect
                    )
                }
            )
        }
    }

    // Add Syllabus Question Bottom Sheet
    if (showAddSyllabusQuestionSheet) {
        AddSyllabusQuestionBottomSheet(
            initialClass = uiState.selectedClass,
            onDismiss = { showAddSyllabusQuestionSheet = false },
            onSaveQuestion = { cls, topic, kind, traceTarget, traceHint, qText, optA, optB, optC, optD, correctIdx, stars, photoUri, presetCode ->
                viewModel.saveSyllabusQuestion(
                    preschoolClass = cls,
                    syllabusTopic = topic,
                    questionKind = kind,
                    tracingTarget = traceTarget,
                    tracingHint = traceHint,
                    questionText = qText,
                    optionA = optA,
                    optionB = optB,
                    optionC = optC,
                    optionD = optD,
                    correctOptionIndex = correctIdx,
                    starsReward = stars,
                    photoUri = photoUri,
                    picturePresetCode = presetCode
                )
            }
        )
    }

    // Add Student Dialog
    if (showAddStudentDialog) {
        AddStudentDialog(
            selectedClass = uiState.selectedClass,
            onDismiss = { showAddStudentDialog = false },
            onSaveStudent = { roll, name, parent ->
                viewModel.addStudent(
                    preschoolClass = uiState.selectedClass,
                    rollNumber = roll,
                    fullName = name,
                    parentName = parent
                )
            }
        )
    }

    // Student Detailed Monthly Report Sheet
    selectedStudentReportSummary?.let { currentSummary ->
        val liveSummary = uiState.studentMonthlySummaries.find {
            it.student.id == currentSummary.student.id
        } ?: currentSummary
        StudentDetailedMonthlyReportSheet(
            summary = liveSummary,
            onDismiss = { selectedStudentReportSummary = null },
            onPracticeNow = {
                viewModel.selectActiveStudent(liveSummary.student.id)
                viewModel.selectPortalSection(MainPortalSection.SYLLABUS_TRACING)
            }
        )
    }

    // Multi-Device & Cross-Platform Hub Sheet
    if (showMultiDeviceHubSheet) {
        MultiDeviceAndShareBottomSheet(
            selectedClass = uiState.selectedClass,
            currentLayoutMode = deviceLayoutMode,
            entries = uiState.allEntries,
            monthLabel = uiState.selectedMonth.label,
            studentSummaries = uiState.studentMonthlySummaries,
            onSelectLayoutMode = { deviceLayoutMode = it },
            onDismiss = { showMultiDeviceHubSheet = false }
        )
    }

    // Class-Wise Parent Access Code & Play Store Purchase Sheet
    if (uiState.isClassPortalChooserOpen) {
        ClassAccessAndPlayStoreBottomSheet(
            initialTab = accessSheetInitialTab,
            selectedClass = uiState.selectedClass,
            allStudents = uiState.allStudents,
            accessConfig = uiState.accessConfig,
            billingStatusMessage = uiState.billingStatusMessage,
            onLoginParentForChildAndClass = { cls, student, code ->
                viewModel.loginParentForChildAndClass(cls, student, code)
            },
            onLoginParentWithNewChildFree = { cls, childName, rollNum, parentName ->
                viewModel.loginParentWithNewChildFree(cls, childName, rollNum, parentName)
            },
            onPurchasePlayStoreProduct = { activity, product ->
                viewModel.launchPlayStorePurchase(activity, product)
            },
            onRestorePlayStorePurchases = {
                viewModel.restorePlayStorePurchases()
            },
            onSaveAdminPasscodes = { nurCode, lkgCode, ukgCode, pin ->
                viewModel.saveAdminPasscodes(nurCode, lkgCode, ukgCode, pin)
            },
            onSwitchAccessSimulationMode = { tier, specificClass, specificStudent ->
                viewModel.switchAccessSimulationMode(tier, specificClass, specificStudent)
            },
            onDismiss = { viewModel.toggleClassPortalChooser(false) }
        )
    }
}

@Composable
private fun BoardCategoryFilterAndSearch(
    selectedFilter: CategoryFilter,
    onSelectFilter: (CategoryFilter) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    showPendingOrPinnedOnly: Boolean,
    onTogglePendingOrPinned: () -> Unit,
    activeClassName: String,
    resultCount: Int,
    classAccent: Color,
    classSoftBg: Color,
    classDeepText: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Category Filter Chips Row (All Updates, Daily Tasks, Homework, Instructions)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryFilter.entries.forEach { filter ->
                val selected = selectedFilter == filter
                val icon = when (filter) {
                    CategoryFilter.ALL -> Icons.Default.Dashboard
                    CategoryFilter.DAILY_TASK -> Icons.Default.AddTask
                    CategoryFilter.HOMEWORK -> Icons.Default.MenuBook
                    CategoryFilter.INSTRUCTION -> Icons.Default.Campaign
                }
                FilterChip(
                    selected = selected,
                    onClick = { onSelectFilter(filter) },
                    label = { Text(filter.label, style = MaterialTheme.typography.labelMedium) },
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = filter.label,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = classSoftBg,
                        selectedLabelColor = classDeepText,
                        selectedLeadingIconColor = classAccent
                    ),
                    modifier = Modifier.testTag("filter_tab_${filter.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Search $activeClassName tasks, homework, or instructions…",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search entries"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$activeClassName • ${selectedFilter.label} ($resultCount)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            FilterChip(
                selected = showPendingOrPinnedOnly,
                onClick = onTogglePendingOrPinned,
                label = {
                    Text(
                        text = "Pending / Pinned Only",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter pending or pinned",
                        modifier = Modifier.size(15.dp)
                    )
                },
                modifier = Modifier.testTag("pending_filter_chip")
            )
        }
    }
}

@Composable
private fun EmptyClassFeedState(
    className: String,
    filterLabel: String,
    onAddClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = "Empty class board",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No $filterLabel for $className Yet",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap the button below to post a new Daily Task, Homework worksheet, or Parent Instruction for $className.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add First Entry for $className")
            }
        }
    }
}
