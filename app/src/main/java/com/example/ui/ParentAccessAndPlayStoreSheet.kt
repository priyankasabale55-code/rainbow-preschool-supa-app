package com.example.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.AccessControlConfig
import com.example.data.AccessRoleTier
import com.example.data.PlayBillingManager
import com.example.data.PlayStoreClassProduct
import com.example.data.PreschoolClass
import com.example.data.PreschoolStudent
import com.example.ui.theme.HomeworkAmberContainer
import com.example.ui.theme.HomeworkAmberDark
import com.example.ui.theme.InstructionRoseContainer
import com.example.ui.theme.InstructionRoseDark
import com.example.ui.theme.TaskMint
import com.example.ui.theme.TaskMintContainer
import com.example.ui.theme.TaskMintDark

/**
 * Prominent banner displayed when a Parent is logged in for their specific Child & Class.
 * Enforces: Parent Login -> Child -> Class -> Access ONLY Child's Class.
 */
@Composable
fun ParentChildActiveSessionBanner(
    accessConfig: AccessControlConfig,
    onSwitchOrLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val childClass = accessConfig.loggedInClass
    if (accessConfig.isParentLoggedInForChild && childClass != null) {
        val palette = childClass.palette()

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("parent_active_session_banner"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.softContainer),
            border = BorderStroke(1.5.dp, palette.accent)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = palette.accent,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChildCare,
                                contentDescription = "Child Portal",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Free Parent Portal: ${accessConfig.loggedInChildName} (${accessConfig.loggedInChildRoll})",
                            style = MaterialTheme.typography.titleSmall,
                            color = palette.deepText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "100% Free Access • ${childClass.displayName} Only • Rainbow Preschool Supa",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.deepText.copy(alpha = 0.85f)
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onSwitchOrLogoutClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = CircleShape,
                    modifier = Modifier.testTag("switch_parent_login_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Switch Child",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Switch Child", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    } else {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clickable(onClick = onSwitchOrLogoutClick)
                .testTag("parent_free_access_cta_banner"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TaskMintContainer.copy(alpha = 0.65f)),
            border = BorderStroke(1.5.dp, TaskMint)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = TaskMint,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChildCare,
                                contentDescription = "Free Parent-Child Access",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "100% Free Parent-Child Access",
                            style = MaterialTheme.typography.titleSmall,
                            color = TaskMintDark,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap to select your child & access their class for free",
                            style = MaterialTheme.typography.labelSmall,
                            color = TaskMintDark.copy(alpha = 0.85f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onSwitchOrLogoutClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TaskMint),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = CircleShape,
                    modifier = Modifier.testTag("open_free_parent_login_button")
                ) {
                    Text(
                        text = "Select Child (Free)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Shown when a parent is logged in for another child's class and taps a different class tab.
 * Allows switching to a child in this class for 100% free.
 */
@Composable
fun LockedClassPaywallCard(
    selectedClass: PreschoolClass,
    accessConfig: AccessControlConfig,
    onOpenUnlockSheet: (initialTab: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val classPalette = selectedClass.palette()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("locked_class_paywall_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(2.dp, classPalette.accent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(classPalette.softContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChildCare,
                    contentDescription = "Free Parent Child Access",
                    tint = classPalette.accent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = TaskMintContainer,
                shape = CircleShape
            ) {
                Text(
                    text = "100% Free Parent-Child Access",
                    style = MaterialTheme.typography.labelMedium,
                    color = TaskMintDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Access ${selectedClass.displayName} (${selectedClass.ageGroup}) for Free",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "All Rainbow Preschool Supa parents get 100% FREE access to their child's class (${selectedClass.displayName}), Daily Tasks, Homework, 4-Line Finger Tracing, and Monthly Progress Reports.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { onOpenUnlockSheet(0) }, // Tab 0: Free Parent Login
                colors = ButtonDefaults.buttonColors(containerColor = TaskMint),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("unlock_with_parent_code_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ChildCare,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Select Your Child (100% Free Access)",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Questions? Call Rainbow Preschool Supa: $SCHOOL_PHONE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ClassAccessAndPlayStoreBottomSheet(
    initialTab: Int = 0,
    selectedClass: PreschoolClass,
    allStudents: List<PreschoolStudent>,
    accessConfig: AccessControlConfig,
    billingStatusMessage: String,
    onLoginParentForChildAndClass: (PreschoolClass, PreschoolStudent, String) -> Boolean,
    onLoginParentWithNewChildFree: (PreschoolClass, String, String, String) -> Unit = { _, _, _, _ -> },
    onPurchasePlayStoreProduct: (Activity?, PlayStoreClassProduct) -> Unit,
    onRestorePlayStorePurchases: () -> Unit,
    onSaveAdminPasscodes: (
        nurseryCode: String,
        lkgCode: String,
        ukgCode: String,
        teacherPin: String
    ) -> Unit,
    onSwitchAccessSimulationMode: (AccessRoleTier, PreschoolClass?, PreschoolStudent?) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var activeTab by remember { mutableIntStateOf(initialTab) }

    // Tab 0 state: 100% Free Parent Login (Select Class -> Select or Enter Child -> Free Access to Child's Class)
    var targetParentClass by remember {
        mutableStateOf(accessConfig.loggedInClass ?: selectedClass)
    }
    val studentsInTargetClass = remember(targetParentClass, allStudents) {
        allStudents.filter { it.classCode == targetParentClass.code }
    }
    var selectedChild by remember(targetParentClass, studentsInTargetClass) {
        mutableStateOf(
            studentsInTargetClass.find { it.id == accessConfig.loggedInStudentId }
                ?: studentsInTargetClass.firstOrNull()
        )
    }
    var isEnteringNewChild by remember { mutableStateOf(false) }
    var customChildName by remember { mutableStateOf("") }
    var customChildRoll by remember(targetParentClass) {
        mutableStateOf("${targetParentClass.displayName.first()}-05")
    }
    var customParentName by remember { mutableStateOf("") }
    var customChildNameError by remember { mutableStateOf(false) }

    var enteredParentCode by remember { mutableStateOf("") }
    var parentFeedback by remember { mutableStateOf<String?>(null) }
    var parentUnlockSuccess by remember { mutableStateOf(false) }

    // Tab 2 state: Teacher / Admin Passcode Manager
    var isAdminUnlocked by remember {
        mutableStateOf(accessConfig.roleTier == AccessRoleTier.TEACHER_ADMIN)
    }
    var enteredAdminPin by remember { mutableStateOf("") }
    var adminPinError by remember { mutableStateOf(false) }

    var editNurseryCode by remember(accessConfig) { mutableStateOf(accessConfig.nurseryParentCode) }
    var editLkgCode by remember(accessConfig) { mutableStateOf(accessConfig.lkgParentCode) }
    var editUkgCode by remember(accessConfig) { mutableStateOf(accessConfig.ukgParentCode) }
    var editTeacherPin by remember(accessConfig) { mutableStateOf(accessConfig.teacherPin) }
    var adminSaveMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RainbowSchoolLogoBadge(size = 52.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Free Parent-Child Portal",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = if (accessConfig.isParentLoggedInForChild) {
                            "Active Child: ${accessConfig.loggedInChildName} (${accessConfig.loggedInClass?.displayName} • Free)"
                        } else {
                            "100% Free Access for Parents • Mode: ${accessConfig.roleTier.label}"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = TaskMintDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs: 1. Free Parent Access | 2. All Classes Free | 3. Teacher / Admin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    label = { Text("Parent (Free)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ChildCare,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("access_tab_parent_code")
                )
                FilterChip(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    label = { Text("Free Passes") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("access_tab_play_store")
                )
                FilterChip(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    label = { Text("Teacher/Admin") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("access_tab_admin")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (activeTab) {
                // ==================== TAB 0: 100% FREE PARENT-CHILD ACCESS ====================
                0 -> {
                    Surface(
                        color = TaskMintContainer,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, TaskMint),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "100% FREE FOR PARENTS  ➔  Select Class  ➔  Select Child  ➔  Instant Access",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = TaskMintDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Every parent gets completely FREE access to their child's class (${targetParentClass.displayName}), Daily Tasks, Homework, 4-Line Finger Tracing, and Monthly Report. No payment required!",
                                style = MaterialTheme.typography.labelSmall,
                                color = TaskMintDark.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 1: Select Child's Class (Nursery, LKG, UKG)
                    Text(
                        text = "1. Select Your Child's Class:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PreschoolClass.entries.forEach { cls ->
                            val palette = cls.palette()
                            val selected = targetParentClass == cls
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    targetParentClass = cls
                                    parentFeedback = null
                                },
                                label = {
                                    Text("${cls.displayName} (${cls.ageGroup})")
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = palette.softContainer,
                                    selectedLabelColor = palette.deepText
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 2: Select Child from List OR Enter New Child Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Select Your Child (${targetParentClass.displayName}):",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        FilterChip(
                            selected = isEnteringNewChild,
                            onClick = {
                                isEnteringNewChild = !isEnteringNewChild
                                customChildNameError = false
                            },
                            label = {
                                Text(
                                    text = if (isEnteringNewChild) "Choose Existing Kid" else "+ Enter My Child",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    if (!isEnteringNewChild) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            studentsInTargetClass.forEach { student ->
                                val isSelected = selectedChild?.id == student.id
                                val palette = targetParentClass.palette()
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedChild = student
                                        targetParentClass = student.preschoolClass
                                        parentFeedback = null
                                    },
                                    label = {
                                        Text(
                                            text = "${student.rollNumber} • ${student.fullName}",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ChildCare,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = palette.softContainer,
                                        selectedLabelColor = palette.deepText,
                                        selectedLeadingIconColor = palette.accent
                                    ),
                                    modifier = Modifier.testTag("parent_login_child_${student.id}")
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customChildName,
                                onValueChange = {
                                    customChildName = it
                                    if (it.isNotBlank()) customChildNameError = false
                                },
                                label = { Text("Your Child's Full Name *") },
                                placeholder = { Text("e.g., Aarav Patil") },
                                isError = customChildNameError,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_child_name_input")
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = customChildRoll,
                                    onValueChange = { customChildRoll = it },
                                    label = { Text("Roll No.") },
                                    singleLine = true,
                                    modifier = Modifier.weight(0.4f)
                                )
                                OutlinedTextField(
                                    value = customParentName,
                                    onValueChange = { customParentName = it },
                                    label = { Text("Parent Name (Optional)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(0.6f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Optional School Code input (kept optional so parents can leave it blank for instant free access)
                    OutlinedTextField(
                        value = enteredParentCode,
                        onValueChange = {
                            enteredParentCode = it.uppercase()
                            parentFeedback = null
                        },
                        label = {
                            Text("Optional School Code (Leave blank for Free Access)")
                        },
                        placeholder = {
                            Text("No code needed • 100% Free for Parents")
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_code_input")
                    )

                    parentFeedback?.let { msg ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = if (parentUnlockSuccess) TaskMintContainer else InstructionRoseContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (parentUnlockSuccess) TaskMintDark else InstructionRoseDark,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (isEnteringNewChild) {
                                if (customChildName.isBlank()) {
                                    customChildNameError = true
                                } else {
                                    onLoginParentWithNewChildFree(
                                        targetParentClass,
                                        customChildName,
                                        customChildRoll,
                                        customParentName
                                    )
                                    onDismiss()
                                }
                            } else {
                                val child = selectedChild
                                if (child != null) {
                                    val ok = onLoginParentForChildAndClass(
                                        targetParentClass,
                                        child,
                                        enteredParentCode.trim()
                                    )
                                    parentUnlockSuccess = ok
                                    if (ok) {
                                        onDismiss()
                                    } else {
                                        parentFeedback =
                                            "Tip: Leave the code box blank or enter ${accessConfig.codeForClass(targetParentClass)} for free access."
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TaskMint
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("verify_parent_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val displayKid = if (isEnteringNewChild) {
                            customChildName.ifBlank { "My Child" }
                        } else {
                            selectedChild?.fullName ?: "Child"
                        }
                        Text(
                            text = "Free Access for $displayKid (${targetParentClass.displayName})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // ==================== TAB 1: 100% FREE CLASS ACCESS FOR PARENTS ====================
                1 -> {
                    Text(
                        text = "100% Free Class Access for Parents & Students",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Rainbow Preschool Supa provides completely FREE access for parents to view their child's class updates, tracing practice, and monthly activity reports:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        PreschoolClass.entries.forEach { cls ->
                            val firstStudentInClass = allStudents.firstOrNull { it.classCode == cls.code }
                            val accentColor = cls.palette().accent
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.6f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                        Surface(
                                            color = accentColor.copy(alpha = 0.14f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "${cls.displayName} (${cls.ageGroup})",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = accentColor,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        Surface(
                                            color = TaskMintContainer,
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = "100% FREE",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = TaskMintDark,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "${cls.displayName} Parent-Child Free Access",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Includes Daily Tasks, Homework, 4-Line Finger Tracing, Photo Quizzes & Monthly Report for ${cls.displayName}.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            if (firstStudentInClass != null) {
                                                onLoginParentForChildAndClass(cls, firstStudentInClass, "")
                                            } else {
                                                onSwitchAccessSimulationMode(AccessRoleTier.ENROLLED_PARENT, cls, null)
                                            }
                                            onDismiss()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TaskMint),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Open ${cls.displayName} Parent Portal (Free)",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==================== TAB 2: SCHOOL ADMIN PASSCODE MANAGER ====================
                2 -> {
                    if (!isAdminUnlocked) {
                        Text(
                            text = "Teacher / Principal Admin Verification",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Enter the School Admin PIN (Default: ${accessConfig.teacherPin}) to manage class-wise parent codes or unlock all classes:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = enteredAdminPin,
                            onValueChange = {
                                enteredAdminPin = it
                                adminPinError = false
                            },
                            label = { Text("School Admin PIN") },
                            placeholder = { Text("Default: 9090") },
                            isError = adminPinError,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (enteredAdminPin.trim() == accessConfig.teacherPin) {
                                    isAdminUnlocked = true
                                    onSwitchAccessSimulationMode(AccessRoleTier.TEACHER_ADMIN, null, null)
                                } else {
                                    adminPinError = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Unlock School Admin Controls")
                        }
                    } else {
                        Text(
                            text = "Class-Wise Parent Access Codes (Share with Enrolled Parents)",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Customize the passcodes for Nursery, LKG, and UKG below and tap Share to send to your class parent WhatsApp groups:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Nursery Code Row
                        ClassCodeAdminRow(
                            classLabel = "Nursery Code",
                            codeValue = editNurseryCode,
                            onCodeChange = { editNurseryCode = it.uppercase() },
                            onShareClick = {
                                shareClassCodeWithParents(
                                    context = context,
                                    preschoolClass = PreschoolClass.NURSERY,
                                    code = editNurseryCode
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // LKG Code Row
                        ClassCodeAdminRow(
                            classLabel = "LKG Code",
                            codeValue = editLkgCode,
                            onCodeChange = { editLkgCode = it.uppercase() },
                            onShareClick = {
                                shareClassCodeWithParents(
                                    context = context,
                                    preschoolClass = PreschoolClass.LKG,
                                    code = editLkgCode
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // UKG Code Row
                        ClassCodeAdminRow(
                            classLabel = "UKG Code",
                            codeValue = editUkgCode,
                            onCodeChange = { editUkgCode = it.uppercase() },
                            onShareClick = {
                                shareClassCodeWithParents(
                                    context = context,
                                    preschoolClass = PreschoolClass.UKG,
                                    code = editUkgCode
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editTeacherPin,
                            onValueChange = { editTeacherPin = it },
                            label = { Text("Teacher / Principal Master PIN") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        adminSaveMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelMedium,
                                color = TaskMintDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                onSaveAdminPasscodes(
                                    editNurseryCode,
                                    editLkgCode,
                                    editUkgCode,
                                    editTeacherPin
                                )
                                adminSaveMessage = "Saved updated Class Passcodes & Admin PIN!"
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Class Passcodes")
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Preview / Test Access Modes Live:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Switch modes below to test what Enrolled Parents (Child -> Class -> Only Child's Class) or Play Store Visitors see:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(
                                onClick = {
                                    onSwitchAccessSimulationMode(AccessRoleTier.TEACHER_ADMIN, null, null)
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("School Admin / Teacher Mode (All 3 Classes Unlocked)")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PreschoolClass.entries.forEach { cls ->
                                    val firstKid = allStudents.firstOrNull { it.classCode == cls.code }
                                    OutlinedButton(
                                        onClick = {
                                            onSwitchAccessSimulationMode(
                                                AccessRoleTier.ENROLLED_PARENT,
                                                cls,
                                                firstKid
                                            )
                                            onDismiss()
                                        },
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "${cls.displayName} Parent",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    onSwitchAccessSimulationMode(AccessRoleTier.PLAY_STORE_USER, null, null)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1D2847)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("simulate_play_store_user_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Test as New Play Store User (Paywall Active)",
                                    color = Color.White
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
private fun ClassCodeAdminRow(
    classLabel: String,
    codeValue: String,
    onCodeChange: (String) -> Unit,
    onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = codeValue,
            onValueChange = onCodeChange,
            label = { Text(classLabel) },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        FilledTonalButton(
            onClick = onShareClick,
            modifier = Modifier.height(54.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share Class Code",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Share", style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun shareClassCodeWithParents(
    context: android.content.Context,
    preschoolClass: PreschoolClass,
    code: String
) {
    val message = buildString {
        appendLine("🌈 RAINBOW PRE-SCHOOL SUPA")
        appendLine(SCHOOL_MOTTO)
        appendLine("━━━━━━━━━━━━━━━━━━━━━━")
        appendLine("Dear ${preschoolClass.displayName} Parents,")
        appendLine("Open the Rainbow Pre-School Supa app -> Parent Login -> Select your Child & Class (${preschoolClass.displayName}) and enter your Class Access Code:")
        appendLine()
        appendLine("🔑 ${preschoolClass.displayName} Access Code: $code")
        appendLine("📞 School Contact: $SCHOOL_PHONE | $SCHOOL_EMAIL")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Rainbow Pre-School Supa - ${preschoolClass.displayName} Parent Access Code")
        putExtra(Intent.EXTRA_TEXT, message)
    }
    runCatching {
        context.startActivity(Intent.createChooser(intent, "Share ${preschoolClass.displayName} Code"))
    }
}
