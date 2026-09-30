package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
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

class PreschoolViewModel : ViewModel() {

    // ... (keep your existing properties, constructor, and other functions above)

    fun getUnlockedCsv(tier: AccessRoleTier, targetClass: PreschoolClass): String {
        val unlockedCsv = when (tier) {
            AccessRoleTier.TEACHER_ADMIN -> "NURSERY,LKG,UKG"
            AccessRoleTier.CLASS_TEACHER -> targetClass.code
            AccessRoleTier.ENROLLED_PARENT -> targetClass.code
            AccessRoleTier.PLAY_STORE_USER -> ""
            else -> "" // This fixes the non-exhaustive 'when' error
        }
        return unlockedCsv
    }

    // ... (keep the rest of your methods and class closing braces below)
}
