package com.example.ui

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AccessControlConfig
import com.example.data.AccessRoleTier
import com.example.data.EntryCategory
import com.example.data.PlayBillingManager
import com.example.data.PlayStoreClassProduct
import com.example.data.PreschoolClass
import com.example.data.PreschoolEntry
import com.example.data.PreschoolRepository
import com.example.data.PreschoolStudent
import com.example.data.QuestionKind
import com.example.data.QuickTemplate
import com.example.data.ReportMonthOption
import com.example.data.StudentActivityLog
import com.example.data.SyllabusQuestion
import com.example.data.SyllabusSeedData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainPortalSection(val label: String) {
    CLASS_BOARD("Class Board"),
    SYLLABUS_TRACING("Tracing & Quiz"),
    MONTHLY_REPORT("Monthly Report")
}

enum class CategoryFilter(val label: String, val category: EntryCategory?) {
    ALL("All Updates", null),
    DAILY_TASK("Daily Tasks", EntryCategory.DAILY_TASK),
    HOMEWORK("Homework", EntryCategory.HOMEWORK),
    INSTRUCTION("Instructions", EntryCategory.INSTRUCTION)
}

data class ClassStats(
    val preschoolClass: PreschoolClass,
    val totalCount: Int,
    val dailyTaskCount: Int,
    val homeworkCount: Int,
    val instructionCount: Int,
    val completedCount: Int,
    val pinnedCount: Int,
    val syllabusQuestionCount: Int = 0,
    val studentCount: Int = 0
) {
    val completionFraction: Float
        get() = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount.toFloat()
}

data class StudentMonthlySummary(
    val student: PreschoolStudent,
    val monthOption: ReportMonthOption,
    val totalSolved: Int,
    val tracingSolved: Int,
    val mcqSolved: Int,
    val correctCount: Int,
    val totalStars: Int,
    val topicCounts: Map<String, Int>,
    val logs: List<StudentActivityLog>
) {
    val accuracyPercent: Int
        get() = if (totalSolved == 0) 0 else ((correctCount * 100f) / totalSolved).toInt()

    val badgeLabel: String
        get() = when {
            totalSolved >= 5 -> "Rainbow Star Champion"
            totalSolved >= 3 -> "Super Tracer & Solver"
            totalSolved >= 1 -> "Active Little Learner"
            else -> "Ready to Start"
        }
}

data class PreschoolUiState(
    val activeSection: MainPortalSection = MainPortalSection.CLASS_BOARD,
    val selectedClass: PreschoolClass = PreschoolClass.NURSERY,
    val lockedClass: PreschoolClass? = null,
    val selectedFilter: CategoryFilter = CategoryFilter.ALL,
    val searchQuery: String = "",
    val showPendingOrPinnedOnly: Boolean = false,
    val isTeacherMode: Boolean = true,
    val allEntries: List<PreschoolEntry> = emptyList(),
    val filteredEntries: List<PreschoolEntry> = emptyList(),
    val classStatsMap: Map<PreschoolClass, ClassStats> = emptyMap(),
    val isClassPortalChooserOpen: Boolean = false,
    // Syllabus & Tracing
    val selectedSyllabusTopic: String = "All Topics",
    val classSyllabusQuestions: List<SyllabusQuestion> = emptyList(),
    val filteredSyllabusQuestions: List<SyllabusQuestion> = emptyList(),
    val allStudents: List<PreschoolStudent> = emptyList(),
    val classStudents: List<PreschoolStudent> = emptyList(),
    val activeStudentForPractice: PreschoolStudent? = null,
    // Monthly Activity Report
    val selectedMonth: ReportMonthOption = SyllabusSeedData.availableMonths.first(),
    val studentMonthlySummaries: List<StudentMonthlySummary> = emptyList(),
    val classTotalSolvedThisMonth: Int = 0,
    val classTotalStarsThisMonth: Int = 0,
    // Class-Wise Parent Access & Play Store Purchase Config
    val accessConfig: AccessControlConfig = AccessControlConfig(),
    val billingStatusMessage: String = "Google Play Billing Ready"
) {
    val currentClassStats: ClassStats
        get() = classStatsMap[selectedClass] ?: ClassStats(
            preschoolClass = selectedClass,
            totalCount = 0,
            dailyTaskCount = 0,
            homeworkCount = 0,
            instructionCount = 0,
            completedCount = 0,
            pinnedCount = 0
        )

    val isCurrentClassUnlocked: Boolean
        get() = accessConfig.isClassUnlocked(selectedClass)
}

