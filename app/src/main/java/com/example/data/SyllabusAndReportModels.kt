package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class QuestionKind(val code: String, val label: String) {
    TRACING_AND_MCQ("TRACING_AND_MCQ", "Tracing + MCQ Option"),
    PHOTO_QUIZ("PHOTO_QUIZ", "Photo / Picture Quiz"),
    SYLLABUS_MCQ("SYLLABUS_MCQ", "Syllabus Multiple Choice");

    companion object {
        fun fromCode(code: String): QuestionKind =
            entries.find { it.code == code } ?: TRACING_AND_MCQ
    }
}

enum class PictureQuizPreset(
    val code: String,
    val title: String,
    val visualArt: String,
    val caption: String,
    val bgHex: Long,
    val accentHex: Long
) {
    APPLE("APPLE", "Red Apple", "🍎", "A for Apple • Sweet Red Fruit", 0xFFFFEBEE, 0xFFE53935),
    MANGO("MANGO", "Yellow Mango", "🥭", "M for Mango • King of Fruits", 0xFFFFF8E1, 0xFFFF8F00),
    LION("LION", "Lion (Wild Animal)", "🦁", "L for Lion • King of the Jungle", 0xFFFFF3E0, 0xFFEF6C00),
    ELEPHANT("ELEPHANT", "Big Elephant", "🐘", "E for Elephant • Largest Land Animal", 0xFFECEFF1, 0xFF546E7A),
    COW("COW", "Domestic Cow", "🐄", "C for Cow • Farm Animal Gives Milk", 0xFFE8F5E9, 0xFF2E7D32),
    BUTTERFLIES("BUTTERFLIES", "8 Butterflies", "🦋🦋🦋🦋\n🦋🦋🦋🦋", "Count the Colorful Butterflies (1 to 8)", 0xFFF3E5F5, 0xFF8E24AA),
    APPLES_ADD("APPLES_ADD", "4 + 3 Apples", "🍎🍎🍎🍎  +  🍎🍎🍎", "Picture Addition: 4 Apples + 3 Apples", 0xFFFFEBEE, 0xFFD32F2F),
    RAINY_UMBRELLA("RAINY_UMBRELLA", "Rainy Season", "🌧️ ☔ 🌈", "Umbrella & Raincoat in Monsoon", 0xFFE3F2FD, 0xFF1565C0),
    SCHOOL_BUS("SCHOOL_BUS", "Yellow School Bus", "🚌", "B for Bus • Road Transport Vehicle", 0xFFFFFDE7, 0xFFF9A825),
    SUN_CIRCLE("SUN_CIRCLE", "Round Sun Circle", "☀️ ⭕", "Round Circle Shape with Zero Corners", 0xFFFFF8E1, 0xFFF57F17);

    companion object {
        fun fromCode(code: String): PictureQuizPreset? =
            entries.find { it.code == code }
    }
}

@Entity(tableName = "syllabus_questions")
data class SyllabusQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val classCode: String,
    val syllabusTopic: String,
    val questionKindCode: String,
    val tracingTarget: String, // e.g. "|", "—", "A", "3", "CAT"
    val tracingHint: String,   // e.g. "Trace top to bottom along the dotted line"
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0 = A, 1 = B, 2 = C, 3 = D
    val starsReward: Int = 2,
    val photoUri: String = "",
    val picturePresetCode: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val preschoolClass: PreschoolClass
        get() = PreschoolClass.fromCode(classCode)

    val questionKind: QuestionKind
        get() = QuestionKind.fromCode(questionKindCode)

    val options: List<String>
        get() = listOf(optionA, optionB, optionC, optionD)

    val picturePreset: PictureQuizPreset?
        get() = PictureQuizPreset.fromCode(picturePresetCode)

    val hasPhotoOrPicture: Boolean
        get() = photoUri.isNotBlank() || picturePreset != null
}

@Entity(tableName = "preschool_students")
data class PreschoolStudent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val classCode: String,
    val rollNumber: String,
    val fullName: String,
    val parentName: String,
    val avatarColorHex: Long = 0xFFFF6B6B
) {
    val preschoolClass: PreschoolClass
        get() = PreschoolClass.fromCode(classCode)
}

