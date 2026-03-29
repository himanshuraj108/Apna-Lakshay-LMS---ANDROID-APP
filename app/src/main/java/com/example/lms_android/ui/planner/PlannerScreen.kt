package com.example.lms_android.ui.planner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lms_android.data.models.CreateExamRequest
import com.example.lms_android.data.models.CreateTaskRequest
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// Theme colors
private val BackgroundColor = Color(0xFF0F0C1B)
private val CardBackground = Color(0xFF161320)
private val PrimaryOrange = Color(0xFFE05B43)
private val PrimaryPurple = Color(0xFF6B58C6)
private val BorderColor = Color(0xFF2A2735)

enum class PlannerTab { Tasks, Calendar, Analytics }

@Composable
fun PlannerScreen(
    onNavigateBack: () -> Unit,
    viewModel: PlannerViewModel = viewModel()
) {
    val state by viewModel.plannerState.collectAsState()
    var selectedTab by remember { mutableStateOf(PlannerTab.Tasks) }

    when (state) {
        is PlannerState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(BackgroundColor), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
        }
        is PlannerState.Error -> {
            Box(modifier = Modifier.fillMaxSize().background(BackgroundColor), contentAlignment = Alignment.Center) {
                Text((state as PlannerState.Error).message, color = Color.Red)
            }
        }
        is PlannerState.Success -> {
            val plannerData = state as PlannerState.Success

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(horizontal = 16.dp)
                    .statusBarsPadding()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigateBack() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back", color = Color.White, fontSize = 14.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "Study Base",
                            color = PrimaryOrange,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Row {
                            Text("Level ", color = Color.Gray, fontSize = 12.sp)
                            Text("${plannerData.stats.level}", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(" · ${plannerData.stats.totalXP} XP", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TabButton("Tasks", Icons.Default.FormatListBulleted, selectedTab == PlannerTab.Tasks, { selectedTab = PlannerTab.Tasks }, Modifier.weight(1f))
                    TabButton("Calendar", Icons.Default.CalendarToday, selectedTab == PlannerTab.Calendar, { selectedTab = PlannerTab.Calendar }, Modifier.weight(1f))
                    TabButton("Analytics", Icons.Default.BarChart, selectedTab == PlannerTab.Analytics, { selectedTab = PlannerTab.Analytics }, Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // XP Progress
                val xpInCurrentLevel = plannerData.stats.totalXP % 1000
                val progressRatio = xpInCurrentLevel / 1000f
                Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(BorderColor, CircleShape)) {
                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progressRatio).background(PrimaryPurple, CircleShape))
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("$xpInCurrentLevel XP", color = Color.Gray, fontSize = 10.sp)
                    Text("Next level: 1000 XP", color = Color.Gray, fontSize = 10.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedTab) {
                        PlannerTab.Tasks -> TasksSection(plannerData, viewModel)
                        PlannerTab.Calendar -> CalendarSection(plannerData)
                        PlannerTab.Analytics -> AnalyticsSection(plannerData)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun TabButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor = if (isSelected) PrimaryPurple else Color.Transparent
    val contentColor = if (isSelected) Color.White else Color.Gray
    Box(
        modifier = modifier.clip(RoundedCornerShape(10.dp)).background(bgColor).clickable { onClick() }.padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, color = contentColor, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun TasksSection(data: PlannerState.Success, viewModel: PlannerViewModel) {
    var showNewTask by remember { mutableStateOf(false) }
    var showAddExam by remember { mutableStateOf(false) }

    if (showNewTask) {
        NewTaskDialog(
            onDismiss = { showNewTask = false },
            onSubmit = { t, d, p, e, date ->
                viewModel.createTask(CreateTaskRequest(t, d, p, e, date))
                showNewTask = false
            }
        )
    }

    if (showAddExam) {
        AddExamDialog(
            onDismiss = { showAddExam = false },
            onSubmit = { t, d, s, c ->
                viewModel.createExam(CreateExamRequest(t, d, s, c))
                showAddExam = false
            }
        )
    }

    // New Task Button
    Button(
        onClick = { showNewTask = true },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("NEW TASK", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
    }
    
    Spacer(Modifier.height(16.dp))
    
    // Metrics
    val pending = data.tasks.count { !it.completed }
    val completed = data.tasks.count { it.completed }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        MetricBox(title = "PENDING", value = "$pending", modifier = Modifier.weight(1f), titleColor = Color(0xFF60A5FA))
        MetricBox(title = "COMPLETED", value = "$completed", modifier = Modifier.weight(1f), titleColor = Color(0xFF34D399))
    }
    
    Spacer(Modifier.height(16.dp))
    
    // Tasks List or Empty State
    if (data.tasks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp)).padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text("No active tasks. Time to plan your success!", color = Color.Gray, fontSize = 12.sp)
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.tasks.take(3).forEach { task ->
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(CardBackground).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(task.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        if (task.dueDate != null) Text("Due: ${task.dueDate}", color = Color.Gray, fontSize = 11.sp)
                    }
                    Checkbox(
                        checked = task.completed,
                        onCheckedChange = { v -> viewModel.toggleTaskCompletion(task._id, v) },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryPurple, uncheckedColor = Color.Gray)
                    )
                }
            }
            if (data.tasks.size > 3) {
                Text("View all tasks...", color = PrimaryPurple, fontSize = 12.sp, modifier = Modifier.clickable { })
            }
        }
    }

    Spacer(Modifier.height(16.dp))
    
    // Exam Countdown
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CardBackground).padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Exam Countdown", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(BorderColor).clickable { showAddExam = true }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.height(16.dp))

            if(data.exams.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(8.dp)).padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.border(1.dp, BorderColor, RoundedCornerShape(8.dp)).background(Color(0xFF1E2638)).clickable{ showAddExam = true }.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Add your first exam", color = Color(0xFF60A5FA), fontSize = 10.sp)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("No upcoming exams.", color = Color.DarkGray, fontSize = 12.sp)
                    }
                }
            } else {
                val exam = data.exams.first() // show closest
                Box(modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(8.dp)).padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(exam.title, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(exam.date, color = Color.Gray, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Gray, modifier = Modifier.clickable { viewModel.deleteExam(exam._id) })
                    }
                }
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    // Pomodoro Feature
    PomodoroWidget(viewModel)
    
    Spacer(Modifier.height(16.dp))
    
    // Pro Tip
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(BackgroundColor).border(1.dp, BorderColor, RoundedCornerShape(12.dp)).padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFCD34D), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Pro Tip", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text("\"The Pomodoro Technique optimizes your focus by breaking work into productive intervals separated by short breaks — training your brain to stay fresh and agile.\"", color = Color.Gray, fontSize = 12.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
fun PomodoroWidget(viewModel: PlannerViewModel) {
    var timerMode by remember { mutableStateOf(0) } // 0: Focus, 1: Short, 2: Long
    var settingsFocus by remember { mutableStateOf(25) }
    var settingsShort by remember { mutableStateOf(5) }
    var settingsLong by remember { mutableStateOf(15) }
    var showSettings by remember { mutableStateOf(false) }

    var timeLeft by remember { mutableStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsDialog(
            initialFocus = settingsFocus,
            initialShort = settingsShort,
            initialLong = settingsLong,
            onDismiss = { showSettings = false },
            onSubmit = { f, s, l ->
                settingsFocus = f
                settingsShort = s
                settingsLong = l
                // Reset timer memory on change
                isRunning = false
                timeLeft = when(timerMode) { 0 -> settingsFocus*60; 1 -> settingsShort*60; else -> settingsLong*60 }
                showSettings = false
            }
        )
    }

    LaunchedEffect(timerMode) {
        isRunning = false
        timeLeft = when(timerMode) {
            0 -> settingsFocus * 60
            1 -> settingsShort * 60
            else -> settingsLong * 60
        }
    }

    LaunchedEffect(isRunning) {
        while(isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        if (timeLeft == 0 && isRunning) {
            isRunning = false
            // API call to log focus session if it was Focus time
            val durationLogged = when(timerMode) {
                0 -> settingsFocus
                1 -> settingsShort
                else -> settingsLong
            }
            val typeStr = when(timerMode) { 0 -> "focus"; 1 -> "short_break"; else -> "long_break" }
            viewModel.logPomodoroSession(null, durationLogged, typeStr)
            
            // Loop back to full time
            timeLeft = when(timerMode) { 0 -> settingsFocus*60; 1 -> settingsShort*60; else -> settingsLong*60 }
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CardBackground).padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Mode Selectors
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PomodoroTab("Focus Time", timerMode == 0, onClick = { timerMode = 0 }, activeBg = PrimaryOrange)
                    PomodoroTab("Short Break", timerMode == 1, onClick = { timerMode = 1 }, activeBg = Color(0xFF4B5563))
                    PomodoroTab("Long Break", timerMode == 2, onClick = { timerMode = 2 }, activeBg = Color(0xFF4B5563))
                }
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Gray, modifier = Modifier.clickable { showSettings = true })
            }
            
            Spacer(Modifier.height(32.dp))
            
            // Timer Dial
            Box(
                modifier = Modifier.size(240.dp).border(4.dp, BorderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(timeString, color = PrimaryOrange, fontSize = 56.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text(if (isRunning) "FOCUSING" else "PAUSED", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }
            
            Spacer(Modifier.height(32.dp))
            
            // Controls
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(PrimaryPurple).clickable { isRunning = !isRunning },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                }
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(BorderColor).clickable { 
                        isRunning = false
                        timeLeft = when(timerMode) { 0 -> settingsFocus*60; 1 -> settingsShort*60; else -> settingsLong*60 }
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Gray)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun PomodoroTab(title: String, isActive: Boolean, onClick: () -> Unit, activeBg: Color) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(if (isActive) activeBg else BorderColor).clickable { onClick() }.padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(title, color = if (isActive) Color.White else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MetricBox(title: String, value: String, modifier: Modifier = Modifier, titleColor: Color = Color.Gray, bgColor: Color = CardBackground) {
    Box(
        modifier = modifier.border(1.dp, BorderColor, RoundedCornerShape(12.dp)).background(bgColor, RoundedCornerShape(12.dp)).padding(16.dp)
    ) {
        Column {
            Text(title, color = titleColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun CalendarSection(data: PlannerState.Success) {
    val currentMonthRaw = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    // For heatmap dot mapping: Convert to actual month/year checking
    val todayFormatStr = SimpleDateFormat("yyyy-MM-", Locale.getDefault()).format(Date())

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text(currentMonthRaw, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(CardBackground)) {
            Box(modifier = Modifier.padding(12.dp).clickable{}) { Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.Gray) }
            Box(modifier = Modifier.padding(12.dp).clickable{}) { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray) }
        }
    }
    
    Spacer(Modifier.height(24.dp))
    
    val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        days.forEach { Text(it, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center) }
    }
    
    Spacer(Modifier.height(16.dp))
    
    // Calendar Grid rendering logic matching events
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (week in 0 until 5) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (day in 1..7) {
                    val dateNum = week * 7 + day
                    if (dateNum <= 31) {
                        // Check if day has task or exam
                        val dayStr = String.format("%02d", dateNum)
                        val matchString = "$todayFormatStr$dayStr"
                        
                        val hasExam = data.exams.any { it.date.contains(matchString) }
                        val hasTask = data.tasks.any { it.dueDate?.contains(matchString) == true }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardBackground)
                                .clickable{},
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(dateNum.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                                if (hasExam || hasTask) {
                                    Spacer(Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        if (hasExam) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.Red))
                                        if (hasTask) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF60A5FA)))
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsSection(data: PlannerState.Success) {
    val hrs = data.stats.totalFocusTime / 60
    val mins = data.stats.totalFocusTime % 60

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AnalyticsCard(
                title = "Current Streak", icon = Icons.Default.LocalFireDepartment, 
                value = "${data.stats.currentStreak} Days", subtitle = "Best: ${data.stats.longestStreak} Days", iconBg = Color(0xFF78350F), iconTint = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            )
            AnalyticsCard(
                title = "Focus Time", icon = Icons.Default.Schedule, 
                value = "${hrs}h ${mins}m", subtitle = "Total accumulated", iconBg = Color(0xFF1E3A8A), iconTint = Color(0xFF60A5FA),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AnalyticsCard(
                title = "Total XP", icon = Icons.Default.EmojiEvents, 
                value = "${data.stats.totalXP}", subtitle = "Level ${data.stats.level}", iconBg = Color(0xFF4C1D95), iconTint = Color(0xFFA78BFA),
                modifier = Modifier.weight(1f)
            )
            AnalyticsCard(
                title = "Tasks Done", icon = Icons.Default.CheckCircle, 
                value = "${data.stats.tasksCompleted}", subtitle = "All time", iconBg = Color(0xFF064E3B), iconTint = Color(0xFF34D399),
                modifier = Modifier.weight(1f)
            )
        }
        
        // Study Activity
        Box(modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp)).padding(16.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = PrimaryOrange)
                    Spacer(Modifier.width(8.dp))
                    Text("Study Activity", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                
                // Mapped Heatmap
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for(i in 0 until 7) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for(j in 0 until 22) {
                                // Simulate random mapped data for aesthetic or map from activityLog
                                // For MVP: Check if activityLog has data, and color random boxes if count > 0 locally
                                val cellIdx = (j * 7) + i
                                val activityVal = data.stats.activityLog.getOrNull(cellIdx % (data.stats.activityLog.size.coerceAtLeast(1)))?.count ?: 0
                                
                                val bgColor = when {
                                    activityVal > 3 -> Color(0xFF34D399)
                                    activityVal > 1 -> Color(0xFF10B981)
                                    activityVal > 0 -> Color(0xFF064E3B)
                                    else -> BorderColor
                                }
                                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(bgColor))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(BorderColor, CircleShape)) {
                    Box(modifier = Modifier.fillMaxWidth(0.3f).height(4.dp).background(PrimaryPurple, CircleShape))
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    Text("Less", color = Color.Gray, fontSize = 10.sp)
                    Spacer(Modifier.width(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(Modifier.size(8.dp).background(BorderColor))
                        Box(Modifier.size(8.dp).background(Color(0xFF064E3B)))
                        Box(Modifier.size(8.dp).background(Color(0xFF059669)))
                        Box(Modifier.size(8.dp).background(Color(0xFF10B981)))
                        Box(Modifier.size(8.dp).background(Color(0xFF34D399)))
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("More", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }
        
        // Achievements
        Box(modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp)).padding(16.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFBBF24))
                    Spacer(Modifier.width(8.dp))
                    Text("Achievements", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                val warriorProg = (data.stats.currentStreak / 7f).coerceIn(0f, 1f)
                val focusProg = (data.stats.totalFocusTime / 250f).coerceIn(0f, 1f)
                val lvlProg = (data.stats.level / 5f).coerceIn(0f, 1f)
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AchievementCard("Week Warrior", "Study for 7 days in a row", "${data.stats.currentStreak.coerceAtMost(7)} / 7", warriorProg, Modifier.weight(1f))
                    AchievementCard("Focus Master", "Complete 250 mins of Focus", "${data.stats.totalFocusTime.coerceAtMost(250)} / 250", focusProg, Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AchievementCard("Level 5", "Reach Level 5 to unlock", "${data.stats.level.coerceAtMost(5)} / 5", lvlProg, Modifier.weight(1f))
                    Box(Modifier.weight(1f)) 
                }
            }
        }
    }
}

@Composable
fun AnalyticsCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, subtitle: String, iconBg: Color, iconTint: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.border(1.dp, BorderColor, RoundedCornerShape(12.dp)).background(CardBackground, RoundedCornerShape(12.dp)).padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(6.dp)).background(iconBg), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text(title, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = Color.DarkGray, fontSize = 10.sp)
        }
    }
}

@Composable
fun AchievementCard(title: String, desc: String, progressText: String, progressRatio: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.border(1.dp, BorderColor, RoundedCornerShape(12.dp)).background(CardBackground, RoundedCornerShape(12.dp)).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val isComplete = progressRatio >= 1f
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(if(isComplete) Color(0xFF10B981).copy(alpha=0.2f) else BorderColor), contentAlignment = Alignment.Center) {
                Icon(if(isComplete) Icons.Default.Check else Icons.Default.Lock, contentDescription = null, tint = if(isComplete) Color(0xFF10B981) else Color.Gray)
            }
            Spacer(Modifier.height(12.dp))
            Text(title, color = if(isComplete) Color.White else Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(desc, color = Color.Gray, fontSize = 9.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progress", color = Color.Gray, fontSize = 9.sp)
                Text(progressText, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(BorderColor, CircleShape), contentAlignment = Alignment.CenterStart) {
                if (progressRatio > 0f) {
                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progressRatio).background(PrimaryPurple, CircleShape))
                }
            }
        }
    }
}
