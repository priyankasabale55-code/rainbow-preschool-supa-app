package com.example.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PictureQuizPreset
import com.example.data.PreschoolClass
import com.example.data.PreschoolStudent
import com.example.data.QuestionKind
import com.example.data.SyllabusQuestion
import com.example.data.SyllabusSeedData
import java.io.File
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.HomeworkAmber
import com.example.ui.theme.HomeworkAmberContainer
import com.example.ui.theme.HomeworkAmberDark
import com.example.ui.theme.StripeBlue
import com.example.ui.theme.StripeGreen
import com.example.ui.theme.StripeOrange
import com.example.ui.theme.StripePurple
import com.example.ui.theme.StripeRed
import com.example.ui.theme.TaskMint
import com.example.ui.theme.TaskMintContainer
import com.example.ui.theme.TaskMintDark

data class TracedStroke(
    val points: List<Offset>,
    val color: Color
)

@Composable
fun SyllabusPracticeHeaderAndFilters(
    selectedClass: PreschoolClass,
    students: List<PreschoolStudent>,
    activeStudent: PreschoolStudent?,
    selectedTopic: String,
    isTeacherMode: Boolean,
    onSelectStudent: (Int) -> Unit,
    onSelectTopic: (String) -> Unit,
    onAddQuestionClick: () -> Unit,
    onAddStudentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val classPalette = selectedClass.palette()
    val topics = remember(selectedClass) {
        listOf("All Topics") + (SyllabusSeedData.syllabusTopicsByClass[selectedClass] ?: emptyList())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Active Student Selector Card (so solved questions count toward their Monthly Report!)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, classPalette.accent.copy(alpha = 0.35f)),
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Solving Activity As Student (${selectedClass.displayName}):",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Every solved tracing or syllabus question updates the student's Monthly Report",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isTeacherMode) {
                        FilledTonalButton(
                            onClick = onAddStudentClick,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_student_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Add Student",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Kid", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    students.forEach { student ->
                        val isSelected = activeStudent?.id == student.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectStudent(student.id) },
                            label = {
                                Text(
                                    text = "${student.rollNumber} • ${student.fullName}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = classPalette.softContainer,
                                selectedLabelColor = classPalette.deepText
                            ),
                            modifier = Modifier.testTag("select_student_${student.id}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Syllabus Topics Header + Teacher "+ Add Syllabus Question" button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${selectedClass.displayName} Tracing, Photo Quiz & MCQ",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Tap any card for finger tracing, photo quiz & multiple-choice options",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onAddQuestionClick,
                colors = ButtonDefaults.buttonColors(containerColor = classPalette.accent),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("add_syllabus_question_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Add Photo / Tracing Question",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "+ Photo / Quiz",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Syllabus Topic Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            topics.forEach { topic ->
                FilterChip(
                    selected = selectedTopic == topic,
                    onClick = { onSelectTopic(topic) },
                    label = { Text(topic, style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = classPalette.softContainer,
                        selectedLabelColor = classPalette.deepText
                    )
                )
            }
        }
    }
}

@Composable
fun SyllabusQuestionCard(
    question: SyllabusQuestion,
    activeStudentName: String,
    isTeacherMode: Boolean,
    onStartPractice: () -> Unit,
    onDeleteQuestion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val classPalette = question.preschoolClass.palette()
    val isTracing = question.questionKind == QuestionKind.TRACING_AND_MCQ
    val isPhotoQuiz = question.questionKind == QuestionKind.PHOTO_QUIZ || question.hasPhotoOrPicture

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable { onStartPractice() }
            .testTag("syllabus_question_card_${question.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, classPalette.accent.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = if (isTracing) TaskMintContainer else HomeworkAmberContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when {
                                    isTracing -> Icons.Default.Draw
                                    isPhotoQuiz -> Icons.Default.Image
                                    else -> Icons.Default.Quiz
                                },
                                contentDescription = null,
                                tint = if (isTracing) TaskMintDark else HomeworkAmberDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = question.questionKind.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isTracing) TaskMintDark else HomeworkAmberDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "• ${question.syllabusTopic}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = Color(0xFFFFF3CD),
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stars reward",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "+${question.starsReward}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isTracing) {
                    // Dotted Tracing Preview Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = classPalette.softContainer,
                        border = BorderStroke(1.5.dp, classPalette.accent),
                        modifier = Modifier.size(68.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = question.tracingTarget,
                                style = MaterialTheme.typography.headlineLarge,
                                color = classPalette.deepText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = question.questionText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isTracing && question.tracingHint.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tracing Guide: ${question.tracingHint}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (question.hasPhotoOrPicture) {
                Spacer(modifier = Modifier.height(10.dp))
                QuestionPhotoOrFlashcardBanner(
                    photoUri = question.photoUri,
                    picturePreset = question.picturePreset,
                    compact = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Preview of 4 MCQ options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                question.options.forEachIndexed { idx, opt ->
                    val optionLetter = ('A' + idx)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$optionLetter. $opt",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Student: $activeStudentName",
                    style = MaterialTheme.typography.labelMedium,
                    color = classPalette.accent
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isTeacherMode) {
                        IconButton(
                            onClick = onDeleteQuestion,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete question",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onStartPractice,
                        colors = ButtonDefaults.buttonColors(containerColor = classPalette.accent),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        shape = CircleShape,
                        modifier = Modifier.testTag("start_question_${question.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isTracing) "Trace & Answer" else "Solve Quiz",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveTracingAndQuizBottomSheet(
    question: SyllabusQuestion,
    student: PreschoolStudent,
    onDismiss: () -> Unit,
    onCompletedActivity: (isCorrect: Boolean) -> Unit
) {
    BackHandler { onDismiss() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val classPalette = question.preschoolClass.palette()
    val isTracingQuestion = question.questionKind == QuestionKind.TRACING_AND_MCQ

    val crayonColors = remember {
        listOf(StripeRed, StripeBlue, StripeGreen, StripePurple, StripeOrange)
    }
    var selectedCrayon by remember { mutableStateOf(crayonColors.first()) }
    val strokes = remember { mutableStateListOf<TracedStroke>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var selectedOptionIndex by remember { mutableIntStateOf(-1) }
    var hasSubmitted by remember { mutableStateOf(false) }

    val totalTracedPoints = strokes.sumOf { it.points.size } + currentPoints.size
    val tracingProgress = (totalTracedPoints / 35f).coerceIn(0f, 1f)
    val isTracingComplete = !isTracingQuestion || tracingProgress >= 0.35f

    val textMeasurer = rememberTextMeasurer()

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${question.preschoolClass.displayName} • ${question.syllabusTopic}",
                        style = MaterialTheme.typography.labelLarge,
                        color = classPalette.accent
                    )
                    Text(
                        text = "Student: ${student.fullName} (${student.rollNumber})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    color = Color(0xFFFFF3CD),
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${question.starsReward} Stars",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Photo / Picture Flashcard Display (if question has a photo or picture card)
            if (question.hasPhotoOrPicture) {
                QuestionPhotoOrFlashcardBanner(
                    photoUri = question.photoUri,
                    picturePreset = question.picturePreset,
                    compact = false
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Interactive Finger Tracing Board (when TRACING_AND_MCQ)
            if (isTracingQuestion) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Step 1: Finger Trace '${question.tracingTarget}' on the 4-Line Board",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${(tracingProgress * 100).toInt()}% Traced",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isTracingComplete) TaskMintDark else classPalette.accent
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { tracingProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = if (isTracingComplete) TaskMint else classPalette.accent,
                    trackColor = classPalette.softContainer
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Crayon Selector + Clear Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = "Select Crayon",
                            tint = selectedCrayon,
                            modifier = Modifier.size(18.dp)
                        )
                        crayonColors.forEach { color ->
                            val isSelected = selectedCrayon == color
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 30.dp else 24.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) Color.Black else Color.White,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedCrayon = color }
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            strokes.clear()
                            currentPoints = emptyList()
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Clear tracing",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 4-Line Preschool Tracing Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFFEF9))
                        .border(2.dp, classPalette.accent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .testTag("tracing_canvas")
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(selectedCrayon) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPoints = listOf(offset)
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        currentPoints = currentPoints + change.position
                                    },
                                    onDragEnd = {
                                        if (currentPoints.isNotEmpty()) {
                                            strokes.add(TracedStroke(currentPoints, selectedCrayon))
                                            currentPoints = emptyList()
                                        }
                                    }
                                )
                            }
                    ) {
                        val h = size.height
                        val w = size.width
                        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)

                        // Preschool 4-line notebook guidelines (Red top/bottom, Blue middle)
                        val line1Y = h * 0.20f
                        val line2Y = h * 0.42f
                        val line3Y = h * 0.64f
                        val line4Y = h * 0.84f

                        drawLine(
                            color = Color(0xFFE53935).copy(alpha = 0.45f),
                            start = Offset(0f, line1Y),
                            end = Offset(w, line1Y),
                            strokeWidth = 2.5f
                        )
                        drawLine(
                            color = Color(0xFF1E88E5).copy(alpha = 0.45f),
                            start = Offset(0f, line2Y),
                            end = Offset(w, line2Y),
                            strokeWidth = 2f,
                            pathEffect = dashEffect
                        )
                        drawLine(
                            color = Color(0xFF1E88E5).copy(alpha = 0.45f),
                            start = Offset(0f, line3Y),
                            end = Offset(w, line3Y),
                            strokeWidth = 2f,
                            pathEffect = dashEffect
                        )
                        drawLine(
                            color = Color(0xFFE53935).copy(alpha = 0.45f),
                            start = Offset(0f, line4Y),
                            end = Offset(w, line4Y),
                            strokeWidth = 2.5f
                        )

                        // Draw the large guide character/word in center
                        val fontSizeSp = if (question.tracingTarget.length > 2) 86.sp else 118.sp
                        val textLayout = textMeasurer.measure(
                            text = question.tracingTarget,
                            style = TextStyle(
                                fontFamily = FredokaFontFamily,
                                fontSize = fontSizeSp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB0B8C9).copy(alpha = 0.55f)
                            )
                        )
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(
                                x = (w - textLayout.size.width) / 2f,
                                y = (h - textLayout.size.height) / 2f
                            )
                        )

                        // Draw completed child strokes
                        strokes.forEach { stroke ->
                            if (stroke.points.size > 1) {
                                val path = Path().apply {
                                    moveTo(stroke.points.first().x, stroke.points.first().y)
                                    stroke.points.drop(1).forEach { pt ->
                                        lineTo(pt.x, pt.y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = stroke.color,
                                    style = Stroke(
                                        width = 22f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // Draw active stroke
                        if (currentPoints.size > 1) {
                            val activePath = Path().apply {
                                moveTo(currentPoints.first().x, currentPoints.first().y)
                                currentPoints.drop(1).forEach { pt ->
                                    lineTo(pt.x, pt.y)
                                }
                            }
                            drawPath(
                                path = activePath,
                                color = selectedCrayon,
                                style = Stroke(
                                    width = 22f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }

                if (question.tracingHint.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tip: ${question.tracingHint}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Step 2: Multiple-Choice Question with 4 Options
            Text(
                text = if (isTracingQuestion) {
                    "Step 2: Choose the Correct Option"
                } else {
                    "Syllabus Question:"
                },
                style = MaterialTheme.typography.titleSmall,
                color = classPalette.accent
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.options.forEachIndexed { index, optionText ->
                    val optionLetter = ('A' + index)
                    val isSelected = selectedOptionIndex == index
                    val isCorrectOption = index == question.correctOptionIndex

                    val bgColor = when {
                        hasSubmitted && isCorrectOption -> TaskMintContainer
                        hasSubmitted && isSelected && !isCorrectOption -> Color(0xFFFEE2E2)
                        isSelected -> classPalette.softContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    }
                    val borderColor = when {
                        hasSubmitted && isCorrectOption -> TaskMint
                        hasSubmitted && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.error
                        isSelected -> classPalette.accent
                        else -> Color.Transparent
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = bgColor,
                        border = BorderStroke(2.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = !hasSubmitted) {
                                selectedOptionIndex = index
                            }
                            .testTag("mcq_option_$index")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) classPalette.accent else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = optionLetter.toString(),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (hasSubmitted && isCorrectOption) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Correct option",
                                    tint = TaskMintDark
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (hasSubmitted) {
                val isCorrect = selectedOptionIndex == question.correctOptionIndex
                Surface(
                    color = if (isCorrect) TaskMintContainer else HomeworkAmberContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isCorrect) {
                                "Wonderful Job, ${student.fullName}! +${question.starsReward} Stars Added to Monthly Report!"
                            } else {
                                "Good Try, ${student.fullName}! Correct Answer: ${question.options[question.correctOptionIndex]} (+1 Practice Star Logged)"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isCorrect) TaskMintDark else HomeworkAmberDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = TaskMint),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("finish_activity_button")
                ) {
                    Text(
                        text = "Done • View Updated Monthly Report",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = {
                        if (selectedOptionIndex in 0..3) {
                            hasSubmitted = true
                            val isCorrect = selectedOptionIndex == question.correctOptionIndex
                            onCompletedActivity(isCorrect)
                        }
                    },
                    enabled = selectedOptionIndex in 0..3,
                    colors = ButtonDefaults.buttonColors(containerColor = classPalette.accent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_activity_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Submit Answer & Save to ${student.fullName}'s Report",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionPhotoOrFlashcardBanner(
    photoUri: String,
    picturePreset: PictureQuizPreset?,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val loadedBitmap = remember(photoUri) {
        if (photoUri.isNotBlank()) {
            runCatching {
                val file = File(photoUri)
                if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                } else null
            }.getOrNull()
        } else null
    }

    if (loadedBitmap != null) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(if (compact) 140.dp else 200.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = loadedBitmap,
                    contentDescription = "Quiz Question Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = Color(0xCC1D2847),
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = Color(0xFFF6C116),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Photo Quiz",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    } else if (picturePreset != null) {
        val bgColor = Color(picturePreset.bgHex)
        val accentColor = Color(picturePreset.accentHex)
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = bgColor,
            border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.55f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (compact) 10.dp else 16.dp, horizontal = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = accentColor.copy(alpha = 0.14f),
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Picture Card: ${picturePreset.title}",
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(if (compact) 6.dp else 10.dp))
                Text(
                    text = picturePreset.visualArt,
                    fontSize = if (compact) 34.sp else 48.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = if (compact) 38.sp else 54.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = picturePreset.caption,
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun copyPickedPhotoToInternalStorage(context: Context, uri: Uri): String? {
    return runCatching {
        val dir = File(context.filesDir, "quiz_photos").apply { mkdirs() }
        val targetFile = File(dir, "quiz_photo_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        if (targetFile.exists() && targetFile.length() > 0L) {
            targetFile.absolutePath
        } else null
    }.getOrNull()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddSyllabusQuestionBottomSheet(
    initialClass: PreschoolClass,
    onDismiss: () -> Unit,
    onSaveQuestion: (
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
        starsReward: Int,
        photoUri: String,
        picturePresetCode: String
    ) -> Unit
) {
    BackHandler { onDismiss() }

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedClass by remember { mutableStateOf(initialClass) }
    val topicsForClass = remember(selectedClass) {
        SyllabusSeedData.syllabusTopicsByClass[selectedClass] ?: listOf("General Syllabus")
    }
    var syllabusTopic by remember(selectedClass) { mutableStateOf(topicsForClass.first()) }
    var questionKind by remember { mutableStateOf(QuestionKind.PHOTO_QUIZ) }
    var tracingTarget by remember { mutableStateOf("B") }
    var tracingHint by remember { mutableStateOf("One standing line and two right curves") }
    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctOptionIndex by remember { mutableIntStateOf(0) }
    var selectedPhotoPath by remember { mutableStateOf("") }
    var selectedPresetCode by remember { mutableStateOf(PictureQuizPreset.APPLE.code) }
    var errorText by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = copyPickedPhotoToInternalStorage(context, uri)
            if (savedPath != null) {
                selectedPhotoPath = savedPath
                selectedPresetCode = ""
            }
        }
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
                text = "Add Photo Quiz, Tracing or MCQ Question",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Upload a photo from your phone/tablet gallery, pick a preschool picture flashcard, or add a tracing & MCQ question:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Select Class
            Text(
                text = "1. Select Class:",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PreschoolClass.entries.forEach { cls ->
                    FilterChip(
                        selected = selectedClass == cls,
                        onClick = { selectedClass = cls },
                        label = { Text(cls.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Select Question Kind (Tracing + MCQ, Photo / Picture Quiz, Syllabus MCQ)
            Text(
                text = "2. Select Question Type:",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuestionKind.entries.forEach { kind ->
                    FilterChip(
                        selected = questionKind == kind,
                        onClick = { questionKind = kind },
                        label = { Text(kind.label) },
                        leadingIcon = {
                            Icon(
                                imageVector = when (kind) {
                                    QuestionKind.TRACING_AND_MCQ -> Icons.Default.Draw
                                    QuestionKind.PHOTO_QUIZ -> Icons.Default.Image
                                    QuestionKind.SYLLABUS_MCQ -> Icons.Default.Quiz
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Photo / Picture Attachment Section (Gallery Photo Picker + Preschool Picture Cards)
            Surface(
                color = selectedClass.palette().softContainer.copy(alpha = 0.55f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, selectedClass.palette().accent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "3. Attach Quiz Photo or Picture Card",
                                style = MaterialTheme.typography.titleSmall,
                                color = selectedClass.palette().deepText
                            )
                            Text(
                                text = "Pick any photo from your device gallery or choose a picture flashcard below",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = selectedClass.palette().accent
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("pick_quiz_photo_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Pick Photo from Gallery",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Upload Photo",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Built-in Preschool Picture Flashcard Presets
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedPhotoPath.isBlank() && selectedPresetCode.isBlank(),
                            onClick = {
                                selectedPhotoPath = ""
                                selectedPresetCode = ""
                            },
                            label = { Text("No Picture") }
                        )
                        PictureQuizPreset.entries.forEach { preset ->
                            val isSelected = selectedPhotoPath.isBlank() && selectedPresetCode == preset.code
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedPhotoPath = ""
                                    selectedPresetCode = preset.code
                                    if (questionText.isBlank()) {
                                        questionText = "Look at the picture! Identify: ${preset.title}"
                                    }
                                },
                                label = {
                                    Text("${preset.visualArt.take(2)} ${preset.title}")
                                }
                            )
                        }
                    }

                    // Live Preview of Selected Photo or Flashcard
                    if (selectedPhotoPath.isNotBlank() || selectedPresetCode.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        QuestionPhotoOrFlashcardBanner(
                            photoUri = selectedPhotoPath,
                            picturePreset = PictureQuizPreset.fromCode(selectedPresetCode),
                            compact = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Syllabus Topic (${selectedClass.displayName}):",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                topicsForClass.forEach { topic ->
                    FilterChip(
                        selected = syllabusTopic == topic,
                        onClick = { syllabusTopic = topic },
                        label = { Text(topic, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            if (questionKind == QuestionKind.TRACING_AND_MCQ) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = tracingTarget,
                        onValueChange = { tracingTarget = it.take(5) },
                        label = { Text("Trace Target *") },
                        placeholder = { Text("|, A, 5, CAT") },
                        singleLine = true,
                        modifier = Modifier.weight(0.4f)
                    )
                    OutlinedTextField(
                        value = tracingHint,
                        onValueChange = { tracingHint = it },
                        label = { Text("Tracing Guide Hint") },
                        singleLine = true,
                        modifier = Modifier.weight(0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = questionText,
                onValueChange = {
                    questionText = it
                    if (it.isNotBlank()) errorText = false
                },
                label = { Text("Question Prompt *") },
                placeholder = { Text("e.g., Look at the photo and choose the correct answer:") },
                isError = errorText,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_question_text_input")
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Options (Tap A/B/C/D chip to mark Correct Option):",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = optionA,
                    onValueChange = { optionA = it },
                    label = { Text("Option A") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = optionB,
                    onValueChange = { optionB = it },
                    label = { Text("Option B") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = optionC,
                    onValueChange = { optionC = it },
                    label = { Text("Option C") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = optionD,
                    onValueChange = { optionD = it },
                    label = { Text("Option D") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Correct Answer:", style = MaterialTheme.typography.labelMedium)
                listOf("A", "B", "C", "D").forEachIndexed { idx, letter ->
                    FilterChip(
                        selected = correctOptionIndex == idx,
                        onClick = { correctOptionIndex = idx },
                        label = { Text("Option $letter") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        if (questionText.isBlank()) {
                            errorText = true
                        } else {
                            onSaveQuestion(
                                selectedClass,
                                syllabusTopic,
                                questionKind,
                                tracingTarget,
                                tracingHint,
                                questionText,
                                optionA.ifBlank { "Apple" },
                                optionB.ifBlank { "Ball" },
                                optionC.ifBlank { "Cat" },
                                optionD.ifBlank { "Fish" },
                                correctOptionIndex,
                                3,
                                selectedPhotoPath,
                                selectedPresetCode
                            )
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedClass.palette().accent
                    ),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(48.dp)
                        .testTag("save_syllabus_question_button")
                ) {
                    Text("Save Question", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