@Entity(tableName = "student_activity_logs")
data class StudentActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val classCode: String,
    val questionId: Int,
    val questionTitle: String,
    val syllabusTopic: String,
    val isTracingQuestion: Boolean,
    val isCorrect: Boolean,
    val starsEarned: Int,
    val monthKey: String, // e.g. "2026-09"
    val monthLabel: String, // e.g. "September 2026"
    val completedAt: Long = System.currentTimeMillis()
)

data class ReportMonthOption(
    val key: String,
    val label: String
)

enum class AccessRoleTier(val code: String, val label: String) {
    TEACHER_ADMIN("TEACHER_ADMIN", "Admin Access (All Classes)"),
    CLASS_TEACHER("CLASS_TEACHER", "Teacher Access (Class-Wise)"),
    ENROLLED_PARENT("ENROLLED_PARENT", "Parent-Child Access"),
    PLAY_STORE_USER("PLAY_STORE_USER", "Play Store User");

    companion object {
        fun fromCode(code: String): AccessRoleTier =
            entries.find { it.code == code } ?: TEACHER_ADMIN
    }
}

@Entity(tableName = "access_control_config")
data class AccessControlConfig(
    @PrimaryKey val id: Int = 1,
    val roleTierCode: String = AccessRoleTier.TEACHER_ADMIN.code,
    val nurseryParentCode: String = "RPS-NUR-2026",
    val lkgParentCode: String = "RPS-LKG-2026",
    val ukgParentCode: String = "RPS-UKG-2026",
    val nurseryTeacherCode: String = "TCH-NUR-101",
    val lkgTeacherCode: String = "TCH-LKG-102",
    val ukgTeacherCode: String = "TCH-UKG-103",
    val teacherPin: String = "9090", // Admin Master PIN
    val unlockedClassesCsv: String = "NURSERY,LKG,UKG",
    val playStorePurchasedSkusCsv: String = "",
    val loggedInStudentId: Int = 0,
    val loggedInChildName: String = "",
    val loggedInChildRoll: String = "",
    val loggedInClassCode: String = ""
) {
    val roleTier: AccessRoleTier
        get() = AccessRoleTier.fromCode(roleTierCode)

    val loggedInClass: PreschoolClass?
        get() = if (loggedInClassCode.isBlank()) null else PreschoolClass.fromCode(loggedInClassCode)

    val isAdminLoggedIn: Boolean
        get() = roleTier == AccessRoleTier.TEACHER_ADMIN

    val isTeacherLoggedInForClass: Boolean
        get() = roleTier == AccessRoleTier.CLASS_TEACHER && loggedInClass != null

    val isParentLoggedInForChild: Boolean
        get() = roleTier == AccessRoleTier.ENROLLED_PARENT && loggedInClass != null

    val hasTeacherOrAdminPrivileges: Boolean
        get() = roleTier == AccessRoleTier.TEACHER_ADMIN || roleTier == AccessRoleTier.CLASS_TEACHER

    val unlockedClasses: Set<PreschoolClass>
        get() = when {
            isAdminLoggedIn -> PreschoolClass.entries.toSet()
            isTeacherLoggedInForClass || isParentLoggedInForChild -> setOfNotNull(loggedInClass)
            else -> unlockedClassesCsv
                .split(",")
                .mapNotNull { token ->
                    val clean = token.trim()
                    PreschoolClass.entries.find { it.code == clean }
                }
                .toSet()
        }

    fun isClassUnlocked(preschoolClass: PreschoolClass): Boolean {
        return when (roleTier) {
            AccessRoleTier.TEACHER_ADMIN -> true
            AccessRoleTier.CLASS_TEACHER -> loggedInClass == preschoolClass
            AccessRoleTier.ENROLLED_PARENT -> loggedInClass == preschoolClass
            AccessRoleTier.PLAY_STORE_USER -> unlockedClasses.contains(preschoolClass)
        }
    }

    fun codeForClass(preschoolClass: PreschoolClass): String = when (preschoolClass) {
        PreschoolClass.NURSERY -> nurseryParentCode
        PreschoolClass.LKG -> lkgParentCode
        PreschoolClass.UKG -> ukgParentCode
    }

    fun teacherCodeForClass(preschoolClass: PreschoolClass): String = when (preschoolClass) {
        PreschoolClass.NURSERY -> nurseryTeacherCode
        PreschoolClass.LKG -> lkgTeacherCode
        PreschoolClass.UKG -> ukgTeacherCode
    }
}

