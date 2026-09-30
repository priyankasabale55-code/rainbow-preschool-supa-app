package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.EntryCategory
import com.example.data.PreschoolClass
import com.example.data.PreschoolEntry
import com.example.data.PreschoolSeedData
import com.example.data.QuickTemplate
import com.example.ui.theme.InstructionRoseContainer
import com.example.ui.theme.InstructionRoseDark
import com.example.ui.theme.TaskMint
import com.example.ui.theme.TaskMintContainer
import com.example.ui.theme.TaskMintDark

@Composable
fun PreschoolEntryCard(
    entry: PreschoolEntry,
    isTeacherMode: Boolean,
    onCardClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onTogglePin: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = entry.category
    val catPalette = category.palette()
    val classPalette = entry.preschoolClass.palette()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable { onCardClick() }
            .testTag("entry_card_${entry.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (entry.isPinned || entry.isCompleted) 1.8.dp else 1.dp,
            color = when {
                entry.isCompleted -> TaskMint
                entry.isPinned -> catPalette.accent
                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (entry.isPinned) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Category Badge, Subject Tag, Class Badge, Pin Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = catPalette.container,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = catPalette.icon,
                                contentDescription = category.label,
                                tint = catPalette.onContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = category.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = catPalette.onContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        color = classPalette.softContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = entry.preschoolClass.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = classPalette.deepText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }

                    if (entry.subjectTag.isNotBlank()) {
                        Text(
                            text = "• ${entry.subjectTag}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (entry.isPinned) {
                    Surface(
                        color = InstructionRoseContainer,
                        shape = CircleShape,
                        modifier = Modifier.clickable { onTogglePin() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned Important",
                                tint = InstructionRoseDark,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Pinned",
                                style = MaterialTheme.typography.labelSmall,
                                color = InstructionRoseDark
                            )
                        }
                    }
                } else if (isTeacherMode) {
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PushPin,
                            contentDescription = "Pin entry",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Description
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = entry.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Materials Needed Pill (if present)
            if (entry.materialsNeeded.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backpack,
                            contentDescription = "Materials or Books Required",
                            tint = classPalette.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bring / Use: ${entry.materialsNeeded}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Footer: Due/Schedule + Teacher + Completion/Edit Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Schedule or due time",
                            tint = catPalette.accent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = entry.scheduleOrDue,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isTeacherMode) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("edit_entry_${entry.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit entry",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("delete_entry_${entry.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete entry",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Surface(
                        color = if (entry.isCompleted) TaskMintContainer else catPalette.container,
                        shape = CircleShape,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onToggleComplete() }
                            .testTag("complete_entry_${entry.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (entry.isCompleted) {
                                    Icons.Default.CheckCircle
                                } else {
                                    Icons.Outlined.Circle
                                },
                                contentDescription = "Toggle completion status",
                                tint = if (entry.isCompleted) TaskMintDark else catPalette.onContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (entry.isCompleted) {
                                    category.completionLabel
                                } else {
                                    "Mark Done"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = if (entry.isCompleted) TaskMintDark else catPalette.onContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddOrEditEntryBottomSheet(
    initialClass: PreschoolClass,
    initialCategory: EntryCategory,
    editingEntry: PreschoolEntry?,
    onDismiss: () -> Unit,
    onSaveEntry: (
        id: Int,
        preschoolClass: PreschoolClass,
        category: EntryCategory,
        title: String,
        description: String,
        subjectTag: String,
        scheduleOrDue: String,
        materialsNeeded: String,
        teacherName: String,
        isPinned: Boolean,
        isCompleted: Boolean
    ) -> Unit
) {
    BackHandler { onDismiss() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedClass by remember {
        mutableStateOf(editingEntry?.preschoolClass ?: initialClass)
    }
    var selectedCategory by remember {
        mutableStateOf(editingEntry?.category ?: initialCategory)
    }
    var title by remember { mutableStateOf(editingEntry?.title ?: "") }
    var description by remember { mutableStateOf(editingEntry?.description ?: "") }
    var subjectTag by remember {
        mutableStateOf(
            editingEntry?.subjectTag
                ?: PreschoolSeedData.subjectSuggestions[initialCategory]?.firstOrNull().orEmpty()
        )
    }
    var scheduleOrDue by remember {
        mutableStateOf(
            editingEntry?.scheduleOrDue ?: when (initialCategory) {
                EntryCategory.DAILY_TASK -> "Today • Morning Session"
                EntryCategory.HOMEWORK -> "Due Tomorrow"
                EntryCategory.INSTRUCTION -> "Important • This Week"
            }
        )
    }
    var materialsNeeded by remember { mutableStateOf(editingEntry?.materialsNeeded ?: "") }
    var teacherName by remember {
        mutableStateOf(editingEntry?.teacherName ?: initialClass.defaultTeacher)
    }
    var isPinned by remember {
        mutableStateOf(
            editingEntry?.isPinned ?: (initialCategory == EntryCategory.INSTRUCTION)
        )
    }
    var titleError by remember { mutableStateOf(false) }

    val quickTemplates: List<QuickTemplate> = remember(selectedClass) {
        PreschoolSeedData.getTemplatesForClass(selectedClass)
    }

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
            Text(
                text = if (editingEntry != null) {
                    "Edit ${selectedCategory.label}"
                } else {
                    "Add to Rainbow Preschool Supa"
                },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Assign class-wise Daily Task, Homework, or Instruction for Nursery, LKG, or UKG.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Select Class (Nursery / LKG / UKG)
            Text(
                text = "1. Select Class",
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
                    val selected = selectedClass == cls
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedClass = cls
                            if (editingEntry == null) {
                                teacherName = cls.defaultTeacher
                            }
                        },
                        label = {
                            Text(
                                text = "${cls.displayName} (${cls.ageGroup})",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = palette.icon,
                                contentDescription = cls.displayName,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = palette.softContainer,
                            selectedLabelColor = palette.deepText,
                            selectedLeadingIconColor = palette.accent
                        ),
                        modifier = Modifier.testTag("sheet_class_${cls.code}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Select Entry Type (Daily Task / Homework / Instruction)
            Text(
                text = "2. Select Category",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EntryCategory.entries.forEach { cat ->
                    val catPalette = cat.palette()
                    val selected = selectedCategory == cat
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedCategory = cat
                            val firstSuggestion = PreschoolSeedData.subjectSuggestions[cat]?.firstOrNull()
                            if (firstSuggestion != null && editingEntry == null) {
                                subjectTag = firstSuggestion
                            }
                            if (editingEntry == null) {
                                scheduleOrDue = when (cat) {
                                    EntryCategory.DAILY_TASK -> "Today • Morning Session"
                                    EntryCategory.HOMEWORK -> "Due Tomorrow"
                                    EntryCategory.INSTRUCTION -> "Important • Parent Notice"
                                }
                            }
                        },
                        label = {
                            Text(
                                text = cat.label,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = catPalette.icon,
                                contentDescription = cat.label,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = catPalette.container,
                            selectedLabelColor = catPalette.onContainer,
                            selectedLeadingIconColor = catPalette.accent
                        ),
                        modifier = Modifier.testTag("sheet_category_${cat.code}")
                    )
                }
            }

            // One-tap Quick Templates for the selected class
            if (editingEntry == null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Quick templates",
                        tint = selectedClass.palette().accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Quick Fill Templates for ${selectedClass.displayName}:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickTemplates.forEach { tpl ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = tpl.category.palette().container.copy(alpha = 0.65f),
                            border = BorderStroke(1.dp, tpl.category.palette().accent.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedCategory = tpl.category
                                    title = tpl.title
                                    description = tpl.description
                                    subjectTag = tpl.subjectTag
                                    scheduleOrDue = tpl.scheduleOrDue
                                    materialsNeeded = tpl.materialsNeeded
                                    isPinned = tpl.isPinned
                                    titleError = false
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(210.dp)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "${tpl.category.label}: ${tpl.title}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = tpl.category.palette().onContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Tap to auto-fill form",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Title Input
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) titleError = false
                },
                label = { Text("${selectedCategory.label} Title *") },
                placeholder = {
                    Text(
                        when (selectedCategory) {
                            EntryCategory.DAILY_TASK -> "e.g., Phonics Sound Basket & Clay Play"
                            EntryCategory.HOMEWORK -> "e.g., Trace Letters A–D in Workbook Pg. 12"
                            EntryCategory.INSTRUCTION -> "e.g., Red Color Day Outfit on Friday"
                        }
                    )
                },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text("Please enter a title for this ${selectedCategory.label.lowercase()}.") }
                } else null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_title_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Subject / Topic Tag Suggestions
            Text(
                text = "Subject / Topic Tag",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            val suggestions = PreschoolSeedData.subjectSuggestions[selectedCategory].orEmpty()
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                suggestions.forEach { tag ->
                    FilterChip(
                        selected = subjectTag == tag,
                        onClick = { subjectTag = tag },
                        label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Details / Step-by-Step Instructions
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = {
                    Text(
                        when (selectedCategory) {
                            EntryCategory.DAILY_TASK -> "Activity Steps & Learning Outcome"
                            EntryCategory.HOMEWORK -> "Homework Instructions for Parents"
                            EntryCategory.INSTRUCTION -> "Detailed Parent / Class Instruction"
                        }
                    )
                },
                placeholder = {
                    Text("Write clear steps, page numbers, or instructions for parents and students…")
                },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_description_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 6. Schedule / Due Date & Materials Needed
            OutlinedTextField(
                value = scheduleOrDue,
                onValueChange = { scheduleOrDue = it },
                label = {
                    Text(
                        if (selectedCategory == EntryCategory.HOMEWORK) "Due Date" else "Time / Day"
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_schedule_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = materialsNeeded,
                onValueChange = { materialsNeeded = it },
                label = { Text("Books / Materials to Bring or Use (Optional)") },
                placeholder = { Text("e.g., 4-Line Notebook, Crayons, Yellow Fruit in Tiffin") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_materials_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pin as Important Switch
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pin at Top of ${selectedClass.displayName} Board",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Highlights this update with a priority notice badge",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        modifier = Modifier.testTag("entry_pin_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            titleError = true
                        } else {
                            onSaveEntry(
                                editingEntry?.id ?: 0,
                                selectedClass,
                                selectedCategory,
                                title,
                                description.ifBlank {
                                    "Assigned for ${selectedClass.displayName} class (${selectedCategory.label})."
                                },
                                subjectTag,
                                scheduleOrDue,
                                materialsNeeded,
                                teacherName,
                                isPinned,
                                editingEntry?.isCompleted ?: false
                            )
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedClass.palette().accent
                    ),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(50.dp)
                        .testTag("save_entry_button")
                ) {
                    Text(
                        text = if (editingEntry != null) {
                            "Update ${selectedCategory.label}"
                        } else {
                            "Post to ${selectedClass.displayName}"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailBottomSheet(
    entry: PreschoolEntry,
    isTeacherMode: Boolean,
    onDismiss: () -> Unit,
    onToggleComplete: () -> Unit,
    onTogglePin: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    BackHandler { onDismiss() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val catPalette = entry.category.palette()
    val classPalette = entry.preschoolClass.palette()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = catPalette.container,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = catPalette.icon,
                                contentDescription = entry.category.label,
                                tint = catPalette.onContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = entry.category.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = catPalette.onContainer
                            )
                        }
                    }

                    Surface(
                        color = classPalette.softContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${entry.preschoolClass.displayName} Class",
                            style = MaterialTheme.typography.labelLarge,
                            color = classPalette.deepText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                if (entry.isPinned) {
                    Surface(
                        color = InstructionRoseContainer,
                        shape = CircleShape
                    ) {
                        Text(
                            text = "Pinned Notice",
                            style = MaterialTheme.typography.labelSmall,
                            color = InstructionRoseDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = entry.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${entry.subjectTag} • ${entry.scheduleOrDue}",
                style = MaterialTheme.typography.labelLarge,
                color = classPalette.accent
            )

            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = entry.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            if (entry.materialsNeeded.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = classPalette.softContainer.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backpack,
                            contentDescription = "Materials needed",
                            tint = classPalette.deepText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Books / Materials Checklist",
                                style = MaterialTheme.typography.labelMedium,
                                color = classPalette.deepText
                            )
                            Text(
                                text = entry.materialsNeeded,
                                style = MaterialTheme.typography.bodyMedium,
                                color = classPalette.deepText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Rainbow Pre-School Supa • ${entry.preschoolClass.displayName} (${entry.preschoolClass.roomName})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onToggleComplete()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (entry.isCompleted) TaskMint else classPalette.accent
                    ),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (entry.isCompleted) "Completed (Undo)" else entry.category.completionLabel,
                        color = Color.White
                    )
                }

                if (isTeacherMode) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }

                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onTogglePin,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pin",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (entry.isPinned) "Unpin" else "Pin")
                    }
                }
            }
        }
    }
}

@Composable
fun ClassAccessPortalDialog(
    lockedClass: PreschoolClass?,
    onSelectClassLock: (PreschoolClass?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Class-Wise Access Mode",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Choose whether to browse all classes (Principal / Teacher View) or lock the app to a single class portal (Nursery, LKG, or UKG):",
                    style = MaterialTheme.typography.bodyMedium
                )

                // All classes option
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (lockedClass == null) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelectClassLock(null) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "All Classes Access (Nursery, LKG & UKG)",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "Switch freely between all three classes",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                PreschoolClass.entries.forEach { cls ->
                    val palette = cls.palette()
                    val isSelected = lockedClass == cls
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) {
                            palette.softContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        },
                        border = if (isSelected) BorderStroke(1.5.dp, palette.accent) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectClassLock(cls) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = palette.accent
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${cls.displayName} Only Portal (${cls.ageGroup})",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (isSelected) palette.deepText else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = cls.roomName,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
