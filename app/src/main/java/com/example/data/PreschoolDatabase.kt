package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PreschoolEntry::class,
        SyllabusQuestion::class,
        PreschoolStudent::class,
        StudentActivityLog::class,
        AccessControlConfig::class,
        SchoolAnnouncement::class,
        StudentAttendanceRecord::class,
        StudyResourceItem::class
    ],
    version = 8,
    exportSchema = false
)
abstract class PreschoolDatabase : RoomDatabase() {

    abstract fun preschoolDao(): PreschoolDao

    companion object {
        @Volatile
        private var INSTANCE: PreschoolDatabase? = null

        fun getDatabase(context: Context): PreschoolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PreschoolDatabase::class.java,
                    "rainbow_preschool_supa.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
