package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AnnouncementCategory(
    val code: String,
    val label: String,
    val badgeEmoji: String
) {
    EVENTS("EVENTS", "Events", "🎉"),
    EXAMS("EXAMS", "Exams", "📝"),
    HOLIDAYS("HOLIDAYS", "Holidays", "🏖️");

    companion object {
        fun fromCode(code: String): AnnouncementCategory =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: EVENTS
    }
}

@Entity(tableName = "school_announcements")
data class SchoolAnnouncement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val categoryCode: String,
    val targetClassCode: String = "ALL",
    val scheduledDate: String,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val category: AnnouncementCategory
        get() = AnnouncementCategory.fromCode(categoryCode)
}

enum class AttendanceStatus(
    val code: String,
    val label: String
) {
    PRESENT("PRESENT", "Present"),
    ABSENT("ABSENT", "Absent"),
    LATE("LATE", "Late");

    companion object {
        fun fromCode(code: String): AttendanceStatus =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: PRESENT
    }
}

@Entity(tableName = "student_attendance_records")
data class StudentAttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentId: Int,
    val studentName: String,
    val rollNumber: String,
    val classCode: String,
    val attendanceDate: String,
    val statusCode: String,
    val note: String = "",
    val recordedAt: Long = System.currentTimeMillis()
) {
    val status: AttendanceStatus
        get() = AttendanceStatus.fromCode(statusCode)
}

@Entity(tableName = "study_resources")
data class StudyResourceItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val classCode: String,
    val subjectName: String,
    val title: String,
    val description: String,
    val resourceLink: String,
    val resourceType: String = "Worksheet / Link",
    val createdAt: Long = System.currentTimeMillis()
)

data class TimetableSlot(
    val dayOfWeek: String,
    val timeSlot: String,
    val subject: String,
    val activityTitle: String,
    val roomLabel: String
)

data class SubjectGradeReportItem(
    val subject: String,
    val assessmentTitle: String,
    val gradeLetter: String,
    val scorePercent: Int,
    val remarks: String
)

object SchoolPortalSeedData {

    val availableDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    val availableAttendanceDates = listOf(
        "30 Sep 2026 (Today)",
        "29 Sep 2026",
        "28 Sep 2026",
        "27 Sep 2026"
    )

    fun initialAnnouncements(): List<SchoolAnnouncement> {
        val now = System.currentTimeMillis()
        return listOf(
            SchoolAnnouncement(
                id = 1,
                title = "Term-1 Oral & Tracing Assessment Schedule",
                description = "Term-1 friendly oral phonics, picture recognition, and slate tracing assessments for Nursery, LKG, and UKG will be conducted from 9:30 AM to 11:30 AM.",
                categoryCode = AnnouncementCategory.EXAMS.code,
                targetClassCode = "ALL",
                scheduledDate = "12 Oct 2026 – 16 Oct 2026",
                isPinned = true,
                createdAt = now - 3_600_000L * 2
            ),
            SchoolAnnouncement(
                id = 2,
                title = "Grand Rainbow Annual Colour & Fancy Dress Day",
                description = "Students are invited to dress up as their favourite fruit, animal, or community helper. Parents are cordially invited to the Main Activity Hall at 10:00 AM.",
                categoryCode = AnnouncementCategory.EVENTS.code,
                targetClassCode = "ALL",
                scheduledDate = "08 Oct 2026",
                isPinned = true,
                createdAt = now - 3_600_000L * 8
            ),
            SchoolAnnouncement(
                id = 3,
                title = "Gandhi Jayanti National Holiday",
                description = "Rainbow Pre-School Supa will remain closed on 02 October 2026 on account of Gandhi Jayanti. Regular classes resume the next working day.",
                categoryCode = AnnouncementCategory.HOLIDAYS.code,
                targetClassCode = "ALL",
                scheduledDate = "02 Oct 2026",
                isPinned = false,
                createdAt = now - 3_600_000L * 16
            ),
            SchoolAnnouncement(
                id = 4,
                title = "LKG & UKG Picture Math & CVC Words Readiness Test",
                description = "Interactive classroom picture counting, number recognition (1–50), and 3-letter CVC word matching review.",
                categoryCode = AnnouncementCategory.EXAMS.code,
                targetClassCode = PreschoolClass.UKG.code,
                scheduledDate = "19 Oct 2026",
                isPinned = false,
                createdAt = now - 3_600_000L * 24
            ),
            SchoolAnnouncement(
                id = 5,
                title = "Diwali Vacation Holiday Notice",
                description = "School will observe Diwali festival holidays. Wishing all Rainbow Pre-School Supa families a joyful and safe celebration!",
                categoryCode = AnnouncementCategory.HOLIDAYS.code,
                targetClassCode = "ALL",
                scheduledDate = "28 Oct 2026 – 04 Nov 2026",
                isPinned = false,
                createdAt = now - 3_600_000L * 36
            ),
            SchoolAnnouncement(
                id = 6,
                title = "Parent-School Open House & Monthly Portfolio Showcase",
                description = "Review your child's finger-tracing worksheets, art craft folders, and monthly star badges.",
                categoryCode = AnnouncementCategory.EVENTS.code,
                targetClassCode = "ALL",
                scheduledDate = "24 Oct 2026",
                isPinned = false,
                createdAt = now - 3_600_000L * 48
            )
        )
    }