data class PlayStoreClassProduct(
    val productId: String,
    val title: String,
    val subtitle: String,
    val formattedPrice: String,
    val targetClass: PreschoolClass?, // null means All 3 Classes Bundle
    val badgeText: String
)

object SyllabusSeedData {

    val availableMonths = listOf(
        ReportMonthOption("2026-09", "September 2026"),
        ReportMonthOption("2026-08", "August 2026"),
        ReportMonthOption("2026-07", "July 2026")
    )

    val syllabusTopicsByClass: Map<PreschoolClass, List<String>> = mapOf(
        PreschoolClass.NURSERY to listOf(
            "Pre-Writing Strokes & Lines",
            "Letters A to F Tracing",
            "Numbers 1 to 5 Counting",
            "Colors & Basic Shapes",
            "Fruits & Animals"
        ),
        PreschoolClass.LKG to listOf(
            "Capital & Small Letters (A–Z)",
            "Phonics Beginning Sounds",
            "Numbers 1 to 20 & Counting",
            "Shapes & Comparisons",
            "EVS: Animals & Vehicles"
        ),
        PreschoolClass.UKG to listOf(
            "3-Letter CVC Words & Vowels",
            "Before, After & Between (1–50)",
            "Picture Addition & Subtraction",
            "Cursive & Word Tracing",
            "EVS: Seasons & Good Habits"
        )
    )

    fun initialStudents(): List<PreschoolStudent> = listOf(
        // Nursery Students
        PreschoolStudent(
            id = 1,
            classCode = PreschoolClass.NURSERY.code,
            rollNumber = "N-01",
            fullName = "Aarav Shinde",
            parentName = "Mr. Rahul Shinde",
            avatarColorHex = 0xFFFF6B6B
        ),
        PreschoolStudent(
            id = 2,
            classCode = PreschoolClass.NURSERY.code,
            rollNumber = "N-02",
            fullName = "Ananya Kale",
            parentName = "Mrs. Pooja Kale",
            avatarColorHex = 0xFFFF9F1C
        ),
        PreschoolStudent(
            id = 3,
            classCode = PreschoolClass.NURSERY.code,
            rollNumber = "N-03",
            fullName = "Vihaan Pawar",
            parentName = "Mr. Sandeep Pawar",
            avatarColorHex = 0xFF2EC4B6
        ),
        PreschoolStudent(
            id = 4,
            classCode = PreschoolClass.NURSERY.code,
            rollNumber = "N-04",
            fullName = "Myra Jadhav",
            parentName = "Mrs. Swati Jadhav",
            avatarColorHex = 0xFF8338EC
        ),

        // LKG Students
        PreschoolStudent(
            id = 5,
            classCode = PreschoolClass.LKG.code,
            rollNumber = "L-01",
            fullName = "Reyansh More",
            parentName = "Mr. Amit More",
            avatarColorHex = 0xFF00A896
        ),
        PreschoolStudent(
            id = 6,
            classCode = PreschoolClass.LKG.code,
            rollNumber = "L-02",
            fullName = "Saanvi Chavan",
            parentName = "Mrs. Neha Chavan",
            avatarColorHex = 0xFFEF476F
        ),
        PreschoolStudent(
            id = 7,
            classCode = PreschoolClass.LKG.code,
            rollNumber = "L-03",
            fullName = "Kabir Bhosale",
            parentName = "Mr. Vikram Bhosale",
            avatarColorHex = 0xFF118AB2
        ),
        PreschoolStudent(
            id = 8,
            classCode = PreschoolClass.LKG.code,
            rollNumber = "L-04",
            fullName = "Ira Gaikwad",
            parentName = "Mrs. Pallavi Gaikwad",
            avatarColorHex = 0xFFFFB703
        ),

        // UKG Students
        PreschoolStudent(
            id = 9,
            classCode = PreschoolClass.UKG.code,
            rollNumber = "U-01",
            fullName = "Advait Kulkarni",
            parentName = "Mr. Sachin Kulkarni",
            avatarColorHex = 0xFF6C5CE7
        ),
        PreschoolStudent(
            id = 10,
            classCode = PreschoolClass.UKG.code,
            rollNumber = "U-02",
            fullName = "Diya Deshmukh",
            parentName = "Mrs. Meera Deshmukh",
            avatarColorHex = 0xFF00B894
        ),
        PreschoolStudent(
            id = 11,
            classCode = PreschoolClass.UKG.code,
            rollNumber = "U-03",
            fullName = "Atharv Joshi",
            parentName = "Mr. Kiran Joshi",
            avatarColorHex = 0xFFE17055
        ),
        PreschoolStudent(
            id = 12,
            classCode = PreschoolClass.UKG.code,
            rollNumber = "U-04",
            fullName = "Kavya Patil",
            parentName = "Mrs. Rupali Patil",
            avatarColorHex = 0xFF0984E3
        )
    )

