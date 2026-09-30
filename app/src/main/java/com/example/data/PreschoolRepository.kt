package com.example.data

import kotlinx.coroutines.flow.Flow

class PreschoolRepository(private val dao: PreschoolDao) {

    val allEntries: Flow<List<PreschoolEntry>> = dao.getAllEntries()
    val allSyllabusQuestions: Flow<List<SyllabusQuestion>> = dao.getAllSyllabusQuestions()
    val allStudents: Flow<List<PreschoolStudent>> = dao.getAllStudents()
    val allActivityLogs: Flow<List<StudentActivityLog>> = dao.getAllActivityLogs()
    val accessConfig: Flow<AccessControlConfig?> = dao.getAccessConfig()
    val allAnnouncements: Flow<List<SchoolAnnouncement>> = dao.getAllAnnouncements()
    val allAttendanceRecords: Flow<List<StudentAttendanceRecord>> = dao.getAllAttendanceRecords()
    val allStudyResources: Flow<List<StudyResourceItem>> = dao.getAllStudyResources()

    fun getEntriesForClass(classCode: String): Flow<List<PreschoolEntry>> =
        dao.getEntriesForClass(classCode)

    suspend fun ensureSeeded() {
        if (dao.getEntryCount() == 0) {
            dao.insertAll(PreschoolSeedData.initialEntries())
        }
        if (dao.getQuestionCount() == 0) {
            dao.insertAllQuestions(SyllabusSeedData.initialQuestions())
        }
        if (dao.getStudentCount() == 0) {
            dao.insertAllStudents(SyllabusSeedData.initialStudents())
        }
        if (dao.getActivityLogCount() == 0) {
            dao.insertAllActivityLogs(SyllabusSeedData.initialActivityLogs())
        }
        if (dao.getAccessConfigOnce() == null) {
            dao.saveAccessConfig(AccessControlConfig())
        }
        if (dao.getAnnouncementCount() == 0) {
            dao.insertAllAnnouncements(SchoolPortalSeedData.initialAnnouncements())
        }
        if (dao.getAttendanceRecordCount() == 0) {
            dao.insertAllAttendanceRecords(SchoolPortalSeedData.initialAttendanceRecords())
        }
        if (dao.getStudyResourceCount() == 0) {
            dao.insertAllStudyResources(SchoolPortalSeedData.initialStudyResources())
        }
    }

    suspend fun updateAccessConfig(config: AccessControlConfig) {
        dao.saveAccessConfig(config)
    }

    suspend fun saveEntry(entry: PreschoolEntry) {
        dao.insertEntry(entry)
    }

    suspend fun toggleCompleted(entry: PreschoolEntry) {
        dao.updateEntry(entry.copy(isCompleted = !entry.isCompleted))
    }

    suspend fun togglePinned(entry: PreschoolEntry) {
        dao.updateEntry(entry.copy(isPinned = !entry.isPinned))
    }

    suspend fun deleteEntry(id: Int) {
        dao.deleteEntryById(id)
    }

    suspend fun saveSyllabusQuestion(question: SyllabusQuestion) {
        dao.insertQuestion(question)
    }

    suspend fun deleteSyllabusQuestion(id: Int) {
        dao.deleteQuestionById(id)
    }

    suspend fun saveStudent(student: PreschoolStudent) {
        dao.insertStudent(student)
    }

    suspend fun recordStudentActivity(log: StudentActivityLog) {
        dao.insertActivityLog(log)
    }

    suspend fun saveAnnouncement(announcement: SchoolAnnouncement) {
        dao.insertAnnouncement(announcement)
    }

    suspend fun deleteAnnouncement(id: Int) {
        dao.deleteAnnouncementById(id)
    }

    suspend fun logStudentAttendance(
        student: PreschoolStudent,
        attendanceDate: String,
        status: AttendanceStatus,
        note: String
    ) {
        val existing = dao.findAttendanceRecord(student.id, attendanceDate)
        val record = StudentAttendanceRecord(
            id = existing?.id ?: 0,
            studentId = student.id,
            studentName = student.fullName,
            rollNumber = student.rollNumber,
            classCode = student.classCode,
            attendanceDate = attendanceDate,
            statusCode = status.code,
            note = note
        )
        dao.insertAttendanceRecord(record)
    }

    suspend fun saveStudyResource(resource: StudyResourceItem) {
        dao.insertStudyResource(resource)
    }

    suspend fun deleteStudyResource(id: Int) {
        dao.deleteStudyResourceById(id)
    }
}
