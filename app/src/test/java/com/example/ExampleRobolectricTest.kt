package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AccessControlConfig
import com.example.data.AccessRoleTier
import com.example.data.PreschoolClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Rainbow Preschool Supa", appName)
  }

  @Test
  fun `parent login restricts access strictly to child class`() {
    val parentConfig = AccessControlConfig(
      roleTierCode = AccessRoleTier.ENROLLED_PARENT.code,
      unlockedClassesCsv = PreschoolClass.LKG.code,
      loggedInStudentId = 5,
      loggedInChildName = "Reyansh Jadhav",
      loggedInChildRoll = "L-01",
      loggedInClassCode = PreschoolClass.LKG.code
    )

    assertTrue(parentConfig.isParentLoggedInForChild)
    assertEquals(PreschoolClass.LKG, parentConfig.loggedInClass)
    assertTrue(parentConfig.isClassUnlocked(PreschoolClass.LKG))
    assertFalse(parentConfig.isClassUnlocked(PreschoolClass.NURSERY))
    assertFalse(parentConfig.isClassUnlocked(PreschoolClass.UKG))
  }
}