    fun initialQuestions(): List<SyllabusQuestion> = listOf(
        // ==================== NURSERY SYLLABUS QUESTIONS ====================
        SyllabusQuestion(
            id = 1,
            classCode = PreschoolClass.NURSERY.code,
            syllabusTopic = "Pre-Writing Strokes & Lines",
            questionKindCode = QuestionKind.TRACING_AND_MCQ.code,
            tracingTarget = "|",
            tracingHint = "Start at the top dot and pull straight down to trace a Standing Line ( | )",
            questionText = "Trace the Standing Line ( | ) and choose which pattern matches a Standing Line:",
            optionA = "|  Standing Line",
            optionB = "—  Sleeping Line",
            optionC = "O  Circle Shape",
            optionD = "~  Wave Line",
            correctOptionIndex = 0,
            starsReward = 3
        ),
        SyllabusQuestion(
            id = 2,
            classCode = PreschoolClass.NURSERY.code,
            syllabusTopic = "Pre-Writing Strokes & Lines",
            questionKindCode = QuestionKind.TRACING_AND_MCQ.code,
            tracingTarget = "—",
            tracingHint = "Move your finger from left to right across the dotted line",
            questionText = "Trace the Sleeping Line ( — ) and pick the correct line name:",
            optionA = "/  Slanting Line",
            optionB = "—  Sleeping Line",
            optionC = "|  Standing Line",
            optionD = "U  Upward Curve",
            correctOptionIndex = 1,
            starsReward = 3
        ),
        SyllabusQuestion(
            id = 3,
            classCode = PreschoolClass.NURSERY.code,
            syllabusTopic = "Letters A to F Tracing",
            questionKindCode = QuestionKind.TRACING_AND_MCQ.code,
            tracingTarget = "A",
            tracingHint = "Two slanting lines from the top and one sleeping line in the middle",
            questionText = "Trace Capital Letter 'A' and choose the fruit that starts with 'A':",
            optionA = "🍎 Apple",
            optionB = "🍌 Banana",
            optionC = "🥭 Mango",
            optionD = "🍇 Grapes",
            correctOptionIndex = 0,
            starsReward = 3
        ),
        SyllabusQuestion(
            id = 4,
            classCode = PreschoolClass.NURSERY.code,
            syllabusTopic = "Numbers 1 to 5 Counting",
            questionKindCode = QuestionKind.TRACING_AND_MCQ.code,
            tracingTarget = "3",
            tracingHint = "Trace two round curves to form Number 3",
            questionText = "Trace Number '3' and count the stars: ⭐ ⭐ ⭐ — How many stars are there?",
            optionA = "1 Star",
            optionB = "2 Stars",
            optionC = "3 Stars",
            optionD = "5 Stars",
            correctOptionIndex = 2,
            starsReward = 3
        ),
        SyllabusQuestion(
            id = 5,
            classCode = PreschoolClass.NURSERY.code,
            syllabusTopic = "Colors & Basic Shapes",
            questionKindCode = QuestionKind.PHOTO_QUIZ.code,
            tracingTarget = "O",
            tracingHint = "Round like a ball",
            questionText = "Look at the picture card! Which shape is round like the Sun with no corners?",
            optionA = "⭕ Circle",
            optionB = "🟦 Square",
            optionC = "🔺 Triangle",
            optionD = "⭐ Star",
            correctOptionIndex = 0,
            starsReward = 3,
            picturePresetCode = PictureQuizPreset.SUN_CIRCLE.code
        ),
        SyllabusQuestion(
            id = 16,
            classCode = PreschoolClass.NURSERY.code,
            syllabusTopic = "Fruits & Animals",
            questionKindCode = QuestionKind.PHOTO_QUIZ.code,
            tracingTarget = "A",
            tracingHint = "",
            questionText = "Photo Quiz: Identify this sweet red fruit that starts with Letter 'A':",
            optionA = "🍎 Apple",
            optionB = "🍌 Banana",
            optionC = "🍇 Grapes",
            optionD = "🍊 Orange",
            correctOptionIndex = 0,
            starsReward = 3,
            picturePresetCode = PictureQuizPreset.APPLE.code
        ),

        // ==================== LKG SYLLABUS QUESTIONS ====================
        SyllabusQuestion(
            id = 6,
            classCode = PreschoolClass.LKG.code,
            syllabusTopic = "Capital & Small Letters (A–Z)",
            questionKindCode = QuestionKind.TRACING_AND_MCQ.code,
            tracingTarget = "G",
            tracingHint = "Trace a big curve like C, then go up and across to make Capital G",
            questionText = "Trace Letter 'G' and select the correct small letter match for Capital 'G':",
            optionA = "g",
            optionB = "b",
            optionC = "d",
            optionD = "p",
            correctOptionIndex = 0,
            starsReward = 3
        ),
        SyllabusQuestion(
            id = 7,
            classCode = PreschoolClass.LKG.code,
            syllabusTopic = "Phonics Beginning Sounds",
            questionKindCode = QuestionKind.TRACING_AND_MCQ.code,
            tracingTarget = "H",
            tracingHint = "Two standing lines connected by a middle sleeping line",
            questionText = "Trace Letter 'H' and pick the picture word that begins with the /h/ sound:",
            optionA = "🐱 Cat",
            optionB = "🏠 House",
            optionC = "🐟 Fish",
            optionD = "🥁 Drum",
            correctOptionIndex = 1,
            starsReward = 3
        ),
        SyllabusQuestion(
            id = 8,
            classCode = PreschoolClass.LKG.code,
            syllabusTopic = "Numbers 1 to 20 & Counting",
            questionKindCode = QuestionKind.TRACING_AND_MCQ.code,
            tracingTarget = "8",
            tracingHint = "Loop around like an 'S' and close the loop back to the top",
            questionText = "Trace Number '8' and count the butterflies in the picture card:",
            optionA = "6 Butterflies",
            optionB = "7 Butterflies",
            optionC = "8 Butterflies",
            optionD = "10 Butterflies",
            correctOptionIndex = 2,
            starsReward = 3,
            picturePresetCode = PictureQuizPreset.BUTTERFLIES.code
        ),
        SyllabusQuestion(
            id = 9,
            classCode = PreschoolClass.LKG.code,
            syllabusTopic = "Shapes & Comparisons",
            questionKindCode = QuestionKind.PHOTO_QUIZ.code,
            tracingTarget = "E",
            tracingHint = "",
            questionText = "Photo Quiz: Look at the picture! Which large animal has a long trunk and big ears?",
            optionA = "🐘 Elephant",
            optionB = "🐁 Mouse",
            optionC = "🐜 Ant",
            optionD = "🐝 Honeybee",
            correctOptionIndex = 0,
            starsReward = 3,
            picturePresetCode = PictureQuizPreset.ELEPHANT.code
        ),
        SyllabusQuestion(
            id = 10,
            classCode = PreschoolClass.LKG.code,
            syllabusTopic = "EVS: Animals & Vehicles",
            questionKindCode = QuestionKind.PHOTO_QUIZ.code,
            tracingTarget = "C",
            tracingHint = "",
            questionText = "Photo Quiz: Look at the picture card! Which Domestic Farm Animal gives us fresh milk?",
            optionA = "🦁 Lion",
            optionB = "🐄 Cow",
            optionC = "🐊 Crocodile",
            optionD = "🦊 Fox",
            correctOptionIndex = 1,
            starsReward = 3,
            picturePresetCode = PictureQuizPreset.COW.code
        ),

        // ==================== UKG SYLLABUS QUESTIONS ====================
        SyllabusQuestion(
            id = 11,
            classCode = PreschoolClass.UKG.code,
            syllabusTopic = "3-Letter CVC Words & Vowels",
            questionKindCode = QuestionKind.TRACING_AND_MCQ.code,
            tracingTarget = "CAT",
            tracingHint = "Trace all 3 letters C - A - T neatly from left to right",
            questionText = "Trace the CVC word 'CAT' and choose the missing middle vowel in C _ T:",
            optionA = "A",
            optionB = "E",
            optionC = "I",
            optionD = "U",
            correctOptionIndex = 0,
            starsReward = 3
        ),
        SyllabusQuestion(
            id = 12,
            classCode = PreschoolClass.UKG.code,
            syllabusTopic = "3-Letter CVC Words & Vowels",
            questionKindCode = QuestionKind.TRACING_AND_MCQ.code,
            tracingTarget = "PEN",
            tracingHint = "Trace P - E - N smoothly to complete the '-en' family word",
            questionText = "Trace 'PEN' and choose the word that rhymes with PEN:",
            optionA = "SUN",
            optionB = "HEN",
            optionC = "DOG",
            optionD = "CAR",
            correctOptionIndex = 1,
            starsReward = 3
        ),
        SyllabusQuestion(
            id = 13,
            classCode = PreschoolClass.UKG.code,
            syllabusTopic = "Before, After & Between (1–50)",
            questionKindCode = QuestionKind.TRACING_AND_MCQ.code,
            tracingTarget = "25",
            tracingHint = "Trace Number 25 (Two Tens and Five Ones)",
            questionText = "Trace '25' and find which number comes BETWEEN 24 and 26:",
            optionA = "23",
            optionB = "25",
            optionC = "27",
            optionD = "20",
            correctOptionIndex = 1,
            starsReward = 3
        ),
        SyllabusQuestion(
            id = 14,
            classCode = PreschoolClass.UKG.code,
            syllabusTopic = "Picture Addition & Subtraction",
            questionKindCode = QuestionKind.PHOTO_QUIZ.code,
            tracingTarget = "7",
            tracingHint = "",
            questionText = "Photo Addition Quiz: Count the apples in the picture (4 Apples + 3 Apples) =",
            optionA = "5 Apples",
            optionB = "6 Apples",
            optionC = "7 Apples",
            optionD = "8 Apples",
            correctOptionIndex = 2,
            starsReward = 3,
            picturePresetCode = PictureQuizPreset.APPLES_ADD.code
        ),
        SyllabusQuestion(
            id = 15,
            classCode = PreschoolClass.UKG.code,
            syllabusTopic = "EVS: Seasons & Good Habits",
            questionKindCode = QuestionKind.PHOTO_QUIZ.code,
            tracingTarget = "R",
            tracingHint = "",
            questionText = "Photo Quiz: Look at the picture! In which season do we use an umbrella and raincoat?",
            optionA = "Rainy Season (Monsoon)",
            optionB = "Summer Season",
            optionC = "Winter Season",
            optionD = "Spring Season",
            correctOptionIndex = 0,
            starsReward = 3,
            picturePresetCode = PictureQuizPreset.RAINY_UMBRELLA.code
        )
    )

