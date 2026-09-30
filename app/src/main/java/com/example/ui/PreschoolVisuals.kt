package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.EntryCategory
import com.example.data.PreschoolClass
import com.example.ui.theme.HomeworkAmber
import com.example.ui.theme.HomeworkAmberContainer
import com.example.ui.theme.HomeworkAmberDark
import com.example.ui.theme.InstructionRose
import com.example.ui.theme.InstructionRoseContainer
import com.example.ui.theme.InstructionRoseDark
import com.example.ui.theme.LkgTeal
import com.example.ui.theme.LkgTealDark
import com.example.ui.theme.LkgTealLight
import com.example.ui.theme.NurseryCoral
import com.example.ui.theme.NurseryCoralDark
import com.example.ui.theme.NurseryCoralLight
import com.example.ui.theme.StripeBlue
import com.example.ui.theme.StripeGreen
import com.example.ui.theme.StripeOrange
import com.example.ui.theme.StripePurple
import com.example.ui.theme.StripeRed
import com.example.ui.theme.StripeYellow
import com.example.ui.theme.TaskMint
import com.example.ui.theme.TaskMintContainer
import com.example.ui.theme.TaskMintDark
import com.example.ui.theme.UkgIndigo
import com.example.ui.theme.UkgIndigoDark
import com.example.ui.theme.UkgIndigoLight
import kotlin.math.cos
import kotlin.math.sin

const val SCHOOL_PHONE = "9956359090"
const val SCHOOL_EMAIL = "rainbowpreschoolsupa@gmail.com"
const val SCHOOL_MOTTO = "“A Great Place For Quality Education”"

data class ClassPalette(
    val accent: Color,
    val softContainer: Color,
    val deepText: Color,
    val icon: ImageVector
)

fun PreschoolClass.palette(): ClassPalette = when (this) {
    PreschoolClass.NURSERY -> ClassPalette(
        accent = NurseryCoral,
        softContainer = NurseryCoralLight,
        deepText = NurseryCoralDark,
        icon = Icons.Default.ChildCare
    )
    PreschoolClass.LKG -> ClassPalette(
        accent = LkgTeal,
        softContainer = LkgTealLight,
        deepText = LkgTealDark,
        icon = Icons.Default.Palette
    )
    PreschoolClass.UKG -> ClassPalette(
        accent = UkgIndigo,
        softContainer = UkgIndigoLight,
        deepText = UkgIndigoDark,
        icon = Icons.Default.Stars
    )
}

data class CategoryPalette(
    val accent: Color,
    val container: Color,
    val onContainer: Color,
    val icon: ImageVector
)

fun EntryCategory.palette(): CategoryPalette = when (this) {
    EntryCategory.DAILY_TASK -> CategoryPalette(
        accent = TaskMint,
        container = TaskMintContainer,
        onContainer = TaskMintDark,
        icon = Icons.Default.AddTask
    )
    EntryCategory.HOMEWORK -> CategoryPalette(
        accent = HomeworkAmber,
        container = HomeworkAmberContainer,
        onContainer = HomeworkAmberDark,
        icon = Icons.Default.MenuBook
    )
    EntryCategory.INSTRUCTION -> CategoryPalette(
        accent = InstructionRose,
        container = InstructionRoseContainer,
        onContainer = InstructionRoseDark,
        icon = Icons.Default.Campaign
    )
}

/**
 * Renders the official circular Rainbow Pre-School Supa emblem with the golden-yellow ring,
 * maroon ornamental dots, rainbow arch, multicolor "Rainbow" typography, and "Pre - School Supa".
 */
@Composable
fun RainbowSchoolLogoBadge(
    size: Dp = 76.dp,
    modifier: Modifier = Modifier
) {
    val goldRing = Color(0xFFF6C116)
    val maroonDot = Color(0xFF7B1818)
    val royalBlue = Color(0xFF21357E)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(goldRing)
            .border(1.5.dp, Color(0xFFD9A404), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = this.size.minDimension / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Inner white circle
            drawCircle(
                color = Color.White,
                radius = radius * 0.74f,
                center = center
            )

            // Top ornamental maroon dots & side dots on the golden ring
            val ringRadius = radius * 0.87f
            val leftAngles = listOf(-125f, -142f, -158f, -174f, 170f)
            val rightAngles = listOf(-55f, -38f, -22f, -6f, 10f)
            (leftAngles + rightAngles).forEach { deg ->
                val rad = Math.toRadians(deg.toDouble())
                val dotCenter = Offset(
                    x = center.x + (ringRadius * cos(rad)).toFloat(),
                    y = center.y + (ringRadius * sin(rad)).toFloat()
                )
                drawCircle(
                    color = maroonDot,
                    radius = radius * 0.038f,
                    center = dotCenter
                )
            }

            // Top flourish crown
            drawCircle(
                color = maroonDot,
                radius = radius * 0.045f,
                center = Offset(center.x, center.y - ringRadius)
            )

            // Rainbow Arch in the upper white circle
            val archColors = listOf(
                Color(0xFF7B1FA2),
                Color(0xFF1565C0),
                Color(0xFF2E7D32),
                Color(0xFFFBC02D),
                Color(0xFFEF6C00),
                Color(0xFFD32F2F)
            )
            val strokeW = radius * 0.025f
            archColors.forEachIndexed { idx, arcColor ->
                val inset = idx * strokeW * 1.05f
                val arcRadius = (radius * 0.36f) - inset
                drawArc(
                    color = arcColor,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(center.x - arcRadius, center.y - radius * 0.50f + inset),
                    size = Size(arcRadius * 2f, arcRadius * 1.45f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = size * 0.08f)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF851E78))) { append("R") }
                    withStyle(SpanStyle(color = Color(0xFFD9232A))) { append("a") }
                    withStyle(SpanStyle(color = Color(0xFFEA7624))) { append("i") }
                    withStyle(SpanStyle(color = Color(0xFF0E8342))) { append("n") }
                    withStyle(SpanStyle(color = Color(0xFF21357E))) { append("b") }
                    withStyle(SpanStyle(color = Color(0xFFD81B68))) { append("o") }
                    withStyle(SpanStyle(color = Color(0xFF6A6D3C))) { append("w") }
                },
                fontSize = (size.value * 0.17f).sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = (size.value * 0.18f).sp
            )
            Text(
                text = "Pre - School",
                color = royalBlue,
                fontSize = (size.value * 0.095f).sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (size.value * 0.10f).sp
            )
            Text(
                text = "Supa",
                color = royalBlue,
                fontSize = (size.value * 0.105f).sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = (size.value * 0.11f).sp
            )
        }
    }
}