    fun initialAttendanceRecords(): List<StudentAttendanceRecord> {
        val students = SyllabusSeedData.initialStudents()
        val records = mutableListOf<StudentAttendanceRecord>()
        var idCounter = 1
        val dates = listOf("30 Sep 2026 (Today)", "29 Sep 2026", "28 Sep 2026")
        for (date in dates) {
            for ((idx, student) in students.withIndex()) {
                val status = when {
                    date == "29 Sep 2026" && idx % 5 == 3 -> AttendanceStatus.ABSENT
                    date == "30 Sep 2026 (Today)" && idx % 6 == 4 -> AttendanceStatus.LATE
                    else -> AttendanceStatus.PRESENT
                }
                val note = when (status) {
                    AttendanceStatus.PRESENT -> "On time • Morning circle attended"
                    AttendanceStatus.LATE -> "Arrived 15 mins late"
                    AttendanceStatus.ABSENT -> "Parent informed leave"
                }
                records.add(
                    StudentAttendanceRecord(
                        id = idCounter++,
                        studentId = student.id,
                        studentName = student.fullName,
                        rollNumber = student.rollNumber,
                        classCode = student.classCode,
                        attendanceDate = date,
                        statusCode = status.code,
                        note = note
                    )
                )
            }
        }
        return records
    }

    fun initialStudyResources(): List<StudyResourceItem> = listOf(
        StudyResourceItem(
            id = 1,
            classCode = PreschoolClass.NURSERY.code,
            subjectName = "Pre-Writing & Alphabet",
            title = "Standing, Sleeping & Curve Lines Practice Pack",
            description = "Printable dotted stroke tracing sheets and phonics rhyme audio guide for Letters A to F.",
            resourceLink = "https://rainbowpreschoolsupa.edu.in/resources/nursery-strokes-a-f",
            resourceType = "PDF Worksheet"
        ),
        StudyResourceItem(
            id = 2,
            classCode = PreschoolClass.NURSERY.code,
            subjectName = "Numbers & Shapes",
            title = "Numbers 1 to 10 Finger Counting & Shape Flashcards",
            description = "Interactive visual flashcards for Circle, Square, Triangle, and counting objects 1–10.",
            resourceLink = "https://rainbowpreschoolsupa.edu.in/resources/nursery-numbers-shapes",
            resourceType = "Interactive Flashcards"
        ),
        StudyResourceItem(
            id = 3,
            classCode = PreschoolClass.LKG.code,
            subjectName = "Phonics & Literacy",
            title = "Capital & Small Letters (Aa–Zz) Phonics Sound Guide",
            description = "Audio-visual phonics pronunciation guide and 4-line workbook practice links.",
            resourceLink = "https://rainbowpreschoolsupa.edu.in/resources/lkg-phonics-az",
            resourceType = "Audio + Worksheet"
        ),
        StudyResourceItem(
            id = 4,
            classCode = PreschoolClass.LKG.code,
            subjectName = "EVS & General Awareness",
            title = "Domestic & Wild Animals, Fruits & Vehicles Picture Book",
            description = "Illustrated picture chart for LKG Environmental Studies oral revision.",
            resourceLink = "https://rainbowpreschoolsupa.edu.in/resources/lkg-evs-picturebook",
            resourceType = "Illustrated eBook"
        ),
        StudyResourceItem(
            id = 5,
            classCode = PreschoolClass.UKG.code,
            subjectName = "English CVC Words",
            title = "3-Letter CVC Word Families (a, e, i, o, u) Workbook",
            description = "Sight words, rhyming families (-at, -en, -ig, -op, -un), and simple sentence reading sheets.",
            resourceLink = "https://rainbowpreschoolsupa.edu.in/resources/ukg-cvc-workbook",
            resourceType = "PDF Workbook"
        ),
        StudyResourceItem(
            id = 6,
            classCode = PreschoolClass.UKG.code,
            subjectName = "Early Mathematics",
            title = "Numbers 1–50, Before/After/Between & Picture Addition",
            description = "Practice sheets for tens-and-ones counting and single-digit picture addition.",
            resourceLink = "https://rainbowpreschoolsupa.edu.in/resources/ukg-math-addition",
            resourceType = "Practice Sheet"
        )
    )