private data class FilterConfig(
    val activeSection: MainPortalSection = MainPortalSection.CLASS_BOARD,
    val selectedClass: PreschoolClass = PreschoolClass.NURSERY,
    val lockedClass: PreschoolClass? = null,
    val selectedFilter: CategoryFilter = CategoryFilter.ALL,
    val searchQuery: String = "",
    val showPendingOrPinnedOnly: Boolean = false,
    val isTeacherMode: Boolean = true,
    val isClassPortalChooserOpen: Boolean = false,
    val selectedSyllabusTopic: String = "All Topics",
    val activeStudentId: Int? = null,
    val selectedMonth: ReportMonthOption = SyllabusSeedData.availableMonths.first()
)

class PreschoolViewModel(
    private val repository: PreschoolRepository,
    appContext: Context
) : ViewModel() {

    private val filterConfig = MutableStateFlow(FilterConfig())
    private val latestAccessConfig = MutableStateFlow(AccessControlConfig())

    private val billingManager = PlayBillingManager(
        context = appContext,
        onProductUnlocked = { sku -> handlePlayStoreSkuUnlocked(sku) }
    )

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            repository.accessConfig.collect { cfg ->
                if (cfg != null) {
                    latestAccessConfig.value = cfg
                }
            }
        }
    }

    private val dataBundleFlow = combine(
        repository.allEntries,
        repository.allSyllabusQuestions,
        repository.allStudents,
        repository.allActivityLogs
    ) { entries, questions, students, logs ->
        DataBundle(entries, questions, students, logs)
    }

    private data class DataBundle(
        val entries: List<PreschoolEntry>,
        val questions: List<SyllabusQuestion>,
        val students: List<PreschoolStudent>,
        val logs: List<StudentActivityLog>
    )

    val uiState: StateFlow<PreschoolUiState> = combine(
        dataBundleFlow,
        filterConfig,
        latestAccessConfig,
        billingManager.billingStatusMessage
    ) { bundle, config, accessCfg, billingMsg ->
        val entries = bundle.entries
        val questions = bundle.questions
        val students = bundle.students
        val logs = bundle.logs

        // If a Parent or Class Teacher is logged in for a specific class, enforce ONLY that class
        val roleLockedClass = accessCfg.loggedInClass
        val activeClass = if (
            (accessCfg.isParentLoggedInForChild || accessCfg.isTeacherLoggedInForClass) &&
            roleLockedClass != null
        ) {
            roleLockedClass
        } else {
            config.lockedClass ?: config.selectedClass
        }

        val statsMap = PreschoolClass.entries.associateWith { cls ->
            val clsEntries = entries.filter { it.classCode == cls.code }
            val clsQuestions = questions.filter { it.classCode == cls.code }
            val clsStudents = students.filter { it.classCode == cls.code }
            ClassStats(
                preschoolClass = cls,
                totalCount = clsEntries.size,
                dailyTaskCount = clsEntries.count { it.categoryCode == EntryCategory.DAILY_TASK.code },
                homeworkCount = clsEntries.count { it.categoryCode == EntryCategory.HOMEWORK.code },
                instructionCount = clsEntries.count { it.categoryCode == EntryCategory.INSTRUCTION.code },
                completedCount = clsEntries.count { it.isCompleted },
                pinnedCount = clsEntries.count { it.isPinned },
                syllabusQuestionCount = clsQuestions.size,
                studentCount = clsStudents.size
            )
        }

        val query = config.searchQuery.trim().lowercase()

        val filteredEntries = entries.filter { entry ->
            val matchesClass = entry.classCode == activeClass.code
            val matchesCategory = config.selectedFilter.category == null ||
                entry.categoryCode == config.selectedFilter.category.code
            val matchesPendingOrPinned = !config.showPendingOrPinnedOnly ||
                (!entry.isCompleted || entry.isPinned)
            val matchesQuery = query.isEmpty() ||
                entry.title.lowercase().contains(query) ||
                entry.description.lowercase().contains(query) ||
                entry.subjectTag.lowercase().contains(query) ||
                entry.materialsNeeded.lowercase().contains(query)

            matchesClass && matchesCategory && matchesPendingOrPinned && matchesQuery
        }

        val classQuestions = questions.filter { it.classCode == activeClass.code }
        val filteredQuestions = classQuestions.filter { q ->
            config.selectedSyllabusTopic == "All Topics" || q.syllabusTopic == config.selectedSyllabusTopic
        }

        val fullClassStudents = students.filter { it.classCode == activeClass.code }
        // If parent is logged in for a specific child, restrict student list & report to ONLY that child
        val visibleStudentsForRole = if (
            accessCfg.isParentLoggedInForChild && accessCfg.loggedInStudentId > 0
        ) {
            fullClassStudents.filter { it.id == accessCfg.loggedInStudentId }
                .ifEmpty { fullClassStudents.take(1) }
        } else {
            fullClassStudents
        }

        val activeStudent = if (accessCfg.isParentLoggedInForChild && accessCfg.loggedInStudentId > 0) {
            visibleStudentsForRole.firstOrNull()
        } else {
            visibleStudentsForRole.find { it.id == config.activeStudentId }
                ?: visibleStudentsForRole.firstOrNull()
        }

        val monthLogsForClass = logs.filter {
            it.classCode == activeClass.code && it.monthKey == config.selectedMonth.key
        }

        val summaries = visibleStudentsForRole.map { student ->
            val studentLogs = monthLogsForClass.filter { it.studentId == student.id }
            val topicCounts = studentLogs.groupingBy { it.syllabusTopic }.eachCount()
            StudentMonthlySummary(
                student = student,
                monthOption = config.selectedMonth,
                totalSolved = studentLogs.size,
                tracingSolved = studentLogs.count { it.isTracingQuestion },
                mcqSolved = studentLogs.count { !it.isTracingQuestion },
                correctCount = studentLogs.count { it.isCorrect },
                totalStars = studentLogs.sumOf { it.starsEarned },
                topicCounts = topicCounts,
                logs = studentLogs
            )
        }.sortedByDescending { it.totalSolved }

        PreschoolUiState(
            activeSection = config.activeSection,
            selectedClass = activeClass,
            lockedClass = if (accessCfg.isParentLoggedInForChild || accessCfg.isTeacherLoggedInForClass) {
                roleLockedClass
            } else {
                config.lockedClass
            },
            selectedFilter = config.selectedFilter,
            searchQuery = config.searchQuery,
            showPendingOrPinnedOnly = config.showPendingOrPinnedOnly,
            isTeacherMode = accessCfg.hasTeacherOrAdminPrivileges && config.isTeacherMode,
            allEntries = entries,
            filteredEntries = filteredEntries,
            classStatsMap = statsMap,
            isClassPortalChooserOpen = config.isClassPortalChooserOpen,
            selectedSyllabusTopic = config.selectedSyllabusTopic,
            classSyllabusQuestions = classQuestions,
            filteredSyllabusQuestions = filteredQuestions,
            allStudents = students,
            classStudents = visibleStudentsForRole,
            activeStudentForPractice = activeStudent,
            selectedMonth = config.selectedMonth,
            studentMonthlySummaries = summaries,
            classTotalSolvedThisMonth = summaries.sumOf { it.totalSolved },
            classTotalStarsThisMonth = summaries.sumOf { it.totalStars },
            accessConfig = accessCfg,
            billingStatusMessage = billingMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PreschoolUiState()
    )

    fun selectPortalSection(section: MainPortalSection) {
        filterConfig.value = filterConfig.value.copy(activeSection = section)
    }

    fun selectClass(preschoolClass: PreschoolClass) {
        val accessCfg = latestAccessConfig.value
        if (
            (accessCfg.isParentLoggedInForChild || accessCfg.isTeacherLoggedInForClass) &&
            accessCfg.loggedInClass != preschoolClass
        ) {
            // Parent or class teacher is strictly locked to their assigned class
            return
        }
        filterConfig.value = filterConfig.value.copy(
            selectedClass = preschoolClass,
            lockedClass = null,
            selectedSyllabusTopic = "All Topics",
            activeStudentId = null
        )
    }

    fun setLockedClassAccess(preschoolClass: PreschoolClass?) {
        filterConfig.value = filterConfig.value.copy(
            selectedClass = preschoolClass ?: filterConfig.value.selectedClass,
            lockedClass = preschoolClass,
            isClassPortalChooserOpen = false,
            selectedSyllabusTopic = "All Topics",
            activeStudentId = null
        )
    }

    fun toggleClassPortalChooser(open: Boolean) {
        filterConfig.value = filterConfig.value.copy(isClassPortalChooserOpen = open)
    }

    fun selectCategoryFilter(filter: CategoryFilter) {
        filterConfig.value = filterConfig.value.copy(
            activeSection = MainPortalSection.CLASS_BOARD,
            selectedFilter = filter
        )
    }

    fun selectSyllabusTopic(topic: String) {
        filterConfig.value = filterConfig.value.copy(selectedSyllabusTopic = topic)
    }

    fun selectActiveStudent(studentId: Int) {
        filterConfig.value = filterConfig.value.copy(activeStudentId = studentId)
    }

    fun selectReportMonth(monthOption: ReportMonthOption) {
        filterConfig.value = filterConfig.value.copy(selectedMonth = monthOption)
    }

    fun updateSearchQuery(query: String) {
        filterConfig.value = filterConfig.value.copy(searchQuery = query)
    }

    fun togglePendingOrPinnedOnly() {
        filterConfig.value = filterConfig.value.copy(
            showPendingOrPinnedOnly = !filterConfig.value.showPendingOrPinnedOnly
        )
    }

    fun toggleRoleMode(isTeacher: Boolean) {
        filterConfig.value = filterConfig.value.copy(isTeacherMode = isTeacher)
    }

    // ==================== 1. FREE PARENT-CHILD LOGIN (CHILD -> CLASS -> FREE ACCESS TO CHILD'S CLASS) ====================
    fun loginParentForChildAndClass(
        targetClass: PreschoolClass,
        childStudent: PreschoolStudent,
        enteredCode: String = ""
    ): Boolean {
        val currentCfg = latestAccessConfig.value
        val expectedCode = currentCfg.codeForClass(targetClass)
        // Free parent access: allow instant login with blank code or matching class code
        if (enteredCode.isBlank() || enteredCode.equals(expectedCode, ignoreCase = true)) {
            val updatedCfg = currentCfg.copy(
                roleTierCode = AccessRoleTier.ENROLLED_PARENT.code,
                unlockedClassesCsv = targetClass.code,
                loggedInStudentId = childStudent.id,
                loggedInChildName = childStudent.fullName,
                loggedInChildRoll = childStudent.rollNumber,
                loggedInClassCode = targetClass.code
            )
            viewModelScope.launch {
                repository.updateAccessConfig(updatedCfg)
            }
            filterConfig.value = filterConfig.value.copy(
                selectedClass = targetClass,
                lockedClass = targetClass,
                activeStudentId = childStudent.id,
                isTeacherMode = false,
                isClassPortalChooserOpen = false
            )
            return true
        }
        return false
    }

    fun loginParentWithNewChildFree(
        targetClass: PreschoolClass,
        childFullName: String,
        rollNumber: String,
        parentName: String = "Parent"
    ) {
        val cleanName = childFullName.trim()
        if (cleanName.isBlank()) return
        val cleanRoll = rollNumber.trim().ifEmpty { "${targetClass.displayName.first()}-NEW" }
        val paletteColors = listOf(
            0xFFFF6B6B, 0xFF00A896, 0xFF6C5CE7, 0xFFFF9F1C, 0xFF118AB2, 0xFFEF476F
        )
        viewModelScope.launch {
            val newStudent = PreschoolStudent(
                classCode = targetClass.code,
                rollNumber = cleanRoll,
                fullName = cleanName,
                parentName = parentName.trim().ifEmpty { "Parent / Guardian" },
                avatarColorHex = paletteColors.random()
            )
            repository.saveStudent(newStudent)
            val currentCfg = latestAccessConfig.value
            val updatedCfg = currentCfg.copy(
                roleTierCode = AccessRoleTier.ENROLLED_PARENT.code,
                unlockedClassesCsv = targetClass.code,
                loggedInStudentId = newStudent.id,
                loggedInChildName = cleanName,
                loggedInChildRoll = cleanRoll,
                loggedInClassCode = targetClass.code
            )
            repository.updateAccessConfig(updatedCfg)
            filterConfig.value = filterConfig.value.copy(
                selectedClass = targetClass,
                lockedClass = targetClass,
                isTeacherMode = false,
                isClassPortalChooserOpen = false
            )
        }
    }

    // ==================== 2. TEACHER LOGIN (SEPARATE CLASS-WISE TEACHER ACCESS) ====================
    fun loginTeacherForClass(
        targetClass: PreschoolClass,
        enteredTeacherCode: String
    ): Boolean {
        val currentCfg = latestAccessConfig.value
        val expectedTeacherCode = currentCfg.teacherCodeForClass(targetClass)
        if (
            enteredTeacherCode.equals(expectedTeacherCode, ignoreCase = true) ||
            enteredTeacherCode == currentCfg.teacherPin
        ) {
            val updatedCfg = currentCfg.copy(
                roleTierCode = AccessRoleTier.CLASS_TEACHER.code,
                unlockedClassesCsv = targetClass.code,
                loggedInStudentId = 0,
                loggedInChildName = "",
                loggedInChildRoll = "",
                loggedInClassCode = targetClass.code
            )
            viewModelScope.launch {
                repository.updateAccessConfig(updatedCfg)
            }
            filterConfig.value = filterConfig.value.copy(
                selectedClass = targetClass,
                lockedClass = targetClass,
                activeStudentId = null,
                isTeacherMode = true,
                isClassPortalChooserOpen = false
            )
            return true
        }
        return false
    }

    // ==================== 3. ADMIN LOGIN (SEPARATE FULL SCHOOL ADMIN ACCESS) ====================
    fun loginSchoolAdmin(enteredPin: String): Boolean {
        val currentCfg = latestAccessConfig.value
        if (enteredPin.trim() == currentCfg.teacherPin) {
            val updatedCfg = currentCfg.copy(
                roleTierCode = AccessRoleTier.TEACHER_ADMIN.code,
                unlockedClassesCsv = "NURSERY,LKG,UKG",
                loggedInStudentId = 0,
                loggedInChildName = "",
                loggedInChildRoll = "",
                loggedInClassCode = ""
            )
            viewModelScope.launch {
                repository.updateAccessConfig(updatedCfg)
            }
            filterConfig.value = filterConfig.value.copy(
                lockedClass = null,
                activeStudentId = null,
                isTeacherMode = true
            )
            return true
        }
        return false
    }

    fun launchPlayStorePurchase(activity: Activity?, product: PlayStoreClassProduct) {
        billingManager.launchPurchaseFlow(activity, product)
    }

    fun restorePlayStorePurchases() {
        billingManager.restorePurchases()
    }

    private fun handlePlayStoreSkuUnlocked(sku: String) {
        val currentCfg = latestAccessConfig.value
        val newClasses = when (sku) {
            PlayBillingManager.SKU_NURSERY_PASS -> setOf(PreschoolClass.NURSERY)
            PlayBillingManager.SKU_LKG_PASS -> setOf(PreschoolClass.LKG)
            PlayBillingManager.SKU_UKG_PASS -> setOf(PreschoolClass.UKG)
            PlayBillingManager.SKU_ALL_CLASSES_BUNDLE -> PreschoolClass.entries.toSet()
            else -> emptySet()
        }
        val combinedClasses = (currentCfg.unlockedClasses + newClasses)
            .joinToString(",") { it.code }
        val updatedSkus = (currentCfg.playStorePurchasedSkusCsv.split(",") + sku)
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(",")

        val updatedCfg = currentCfg.copy(
            roleTierCode = AccessRoleTier.PLAY_STORE_USER.code,
            unlockedClassesCsv = combinedClasses,
            playStorePurchasedSkusCsv = updatedSkus,
            loggedInStudentId = 0,
            loggedInChildName = "",
            loggedInChildRoll = "",
            loggedInClassCode = ""
        )
        viewModelScope.launch {
            repository.updateAccessConfig(updatedCfg)
        }
        newClasses.firstOrNull()?.let { unlockedCls ->
            filterConfig.value = filterConfig.value.copy(selectedClass = unlockedCls)
        }
    }

    fun saveAdminPasscodes(
        nurseryCode: String,
        lkgCode: String,
        ukgCode: String,
        teacherPin: String,
        nurseryTeacherCode: String = latestAccessConfig.value.nurseryTeacherCode,
        lkgTeacherCode: String = latestAccessConfig.value.lkgTeacherCode,
        ukgTeacherCode: String = latestAccessConfig.value.ukgTeacherCode
    ) {
        val currentCfg = latestAccessConfig.value
        val updated = currentCfg.copy(
            nurseryParentCode = nurseryCode.trim().ifEmpty { "RPS-NUR-2026" },
            lkgParentCode = lkgCode.trim().ifEmpty { "RPS-LKG-2026" },
            ukgParentCode = ukgCode.trim().ifEmpty { "RPS-UKG-2026" },
            nurseryTeacherCode = nurseryTeacherCode.trim().ifEmpty { "TCH-NUR-101" },
            lkgTeacherCode = lkgTeacherCode.trim().ifEmpty { "TCH-LKG-102" },
            ukgTeacherCode = ukgTeacherCode.trim().ifEmpty { "TCH-UKG-103" },
            teacherPin = teacherPin.trim().ifEmpty { "9090" }
        )
        viewModelScope.launch {
            repository.updateAccessConfig(updated)
        }
    }

    fun switchAccessSimulationMode(
        tier: AccessRoleTier,
        specificParentClass: PreschoolClass?,
        specificStudent: PreschoolStudent? = null
    ) {
        val currentCfg = latestAccessConfig.value
        val targetClass = specificParentClass ?: PreschoolClass.NURSERY
        val unlockedCsv = when (tier) {
            AccessRoleTier.TEACHER_ADMIN -> "NURSERY,LKG,UKG"
            AccessRoleTier.CLASS_TEACHER -> targetClass.code
            AccessRoleTier.ENROLLED_PARENT -> targetClass.code
            AccessRoleTier.PLAY_STORE_USER -> ""
        }
        val updated = currentCfg.copy(
            roleTierCode = tier.code,
            unlockedClassesCsv = unlockedCsv,
            loggedInStudentId = if (tier == AccessRoleTier.ENROLLED_PARENT) {
                specificStudent?.id ?: 0
            } else 0,
            loggedInChildName = if (tier == AccessRoleTier.ENROLLED_PARENT) {
                specificStudent?.fullName ?: "${targetClass.displayName} Student"
            } else "",
            loggedInChildRoll = if (tier == AccessRoleTier.ENROLLED_PARENT) {
                specificStudent?.rollNumber ?: "${targetClass.displayName.first()}-01"
            } else "",
            loggedInClassCode = if (
                tier == AccessRoleTier.ENROLLED_PARENT || tier == AccessRoleTier.CLASS_TEACHER
            ) {
                targetClass.code
            } else ""
        )
        viewModelScope.launch {
            repository.updateAccessConfig(updated)
        }
        filterConfig.value = filterConfig.value.copy(
            selectedClass = targetClass,
            lockedClass = if (
                tier == AccessRoleTier.ENROLLED_PARENT || tier == AccessRoleTier.CLASS_TEACHER
            ) targetClass else null,
            activeStudentId = specificStudent?.id,
            isTeacherMode = (tier == AccessRoleTier.TEACHER_ADMIN || tier == AccessRoleTier.CLASS_TEACHER)
        )
    }

    // ==================== BOARD & SYLLABUS CRUD ====================
    fun saveEntry(
        id: Int = 0,
        preschoolClass: PreschoolClass,
        category: EntryCategory,
        title: String,
        description: String,
        subjectTag: String,
        scheduleOrDue: String,
        materialsNeeded: String,
        teacherName: String,
        isPinned: Boolean,
        isCompleted: Boolean = false
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.saveEntry(
                PreschoolEntry(
                    id = id,
                    classCode = preschoolClass.code,
                    categoryCode = category.code,
                    title = title.trim(),
                    description = description.trim(),
                    subjectTag = subjectTag.trim().ifEmpty { category.label },
                    scheduleOrDue = scheduleOrDue.trim().ifEmpty { "Today" },
                    materialsNeeded = materialsNeeded.trim(),
                    teacherName = teacherName.trim().ifEmpty { preschoolClass.defaultTeacher },
                    isPinned = isPinned,
                    isCompleted = isCompleted,
                    createdAt = System.currentTimeMillis()
                )
            )
            filterConfig.value = filterConfig.value.copy(
                selectedClass = preschoolClass,
                lockedClass = null
            )
        }
    }

    fun addFromTemplate(preschoolClass: PreschoolClass, template: QuickTemplate) {
        saveEntry(
            preschoolClass = preschoolClass,
            category = template.category,
            title = template.title,
            description = template.description,
            subjectTag = template.subjectTag,
            scheduleOrDue = template.scheduleOrDue,
            materialsNeeded = template.materialsNeeded,
            teacherName = preschoolClass.defaultTeacher,
            isPinned = template.isPinned
        )
    }

    fun toggleCompleted(entry: PreschoolEntry) {
        viewModelScope.launch {
            repository.toggleCompleted(entry)
        }
    }

    fun togglePinned(entry: PreschoolEntry) {
        viewModelScope.launch {
            repository.togglePinned(entry)
        }
    }

    fun deleteEntry(entryId: Int) {
        viewModelScope.launch {
            repository.deleteEntry(entryId)
        }
    }

    fun saveSyllabusQuestion(
        preschoolClass: PreschoolClass,
        syllabusTopic: String,
        questionKind: QuestionKind,
        tracingTarget: String,
        tracingHint: String,
        questionText: String,
        optionA: String,
        optionB: String,
        optionC: String,
        optionD: String,
        correctOptionIndex: Int,
        starsReward: Int = 3,
        photoUri: String = "",
        picturePresetCode: String = ""
    ) {
        if (questionText.isBlank()) return
        viewModelScope.launch {
            repository.saveSyllabusQuestion(
                SyllabusQuestion(
                    classCode = preschoolClass.code,
                    syllabusTopic = syllabusTopic.ifBlank { "General Syllabus" },
                    questionKindCode = questionKind.code,
                    tracingTarget = tracingTarget.trim().ifEmpty { "A" },
                    tracingHint = tracingHint.trim().ifEmpty { "Trace neatly along the dotted guide" },
                    questionText = questionText.trim(),
                    optionA = optionA.trim().ifEmpty { "Option A" },
                    optionB = optionB.trim().ifEmpty { "Option B" },
                    optionC = optionC.trim().ifEmpty { "Option C" },
                    optionD = optionD.trim().ifEmpty { "Option D" },
                    correctOptionIndex = correctOptionIndex.coerceIn(0, 3),
                    starsReward = starsReward.coerceIn(1, 5),
                    photoUri = photoUri.trim(),
                    picturePresetCode = picturePresetCode.trim()
                )
            )
            filterConfig.value = filterConfig.value.copy(
                selectedClass = preschoolClass,
                activeSection = MainPortalSection.SYLLABUS_TRACING
            )
        }
    }

    fun deleteSyllabusQuestion(questionId: Int) {
        viewModelScope.launch {
            repository.deleteSyllabusQuestion(questionId)
        }
    }

    fun addStudent(
        preschoolClass: PreschoolClass,
        rollNumber: String,
        fullName: String,
        parentName: String
    ) {
        if (fullName.isBlank()) return
        val paletteColors = listOf(
            0xFFFF6B6B, 0xFF00A896, 0xFF6C5CE7, 0xFFFF9F1C, 0xFF118AB2, 0xFFEF476F
        )
        viewModelScope.launch {
            repository.saveStudent(
                PreschoolStudent(
                    classCode = preschoolClass.code,
                    rollNumber = rollNumber.trim().ifEmpty { "${preschoolClass.displayName.first()}-NEW" },
                    fullName = fullName.trim(),
                    parentName = parentName.trim().ifEmpty { "Parent / Guardian" },
                    avatarColorHex = paletteColors.random()
                )
            )
        }
    }

    fun recordQuestionSolvedByStudent(
        student: PreschoolStudent,
        question: SyllabusQuestion,
        isCorrect: Boolean,
        monthOption: ReportMonthOption = filterConfig.value.selectedMonth
    ) {
        viewModelScope.launch {
            repository.recordStudentActivity(
                StudentActivityLog(
                    studentId = student.id,
                    classCode = student.classCode,
                    questionId = question.id,
                    questionTitle = if (question.questionKind == QuestionKind.TRACING_AND_MCQ) {
                        "Trace '${question.tracingTarget}' • ${question.syllabusTopic}"
                    } else {
                        question.questionText.take(42)
                    },
                    syllabusTopic = question.syllabusTopic,
                    isTracingQuestion = question.questionKind == QuestionKind.TRACING_AND_MCQ,
                    isCorrect = isCorrect,
                    starsEarned = if (isCorrect) question.starsReward else 1,
                    monthKey = monthOption.key,
                    monthLabel = monthOption.label,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
    }

    companion object {
        fun provideFactory(
            repository: PreschoolRepository,
            appContext: Context
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PreschoolViewModel(repository, appContext) as T
                }
            }
    }
}
