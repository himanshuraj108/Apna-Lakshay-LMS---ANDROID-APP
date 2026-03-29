package com.example.lms_android.ui.attendance

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lms_android.data.models.AttendanceRecord
import com.example.lms_android.ui.home.borderDark
import com.example.lms_android.ui.home.colorTextSecondary
import java.text.SimpleDateFormat
import java.util.*

private val analyticsCardBg   = Color(0xFF13151D)
private val analyticsAccent   = Color(0xFF7C3AED)
private val analyticsGreen    = Color(0xFF10B981)
private val analyticsRed      = Color(0xFFF87171)
private val analyticsGold     = Color(0xFFFBBF24)

/**
 * Analytics tab content – shows a monthly bar chart + streak stats.
 * Receives the full list of [AttendanceRecord]s already loaded.
 */
@Composable
fun AnalyticsContent(records: List<AttendanceRecord>) {

    // Build day-indexed data for current month
    val monthData = remember(records) { buildMonthData(records) }
    val longestStreak  = remember(records) { computeStreak(records, longest = true) }
    val currentStreak  = remember(records) { computeStreak(records, longest = false) }
    val presentCount   = records.count { it.status == "present" || it.status == "holiday" }
    val totalStudyMins = records.filter { it.status == "present" }.sumOf { it.duration ?: 0 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(4.dp))

        // ── Monthly Trends Bar Chart ──────────────────────────────────────────
        MonthlyTrendsCard(monthData = monthData)

        Spacer(Modifier.height(16.dp))

        // ── Streak & Summary Cards ────────────────────────────────────────────
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnalyticsMiniCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalFireDepartment,
                iconTint = Color(0xFFE87A5D),
                iconBg = Color(0xFF2A1505),
                label = "CURRENT STREAK",
                value = "${currentStreak}d"
            )
            AnalyticsMiniCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.EmojiEvents,
                iconTint = analyticsGold,
                iconBg = Color(0xFF32240A),
                label = "BEST STREAK",
                value = "${longestStreak}d"
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnalyticsMiniCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CheckCircle,
                iconTint = analyticsGreen,
                iconBg = Color(0xFF0E2E20),
                label = "DAYS PRESENT",
                value = "$presentCount"
            )
            AnalyticsMiniCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Schedule,
                iconTint = Color(0xFFA78BFA),
                iconBg = Color(0xFF1E1530),
                label = "STUDY TIME",
                value = "${totalStudyMins / 60}h ${totalStudyMins % 60}m"
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Weekly Summary ────────────────────────────────────────────────────
        WeeklySummaryCard(records = records)

        Spacer(Modifier.height(24.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Monthly Trends Bar Chart
// ─────────────────────────────────────────────────────────────────────────────
data class DayEntry(val day: Int, val status: String, val durationMins: Int)

private fun buildMonthData(records: List<AttendanceRecord>): List<DayEntry> {
    val now = Calendar.getInstance()
    val daysInMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH)

    val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    // Build map: day-of-month → record
    val dayMap = mutableMapOf<Int, AttendanceRecord>()
    records.forEach { r ->
        try {
            val date = fmt.parse(r.date ?: "") ?: return@forEach
            val cal = Calendar.getInstance()
            cal.time = date
            if (cal.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
            ) {
                dayMap[cal.get(Calendar.DAY_OF_MONTH)] = r
            }
        } catch (_: Exception) {}
    }

    val today = now.get(Calendar.DAY_OF_MONTH)
    return (1..daysInMonth).map { day ->
        val rec = dayMap[day]
        DayEntry(
            day = day,
            status = when {
                day > today -> "future"
                rec != null  -> rec.status ?: "absent"
                else         -> "absent"
            },
            durationMins = rec?.duration ?: 0
        )
    }
}

@Composable
private fun MonthlyTrendsCard(monthData: List<DayEntry>) {
    val maxDuration = monthData.maxOfOrNull { it.durationMins }?.takeIf { it > 0 } ?: 480 // 8h default max
    val scrollState = rememberScrollState()

    // Animate bars
    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "barAnim"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(1.dp, borderDark, RoundedCornerShape(20.dp))
            .background(analyticsCardBg, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF1A1030), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null,
                        tint = analyticsAccent, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Monthly Trends", color = Color.White, fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.weight(1f))
                val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())
                Text(monthName, color = colorTextSecondary, fontSize = 12.sp,
                    fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(24.dp))

            // Bar chart — horizontally scrollable
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState)) {
                monthData.forEach { entry ->
                    BarColumn(
                        entry = entry,
                        maxDuration = maxDuration,
                        animProgress = animProgress
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendDot(color = analyticsGreen, label = "Present")
                LegendDot(color = analyticsRed, label = "Absent")
                LegendDot(color = Color(0xFF374151), label = "Future")
            }
        }
    }
}