    fun initialActivityLogs(): List<StudentActivityLog> {
        val now = System.currentTimeMillis()
        return listOf(
            // Nursery September 2026 logs
            StudentActivityLog(
                studentId = 1,
                classCode = PreschoolClass.NURSERY.code,
                questionId = 1,
                questionTitle = "Standing Line ( | ) Tracing",
                syllabusTopic = "Pre-Writing Strokes & Lines",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 3
            ),
            StudentActivityLog(
                studentId = 1,
                classCode = PreschoolClass.NURSERY.code,
                questionId = 2,
                questionTitle = "Sleeping Line ( — ) Tracing",
                syllabusTopic = "Pre-Writing Strokes & Lines",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 2
            ),
            StudentActivityLog(
                studentId = 1,
                classCode = PreschoolClass.NURSERY.code,
                questionId = 3,
                questionTitle = "Capital Letter 'A' Tracing",
                syllabusTopic = "Letters A to F Tracing",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 1
            ),
            StudentActivityLog(
                studentId = 2,
                classCode = PreschoolClass.NURSERY.code,
                questionId = 1,
                questionTitle = "Standing Line ( | ) Tracing",
                syllabusTopic = "Pre-Writing Strokes & Lines",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 2
            ),
            StudentActivityLog(
                studentId = 2,
                classCode = PreschoolClass.NURSERY.code,
                questionId = 4,
                questionTitle = "Number '3' Tracing & Counting",
                syllabusTopic = "Numbers 1 to 5 Counting",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 1
            ),
            StudentActivityLog(
                studentId = 3,
                classCode = PreschoolClass.NURSERY.code,
                questionId = 5,
                questionTitle = "Circle Shape Recognition",
                syllabusTopic = "Colors & Basic Shapes",
                isTracingQuestion = false,
                isCorrect = true,
                starsEarned = 2,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 1
            ),

            // LKG September 2026 logs
            StudentActivityLog(
                studentId = 5,
                classCode = PreschoolClass.LKG.code,
                questionId = 6,
                questionTitle = "Letter 'G' Tracing & Small Letter Match",
                syllabusTopic = "Capital & Small Letters (A–Z)",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 3
            ),
            StudentActivityLog(
                studentId = 5,
                classCode = PreschoolClass.LKG.code,
                questionId = 7,
                questionTitle = "Letter 'H' Phonics Sound /h/",
                syllabusTopic = "Phonics Beginning Sounds",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 2
            ),
            StudentActivityLog(
                studentId = 5,
                classCode = PreschoolClass.LKG.code,
                questionId = 8,
                questionTitle = "Number '8' Tracing & Counting",
                syllabusTopic = "Numbers 1 to 20 & Counting",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 1
            ),
            StudentActivityLog(
                studentId = 6,
                classCode = PreschoolClass.LKG.code,
                questionId = 6,
                questionTitle = "Letter 'G' Tracing & Small Letter Match",
                syllabusTopic = "Capital & Small Letters (A–Z)",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 2
            ),
            StudentActivityLog(
                studentId = 6,
                classCode = PreschoolClass.LKG.code,
                questionId = 10,
                questionTitle = "Domestic Farm Animals Quiz",
                syllabusTopic = "EVS: Animals & Vehicles",
                isTracingQuestion = false,
                isCorrect = true,
                starsEarned = 2,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 1
            ),
            StudentActivityLog(
                studentId = 7,
                classCode = PreschoolClass.LKG.code,
                questionId = 9,
                questionTitle = "Bigger vs Smaller Comparison",
                syllabusTopic = "Shapes & Comparisons",
                isTracingQuestion = false,
                isCorrect = true,
                starsEarned = 2,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 1
            ),

            // UKG September 2026 logs
            StudentActivityLog(
                studentId = 9,
                classCode = PreschoolClass.UKG.code,
                questionId = 11,
                questionTitle = "CVC Word 'CAT' Tracing & Vowel",
                syllabusTopic = "3-Letter CVC Words & Vowels",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 4
            ),
            StudentActivityLog(
                studentId = 9,
                classCode = PreschoolClass.UKG.code,
                questionId = 12,
                questionTitle = "CVC Word 'PEN' Tracing & Rhyming",
                syllabusTopic = "3-Letter CVC Words & Vowels",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 3
            ),
            StudentActivityLog(
                studentId = 9,
                classCode = PreschoolClass.UKG.code,
                questionId = 13,
                questionTitle = "Number '25' Between Numbers",
                syllabusTopic = "Before, After & Between (1–50)",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 2
            ),
            StudentActivityLog(
                studentId = 9,
                classCode = PreschoolClass.UKG.code,
                questionId = 14,
                questionTitle = "Picture Addition (4 + 3)",
                syllabusTopic = "Picture Addition & Subtraction",
                isTracingQuestion = false,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 1
            ),
            StudentActivityLog(
                studentId = 10,
                classCode = PreschoolClass.UKG.code,
                questionId = 11,
                questionTitle = "CVC Word 'CAT' Tracing & Vowel",
                syllabusTopic = "3-Letter CVC Words & Vowels",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 2
            ),
            StudentActivityLog(
                studentId = 10,
                classCode = PreschoolClass.UKG.code,
                questionId = 15,
                questionTitle = "Rainy Season & Good Habits",
                syllabusTopic = "EVS: Seasons & Good Habits",
                isTracingQuestion = false,
                isCorrect = true,
                starsEarned = 2,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 1
            ),
            StudentActivityLog(
                studentId = 11,
                classCode = PreschoolClass.UKG.code,
                questionId = 13,
                questionTitle = "Number '25' Between Numbers",
                syllabusTopic = "Before, After & Between (1–50)",
                isTracingQuestion = true,
                isCorrect = true,
                starsEarned = 3,
                monthKey = "2026-09",
                monthLabel = "September 2026",
                completedAt = now - 86_400_000L * 1
            )
        )
    }
}
