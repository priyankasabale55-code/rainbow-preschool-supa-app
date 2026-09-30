package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PreschoolDao {

    // ==================== BOARD ENTRIES ====================
    @Query("SELECT * FROM preschool_entries ORDER BY isPinned DESC, isCompleted ASC, createdAt DESC")
    fun getAllEntries(): Flow<List<PreschoolEntry>>

    @Query("SELECT * FROM preschool_entries WHERE classCode = :classCode ORDER BY isPinned DESC, isCompleted ASC, createdAt DESC")
    fun getEntriesForClass(classCode: String): Flow<List<PreschoolEntry>>

    @Query("SELECT COUNT(*) FROM preschool_entries")
    suspend fun getEntryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: PreschoolEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<PreschoolEntry>)

    @Update
    suspend fun updateEntry(entry: PreschoolEntry)

    @Query("DELETE FROM preschool_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Int)

    // ==================== SYLLABUS QUESTIONS & TRACING ====================
    @Query("SELECT * FROM syllabus_questions ORDER BY createdAt ASC")
    fun getAllSyllabusQuestions(): Flow<List<SyllabusQuestion>>

    @Query("SELECT COUNT(*) FROM syllabus_questions")
    suspend fun getQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: SyllabusQuestion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuestions(questions: List<SyllabusQuestion>)

    @Query("DELETE FROM syllabus_questions WHERE id = :id")
    suspend fun deleteQuestionById(id: Int)

    // ==================== STUDENTS ====================
    @Query("SELECT * FROM preschool_students ORDER BY rollNumber ASC")
    fun getAllStudents(): Flow<List<PreschoolStudent>>

    @Query("SELECT COUNT(*) FROM preschool_students")
    suspend fun getStudentCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: PreschoolStudent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudents(students: List<PreschoolStudent>)

    // ==================== STUDENT MONTHLY ACTIVITY LOGS ====================
    @Query("SELECT * FROM student_activity_logs ORDER BY completedAt DESC")
    fun getAllActivityLogs(): Flow<List<StudentActivityLog>>

    @Query("SELECT COUNT(*) FROM student_activity_logs")
    suspend fun getActivityLogCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: StudentActivityLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllActivityLogs(logs: List<StudentActivityLog>)

    // ==================== ACCESS CONTROL & PLAY STORE ENTITLEMENT ====================
    @Query("SELECT * FROM access_control_config WHERE id = 1")
    fun getAccessConfig(): Flow<AccessControlConfig?>

    @Query("SELECT * FROM access_control_config WHERE id = 1")
    suspend fun getAccessConfigOnce(): AccessControlConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAccessConfig(config: AccessControlConfig)

    // ==================== SCHOOL ANNOUNCEMENTS (EVENTS, EXAMS, HOLIDAYS) ====================
    @Query("SELECT * FROM school_announcements ORDER BY isPinned DESC, createdAt DESC")
    fun getAllAnnouncements(): Flow<List<SchoolAnnouncement>>

    @Query("SELECT COUNT(*) FROM school_announcements")
    suspend fun getAnnouncementCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: SchoolAnnouncement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnnouncements(announcements: List<SchoolAnnouncement>)

    @Query("DELETE FROM school_announcements WHERE id = :id")
    suspend fun deleteAnnouncementById(id: Int)

    // ==================== DAILY STUDENT ATTENDANCE ====================
    @Query("SELECT * FROM student_attendance_records ORDER BY recordedAt DESC")
    fun getAllAttendanceRecords(): Flow<List<StudentAttendanceRecord>>

    @Query("SELECT COUNT(*) FROM student_attendance_records")
    suspend fun getAttendanceRecordCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: StudentAttendanceRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAttendanceRecords(records: List<StudentAttendanceRecord>)

    @Query("SELECT * FROM student_attendance_records WHERE studentId = :studentId AND attendanceDate = :attendanceDate LIMIT 1")
    suspend fun findAttendanceRecord(studentId: Int, attendanceDate: String): StudentAttendanceRecord?

    // ==================== SUBJECT-WISE STUDY RESOURCES & LINKS ====================
    @Query("SELECT * FROM study_resources ORDER BY createdAt DESC")
    fun getAllStudyResources(): Flow<List<StudyResourceItem>>

    @Query("SELECT COUNT(*) FROM study_resources")
    suspend fun getStudyResourceCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyResource(resource: StudyResourceItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudyResources(resources: List<StudyResourceItem>)

    @Query("DELETE FROM study_resources WHERE id = :id")
    suspend fun deleteStudyResourceById(id: Int)
}