@Composable
private fun BarColumn(entry: DayEntry, maxDuration: Int, animProgress: Float) {
    val isToday = entry.day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    val barColor = when (entry.status) {
        "present", "holiday" -> analyticsGreen
        "future"             -> Color(0xFF1F2937)
        else                 -> analyticsRed
    }
    val barHeightFraction = when {
        entry.status == "future"             -> 0.05f
        entry.status == "absent"             -> 0.08f
        entry.durationMins <= 0              -> 0.15f
        else                                 -> (entry.durationMins.toFloat() / maxDuration).coerceIn(0.1f, 1f)
    } * animProgress

    val maxBarHeight = 100.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(20.dp)
    ) {
        // Bar
        Box(
            modifier = Modifier.height(maxBarHeight),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .fillMaxHeight(barHeightFraction)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(
                        if (entry.status == "present" || entry.status == "holiday")
                            Brush.verticalGradient(listOf(analyticsGreen, analyticsGreen.copy(alpha = 0.5f)))
                        else
                            Brush.verticalGradient(listOf(barColor, barColor.copy(alpha = 0.7f)))
                    )
            )
        }

        Spacer(Modifier.height(4.dp))

        // Day number
        Text(
            "${entry.day}",
            color = if (isToday) analyticsAccent else colorTextSecondary,
            fontSize = if (isToday) 9.sp else 8.sp,
            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal
        )

        // Today indicator
        if (isToday) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(analyticsAccent, RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(4.dp))
        Text(label, color = colorTextSecondary, fontSize = 11.sp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Mini Analytics Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AnalyticsMiniCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    value: String
) {
    Box(
        modifier = modifier
            .border(1.dp, borderDark, RoundedCornerShape(16.dp))
            .background(analyticsCardBg, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint,
                    modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, color = colorTextSecondary, fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                Spacer(Modifier.height(2.dp))
                Text(value, color = Color.White, fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Weekly Summary Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun WeeklySummaryCard(records: List<AttendanceRecord>) {
    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    // Build week heat map (last 4 weeks, 7 days each)
    val today = Calendar.getInstance()
    val startOfWeek = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        add(Calendar.WEEK_OF_YEAR, -3)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }

    val dateStatusMap = mutableMapOf<String, String>()
    records.forEach { r ->
        try {
            val date = fmt.parse(r.date ?: "") ?: return@forEach
            val key = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
            dateStatusMap[key] = r.status ?: "absent"
        } catch (_: Exception) {}
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(1.dp, borderDark, RoundedCornerShape(20.dp))
            .background(analyticsCardBg, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF0E2E20), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.GridView, contentDescription = null,
                        tint = analyticsGreen, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("4-Week Heatmap", color = Color.White, fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold)
            }

            Spacer(Modifier.height(20.dp))

            // Header row: day names
            Row(modifier = Modifier.fillMaxWidth()) {
                dayNames.forEach { dn ->
                    Text(dn, color = colorTextSecondary, fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // 4 weeks of boxes
            val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cur = startOfWeek.clone() as Calendar
            repeat(4) { // 4 weeks
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) { // 7 days
                        val dateKey = dateFmt.format(cur.time)
                        val isFuture = cur.after(today)
                        val status = if (isFuture) "future" else dateStatusMap[dateKey]

                        val boxColor = when (status) {
                            "present", "holiday" -> analyticsGreen.copy(alpha = 0.8f)
                            "future"             -> Color.Transparent
                            else                 -> Color(0xFF1F2937)
                        }
                        val borderColor = when (status) {
                            "present", "holiday" -> analyticsGreen.copy(alpha = 0.3f)
                            "future"             -> Color.White.copy(alpha = 0.04f)
                            else                 -> Color.White.copy(alpha = 0.06f)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(boxColor)
                                .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                        )
                        cur.add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Streak Calculator
// ─────────────────────────────────────────────────────────────────────────────
private fun computeStreak(records: List<AttendanceRecord>, longest: Boolean): Int {
    val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val presentDays = records
        .filter { it.status == "present" || it.status == "holiday" }
        .mapNotNull { r ->
            try { dateFmt.format(fmt.parse(r.date ?: "")!!) } catch (_: Exception) { null }
        }
        .toSortedSet()
        .toList()

    if (presentDays.isEmpty()) return 0

    var maxStreak = 0
    var curStreak = 0
    val cal = Calendar.getInstance()

    for (i in presentDays.indices) {
        if (i == 0) {
            curStreak = 1
        } else {
            val prev = dateFmt.parse(presentDays[i - 1])!!
            val curr = dateFmt.parse(presentDays[i])!!
            cal.time = prev
            cal.add(Calendar.DAY_OF_YEAR, 1)
            if (dateFmt.format(cal.time) == presentDays[i]) {
                curStreak++
            } else {
                curStreak = 1
            }
        }
        if (curStreak > maxStreak) maxStreak = curStreak
    }

    return if (longest) maxStreak else curStreak
}