    fun getTimetableForClass(preschoolClass: PreschoolClass, dayOfWeek: String): List<TimetableSlot> {
        return when (preschoolClass) {
            PreschoolClass.NURSERY -> listOf(
                TimetableSlot(dayOfWeek, "09:00 AM – 09:30 AM", "Morning Circle", "Prayer, Weather Song & Attendance", preschoolClass.roomName),
                TimetableSlot(dayOfWeek, "09:30 AM – 10:15 AM", "Pre-Writing & Literacy", "Standing/Sleeping Lines & Letter Tracing ($dayOfWeek Focus)", preschoolClass.roomName),
                TimetableSlot(dayOfWeek, "10:15 AM – 10:45 AM", "Nutrition Break", "Handwashing Habit & Healthy Fruit Tiffin", "Dining Area"),
                TimetableSlot(dayOfWeek, "10:45 AM – 11:30 AM", "Numbers & Shapes", "Object Counting (1–10) & Shape Sorting Blocks", preschoolClass.roomName),
                TimetableSlot(dayOfWeek, "11:30 AM – 12:00 PM", "Art, Rhymes & Play", "Action Rhymes, Finger Painting & Story Time", "Play Zone")
            )
            PreschoolClass.LKG -> listOf(
                TimetableSlot(dayOfWeek, "09:00 AM – 09:30 AM", "Assembly & Phonics", "Phonics Sound Drill & Calendar Time", preschoolClass.roomName),
                TimetableSlot(dayOfWeek, "09:30 AM – 10:20 AM", "English Literacy", "Capital & Small Letters (Aa–Zz) 4-Line Writing", preschoolClass.roomName),
                TimetableSlot(dayOfWeek, "10:20 AM – 10:50 AM", "Snack & Social Time", "Healthy Tiffin & Polite Magic Words Practice", "Dining Area"),
                TimetableSlot(dayOfWeek, "10:50 AM – 11:35 AM", "Mathematics", "Numbers 1–20 Counting, Shapes & Comparisons", preschoolClass.roomName),
                TimetableSlot(dayOfWeek, "11:35 AM – 12:15 PM", "EVS & Creative Craft", "Animals, Fruits, Seasons & Origami Craft", preschoolClass.roomName)
            )
            PreschoolClass.UKG -> listOf(
                TimetableSlot(dayOfWeek, "09:00 AM – 09:30 AM", "Morning Assembly", "Show-and-Tell, Sight Words & Mental Math", preschoolClass.roomName),
                TimetableSlot(dayOfWeek, "09:30 AM – 10:25 AM", "English & CVC Words", "3-Letter Vowel Words Reading & Cursive Readiness", preschoolClass.roomName),
                TimetableSlot(dayOfWeek, "10:25 AM – 10:55 AM", "Nutrition Break", "Independent Dining & Cleanliness Routine", "Dining Area"),
                TimetableSlot(dayOfWeek, "10:55 AM – 11:45 AM", "Mathematics (1–50)", "Before/After/Between Numbers & Picture Addition", preschoolClass.roomName),
                TimetableSlot(dayOfWeek, "11:45 AM – 12:30 PM", "EVS & School Readiness", "Good Habits, Seasons, Quiz & Outdoor Sports", preschoolClass.roomName)
            )
        }
    }

