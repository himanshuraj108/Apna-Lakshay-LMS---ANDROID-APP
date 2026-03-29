package com.example.lms_android.ui.attendance

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lms_android.data.models.AttendanceRecord
import com.example.lms_android.data.models.AttendanceRanking
import com.example.lms_android.data.models.AttendanceResponse
import com.example.lms_android.ui.home.bgDark
import com.example.lms_android.ui.home.borderDark
import com.example.lms_android.ui.home.colorTextSecondary
import java.text.SimpleDateFormat
import java.util.*

// ─── Color Palette ────────────────────────────────────────────────────────────
private val colorPurple      = Color(0xFF7C3AED)
private val colorPurpleLight = Color(0xFFA78BFA)
private val colorGreen       = Color(0xFF10B981)
private val colorRed         = Color(0xFFF87171)
private val colorOrange      = Color(0xFFE87A5D)
private val colorGold        = Color(0xFFFBBF24)
private val cardBg           = Color(0xFF13151D)
private val cardBgDarker     = Color(0xFF0F1117)

// ─────────────────────────────────────────────────────────────────────────────
//  ROOT SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AttendanceScreen(
    onNavigateBack: () -> Unit,
    viewModel: AttendanceViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(containerColor = bgDark) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val s = state) {
                is AttendanceState.Loading -> {
                    AttendanceTopBar(onBack = onNavigateBack)
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorPurple)
                    }
                }
                is AttendanceState.Error -> {
                    AttendanceTopBar(onBack = onNavigateBack)
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null,
                                tint = colorRed, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text(s.message, color = colorTextSecondary, fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp))
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.fetchAttendance() },
                                colors = ButtonDefaults.buttonColors(containerColor = colorPurple)
                            ) {
                                Text("Retry", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                is AttendanceState.Success -> {
                    AttendanceContent(data = s.data, onBack = onNavigateBack)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  MAIN CONTENT  (all sections in one scrollable column)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AttendanceContent(data: AttendanceResponse, onBack: () -> Unit) {
    val summary  = data.summary
    val records  = (data.myAttendance ?: emptyList()).sortedByDescending { it.date }
    val rankings = data.rankings ?: emptyList()

    val totalMins  = records.filter { it.status == "present" }.sumOf { it.duration ?: 0 }
    val studyHours = totalMins / 60
    val studyMins  = totalMins % 60

    val percentage = summary?.percentage ?: 0
    val present    = summary?.present    ?: 0
    val total      = summary?.total      ?: 0
    val targetPct  = 76

    // state for "show all" daily log toggle
    var showAllLogs by remember { mutableStateOf(false) }
    val displayedRecords = if (showAllLogs) records else records.take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top Bar ──────────────────────────────────────────────────────────
        AttendanceTopBar(onBack = onBack)

        // ── Tab Switcher (mark attendance button) ─────────────────────────────
        AttendanceMarkButton()

        Spacer(Modifier.height(20.dp))

        // ── Stats Grid ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AttendanceStatCard(
                modifier    = Modifier.weight(1f),
                icon        = Icons.Default.CalendarMonth,
                iconTint    = colorTextSecondary,
                iconBg      = Color(0xFF1C1E2A),
                accentColor = colorGreen,
                label       = "DAYS LOGGED",
                value       = "$total"
            )
            AttendanceStatCard(
                modifier    = Modifier.weight(1f),
                icon        = Icons.Default.CheckCircle,
                iconTint    = colorGreen,
                iconBg      = Color(0xFF0E2E20),
                accentColor = colorGreen,
                label       = "PRESENT",
                value       = "$present"
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AttendanceStatCard(
                modifier    = Modifier.weight(1f),
                icon        = Icons.Default.TrendingUp,
                iconTint    = colorRed,
                iconBg      = Color(0xFF2A1010),
                accentColor = colorRed,
                label       = "ATTENDANCE",
                value       = "$percentage%"
            )
            AttendanceStatCard(
                modifier    = Modifier.weight(1f),
                icon        = Icons.Default.HourglassTop,
                iconTint    = colorPurpleLight,
                iconBg      = Color(0xFF1E1530),
                accentColor = colorPurpleLight,
                label       = "STUDY HOURS",
                value       = "${studyHours}h ${studyMins}m"
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Progress Bar ──────────────────────────────────────────────────────
        AttendanceProgressCard(percentage = percentage, targetPct = targetPct)

        Spacer(Modifier.height(20.dp))

        // ── Daily Log ─────────────────────────────────────────────────────────
        DailyLogSection(
            records         = displayedRecords,
            totalCount      = records.size,
            showingAll      = showAllLogs,
            onToggleShowAll = { showAllLogs = !showAllLogs }
        )

        Spacer(Modifier.height(20.dp))

        // ── Rankings ──────────────────────────────────────────────────────────
        RankingsSection(rankings = rankings)

        Spacer(Modifier.height(40.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  TOP BAR
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AttendanceTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, borderDark, RoundedCornerShape(12.dp))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text("Attendance", color = Color.White, fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold)
            Text("Your study presence tracker", color = colorTextSecondary, fontSize = 13.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  MARK ATTENDANCE BUTTON (replaces tab switcher since rankings are always visible)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AttendanceMarkButton() {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF6D28D9), Color(0xFF4F46E5))))
                .clickable { /* TODO: implement mark attendance */ },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Mark Attendance", color = Color.White,
                    fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(1.dp, borderDark, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BarChart, contentDescription = null,
                    tint = colorTextSecondary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Analytics", color = colorTextSecondary,
                    fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  STAT CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AttendanceStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    accentColor: Color,
    label: String,
    value: String
) {
    Box(
        modifier = modifier
            .border(1.dp, borderDark, RoundedCornerShape(16.dp))
            .background(cardBg, RoundedCornerShape(16.dp))
    ) {
        // Accent top line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(accentColor)
        )
        // Watermark icon
        Icon(
            icon, contentDescription = null,
            tint = accentColor.copy(alpha = 0.07f),
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 10.dp, y = 10.dp)
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = iconTint,
                    modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(label, color = colorTextSecondary, fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  PROGRESS CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AttendanceProgressCard(percentage: Int, targetPct: Int) {
    val isBelowTarget = percentage < targetPct
    val animatedProgress by animateFloatAsState(
        targetValue = (percentage / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "progressAnim"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(1.dp, borderDark, RoundedCornerShape(16.dp))
            .background(cardBg, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bolt, contentDescription = null,
                        tint = colorOrange, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Attendance Progress", color = Color.White,
                        fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (isBelowTarget) colorGold.copy(alpha = 0.4f) else colorGreen.copy(alpha = 0.4f),
                                RoundedCornerShape(20.dp)
                            )
                            .background(
                                if (isBelowTarget) Color(0xFF78350F).copy(alpha = 0.3f) else Color(0xFF064E3B).copy(alpha = 0.3f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            if (isBelowTarget) "Below target" else "On target",
                            color = if (isBelowTarget) colorGold else colorGreen,
                            fontSize = 11.sp, fontWeight = FontWeight.Bold
                        )
                    }
                    Text("$percentage%", color = colorOrange, fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(colorRed, colorOrange)))
                )
                val targetFraction = targetPct / 100f
                Box(modifier = Modifier.fillMaxWidth(targetFraction).fillMaxHeight()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0%", color = colorTextSecondary, fontSize = 11.sp)
                Text("Target: $targetPct%", color = colorTextSecondary, fontSize = 11.sp)
                Text("100%", color = colorTextSecondary, fontSize = 11.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  DAILY LOG SECTION
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DailyLogSection(
    records: List<AttendanceRecord>,
    totalCount: Int,
    showingAll: Boolean,
    onToggleShowAll: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(1.dp, borderDark, RoundedCornerShape(20.dp))
            .background(cardBg, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF1A2E1A), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null,
                        tint = colorGreen, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Daily Log", color = Color.White, fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold)
            }

            Spacer(Modifier.height(16.dp))

            if (records.isEmpty() && totalCount == 0) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null,
                            tint = colorTextSecondary, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No attendance records found",
                            color = colorTextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                records.forEachIndexed { index, record ->
                    DailyLogItem(record = record)
                    if (index < records.lastIndex) {
                        HorizontalDivider(
                            color = borderDark,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                // Show More / Show Less button
                if (totalCount > 5) {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, borderDark, RoundedCornerShape(12.dp))
                            .background(cardBgDarker)
                            .clickable { onToggleShowAll() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                if (showingAll) "Show Less" else "Show ${totalCount - 5} More",
                                color = colorPurpleLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                if (showingAll) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = colorPurpleLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  DAILY LOG ITEM  — fixed layout (chips go below date, not beside it)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DailyLogItem(record: AttendanceRecord) {
    val isPresent       = record.status == "present" || record.status == "holiday"
    val isActiveSession = record.isActive == true

    val displayDate = remember(record.date) {
        try {
            val inputFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
            val outFmt = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
            val date = inputFmt.parse(record.date ?: "") ?: return@remember record.date ?: ""
            outFmt.format(date)
        } catch (e: Exception) {
            record.date ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        // ── Row 1: icon  |  date + status pill  ─────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (isPresent) Color(0xFF064E3B).copy(alpha = 0.4f)
                        else Color(0xFF7F1D1D).copy(alpha = 0.3f),
                        CircleShape
                    )
                    .border(
                        1.dp,
                        if (isPresent) colorGreen.copy(alpha = 0.4f) else colorRed.copy(alpha = 0.4f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPresent) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isPresent) colorGreen else colorRed,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // Date + status pill
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    displayDate,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(
                            if (isPresent) Color(0xFF064E3B).copy(alpha = 0.3f)
                            else Color(0xFF7F1D1D).copy(alpha = 0.2f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = when (record.status) {
                            "present" -> "PRESENT"
                            "holiday" -> "HOLIDAY"
                            else      -> "ABSENT"
                        },
                        color = if (isPresent) colorGreen else colorRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // ── Row 2: time chips (only when present) ───────────────────────────
        if (isPresent) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp), // align under the date text
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AttendanceTimeChip(
                    icon  = Icons.Default.Login,
                    label = "ENTRY",
                    value = record.entryTime ?: "--:--"
                )
                AttendanceTimeChip(
                    icon  = Icons.Default.Logout,
                    label = "EXIT",
                    value = if (isActiveSession) "--:--" else (record.exitTime ?: "--:--")
                )
                AttendanceDurationChip(duration = record.duration, isActive = isActiveSession)
            }

            // Animated bar for active sessions
            if (isActiveSession) {
                Spacer(Modifier.height(10.dp))
                val infiniteTransition = rememberInfiniteTransition(label = "pulseAnim")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.5f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        tween(900, easing = EaseInOut), RepeatMode.Reverse
                    ),
                    label = "pulseAlpha"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(colorPurple.copy(alpha = alpha),
                                    Color(0xFF4F46E5).copy(alpha = alpha))
                            )
                        )
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  CHIP COMPONENTS
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AttendanceTimeChip(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .border(1.dp, borderDark, RoundedCornerShape(8.dp))
            .background(cardBgDarker, RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = colorTextSecondary,
            modifier = Modifier.size(10.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, color = colorTextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(4.dp))
        Text(value, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun AttendanceDurationChip(duration: Int?, isActive: Boolean) {
    val durationText = when {
        isActive                          -> "-"
        duration == null || duration == 0 -> "-"
        else -> {
            val h = duration / 60
            val m = duration % 60
            if (h > 0) "${h}h ${m}m" else "${m}m"
        }
    }
    Row(
        modifier = Modifier
            .border(1.dp, borderDark, RoundedCornerShape(8.dp))
            .background(cardBgDarker, RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = colorPurpleLight,
            modifier = Modifier.size(10.dp))
        Spacer(Modifier.width(4.dp))
        Text("DURATION", color = colorTextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(4.dp))
        Text(durationText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  RANKINGS SECTION
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RankingsSection(rankings: List<AttendanceRanking>) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(1.dp, borderDark, RoundedCornerShape(20.dp))
            .background(cardBg, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF32240A), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null,
                        tint = colorGold, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Attendance Rankings", color = Color.White, fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold)
            }

            Spacer(Modifier.height(16.dp))

            if (rankings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No ranking data available",
                        color = colorTextSecondary, fontSize = 14.sp)
                }
            } else {
                // Column Headers
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("RANK", color = colorTextSecondary, fontSize = 11.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp,
                        modifier = Modifier.width(52.dp))
                    Text("STUDENT", color = colorTextSecondary, fontSize = 11.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp,
                        modifier = Modifier.weight(1f))
                    Text("%", color = colorTextSecondary, fontSize = 11.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
                HorizontalDivider(color = borderDark, thickness = 1.dp)
                Spacer(Modifier.height(4.dp))

                rankings.forEach { ranking ->
                    RankingRow(ranking = ranking)
                    if (ranking != rankings.last()) {
                        HorizontalDivider(
                            color = borderDark.copy(alpha = 0.5f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  RANKING ROW
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RankingRow(ranking: AttendanceRanking) {
    val rankColor = when (ranking.rank) {
        1    -> colorGold
        2    -> Color(0xFFB0C4DE)   // Silver
        3    -> Color(0xFFCD7F32)   // Bronze
        else -> colorTextSecondary
    }
    val pctColor = when {
        ranking.percentage >= 76 -> colorGreen
        ranking.percentage >= 50 -> colorGold
        else                     -> colorOrange
    }
    val rowBg = if (ranking.isMe) Color(0xFF1A1030) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg, RoundedCornerShape(10.dp))
            .padding(vertical = 12.dp,
                horizontal = if (ranking.isMe) 10.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank badge
        Box(modifier = Modifier.width(52.dp), contentAlignment = Alignment.CenterStart) {
            if (ranking.rank <= 3) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(rankColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, rankColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null,
                        tint = rankColor, modifier = Modifier.size(14.dp))
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${ranking.rank}", color = colorTextSecondary,
                        fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Name + "You" pill
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = (ranking.name ?: "Unknown").uppercase(),
                color = if (ranking.isMe) Color.White else Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = if (ranking.isMe) FontWeight.ExtraBold else FontWeight.Medium,
                letterSpacing = 0.3.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (ranking.isMe) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(colorPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .border(1.dp, colorPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("You", color = colorPurpleLight, fontSize = 10.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        // Percentage
        Text("${ranking.percentage}%", color = pctColor, fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold)
    }
}