@Composable
fun RainbowRibbonBar(modifier: Modifier = Modifier) {
    val colors = listOf(
        StripeRed,
        StripeOrange,
        StripeYellow,
        StripeGreen,
        StripeBlue,
        StripePurple
    )
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
    ) {
        val segmentWidth = size.width / colors.size
        colors.forEachIndexed { index, color ->
            drawLine(
                color = color,
                start = Offset(x = index * segmentWidth, y = size.height / 2f),
                end = Offset(x = (index + 1) * segmentWidth, y = size.height / 2f),
                strokeWidth = size.height,
                cap = StrokeCap.Butt
            )
        }
    }
}

@Composable
fun PersistentSchoolTopHeader(
    isTeacherMode: Boolean = true,
    lockedClass: PreschoolClass? = null,
    onOpenClassPortalChooser: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("persistent_school_top_header"),
        color = Color(0xFF1A2542),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RainbowSchoolLogoBadge(
                    size = 50.dp,
                    modifier = Modifier.testTag("school_logo_badge")
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Rainbow Preschool Supa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("school_header_name")
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    runCatching {
                                        context.startActivity(
                                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$SCHOOL_PHONE"))
                                        )
                                    }
                                }
                                .padding(vertical = 2.dp)
                                .testTag("school_phone_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call school",
                                tint = Color(0xFFF6C116),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = SCHOOL_PHONE,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }

                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.45f)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    runCatching {
                                        context.startActivity(
                                            Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$SCHOOL_EMAIL"))
                                        )
                                    }
                                }
                                .padding(vertical = 2.dp)
                                .testTag("school_email_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email school",
                                tint = Color(0xFFF6C116),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = SCHOOL_EMAIL,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.92f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFFF6C116).copy(alpha = 0.55f)),
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { onOpenClassPortalChooser() }
                        .testTag("header_portal_access_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (lockedClass != null) {
                                Icons.Default.Lock
                            } else if (isTeacherMode) {
                                Icons.Default.VerifiedUser
                            } else {
                                Icons.Default.School
                            },
                            contentDescription = "Portal Login & Access Settings",
                            tint = Color(0xFFF6C116),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            RainbowRibbonBar()
        }
    }
}

@Composable
fun PreschoolHeroBanner(
    isTeacherMode: Boolean,
    lockedClass: PreschoolClass?,
    onToggleRoleMode: (Boolean) -> Unit,
    onOpenClassPortalChooser: () -> Unit,
    onOpenMultiDeviceHub: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    PersistentSchoolTopHeader(
        isTeacherMode = isTeacherMode,
        lockedClass = lockedClass,
        onOpenClassPortalChooser = onOpenClassPortalChooser,
        modifier = modifier
    )
}

@Composable
fun ClassAccessSelectorRow(
    selectedClass: PreschoolClass,
    lockedClass: PreschoolClass?,
    statsMap: Map<PreschoolClass, ClassStats>,
    unlockedClasses: Set<PreschoolClass> = PreschoolClass.entries.toSet(),
    isTeacherAdmin: Boolean = true,
    onSelectClass: (PreschoolClass) -> Unit,
    onOpenAccessSheet: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val classesToShow = if (!isTeacherAdmin && lockedClass != null) {
        listOf(lockedClass)
    } else {
        PreschoolClass.entries
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        classesToShow.forEach { cls ->
            val isSelected = cls == selectedClass
            val isClassUnlocked = isTeacherAdmin || unlockedClasses.contains(cls)
            val palette = cls.palette()

            val containerColor by animateColorAsState(
                targetValue = if (isSelected) palette.accent else MaterialTheme.colorScheme.surface,
                label = "classCardBg"
            )
            val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelectClass(cls) }
                    .testTag("class_card_${cls.code}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) palette.accent else palette.accent.copy(alpha = 0.35f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 4.dp else 1.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isClassUnlocked) palette.icon else Icons.Default.Lock,
                        contentDescription = cls.displayName,
                        tint = if (isSelected) Color.White else palette.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = cls.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            maxLines = 1
                        )
                        Text(
                            text = if (isClassUnlocked) cls.ageGroup else "Locked",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) {
                                Color.White.copy(alpha = 0.88f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClassOverviewAndQuickActionsCard(
    selectedClass: PreschoolClass,
    stats: ClassStats,
    isTeacherMode: Boolean,
    onQuickAddCategory: (EntryCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isTeacherMode) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, selectedClass.palette().accent.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EntryCategory.entries.forEach { category ->
                val catPalette = category.palette()
                FilledTonalButton(
                    onClick = { onQuickAddCategory(category) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_add_${category.code}"),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = catPalette.icon,
                        contentDescription = category.label,
                        tint = catPalette.accent,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+ ${category.label}",
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