    fun getGradeReportsForStudent(
        student: PreschoolStudent,
        solvedCount: Int,
        accuracyPercent: Int
    ): List<SubjectGradeReportItem> {
        val baseBoost = (solvedCount * 2).coerceAtMost(8)
        val effectiveScore = if (accuracyPercent > 0) accuracyPercent else (88 + baseBoost).coerceAtMost(98)
        val gradeForScore: (Int) -> String = { score ->
            when {
                score >= 90 -> "A+"
                score >= 80 -> "A"
                score >= 70 -> "B+"
                else -> "B"
            }
        }
        return when (student.preschoolClass) {
            PreschoolClass.NURSERY -> listOf(
                SubjectGradeReportItem(
                    subject = "Pre-Writing & Alphabet (A–F)",
                    assessmentTitle = "Stroke Control & Letter Recognition",
                    gradeLetter = gradeForScore(effectiveScore),
                    scorePercent = effectiveScore,
                    remarks = "Traces standing and sleeping lines neatly with good grip."
                ),
                SubjectGradeReportItem(
                    subject = "Numbers & Shapes (1–10)",
                    assessmentTitle = "Oral Counting & Shape Matching",
                    gradeLetter = gradeForScore((effectiveScore + 2).coerceAtMost(99)),
                    scorePercent = (effectiveScore + 2).coerceAtMost(99),
                    remarks = "Identifies Circle, Square, and Triangle accurately."
                ),
                SubjectGradeReportItem(
                    subject = "Rhymes, Colours & Motor Skills",
                    assessmentTitle = "Oral Recitation & Colour Sorting",
                    gradeLetter = "A+",
                    scorePercent = 95,
                    remarks = "Enthusiastic participation in classroom activities."
                )
            )
            PreschoolClass.LKG -> listOf(
                SubjectGradeReportItem(
                    subject = "English Phonics & Writing (Aa–Zz)",
                    assessmentTitle = "Beginning Sounds & 4-Line Writing",
                    gradeLetter = gradeForScore(effectiveScore),
                    scorePercent = effectiveScore,
                    remarks = "Matches capital and small letters with clear phonics sounds."
                ),
                SubjectGradeReportItem(
                    subject = "Mathematics (1–20 & Comparisons)",
                    assessmentTitle = "Object Counting & Big/Small",
                    gradeLetter = gradeForScore((effectiveScore + 1).coerceAtMost(98)),
                    scorePercent = (effectiveScore + 1).coerceAtMost(98),
                    remarks = "Counts objects accurately and writes numbers neatly."
                ),
                SubjectGradeReportItem(
                    subject = "EVS (Animals, Fruits & Vehicles)",
                    assessmentTitle = "Picture Quiz & Oral Identification",
                    gradeLetter = "A+",
                    scorePercent = 94,
                    remarks = "Quickly identifies domestic/wild animals and healthy fruits."
                )
            )
            PreschoolClass.UKG -> listOf(
                SubjectGradeReportItem(
                    subject = "English CVC Words & Vowels",
                    assessmentTitle = "3-Letter Word Reading & Dictation",
                    gradeLetter = gradeForScore(effectiveScore),
                    scorePercent = effectiveScore,
                    remarks = "Reads and writes vowel family CVC words confidently."
                ),
                SubjectGradeReportItem(
                    subject = "Mathematics (1–50 & Picture Addition)",
                    assessmentTitle = "Before/After/Between & Addition",
                    gradeLetter = gradeForScore((effectiveScore + 2).coerceAtMost(99)),
                    scorePercent = (effectiveScore + 2).coerceAtMost(99),
                    remarks = "Solves picture addition and number sequences accurately."
                ),
                SubjectGradeReportItem(
                    subject = "EVS & Primary School Readiness",
                    assessmentTitle = "Seasons, Good Habits & General Quiz",
                    gradeLetter = "A+",
                    scorePercent = 96,
                    remarks = "Demonstrates strong curiosity and classroom leadership."
                )
            )
        }
    }
}
