package com.example.lms_android.ui.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

private val DialogBackground = Color(0xFF161320)
private val BorderColor = Color(0xFF2A2735)
private val InputBackground = Color(0xFF1E1B29)
private val PlaceholderColor = Color(0xFF4B485A)
private val PrimaryPurple = Color(0xFF6B58C6)

@Composable
fun NewTaskDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, desc: String, priority: String, estMins: Int, dueDate: String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val calendar = Calendar.getInstance()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium Priority") }
    var priorityExpanded by remember { mutableStateOf(false) }
    val priorities = listOf("High Priority", "Medium Priority", "Low Priority")
    var estMinutes by remember { mutableStateOf("30") }
    var dueDate by remember { mutableStateOf("") }

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                dueDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .background(DialogBackground)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("New Mission", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Title Input
                Text("Title", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                CustomTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "What do you want to accomplish?",
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // Description
                Text("Description", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                CustomTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Add details...",
                    singleLine = false,
                    modifier = Modifier.height(100.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Priority & Minutes Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Priority", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        // Simplified priority field
                        Box {
                            CustomTextField(value = priority, onValueChange = { }, placeholder = "Medium Priority")
                            Box(modifier = Modifier.matchParentSize().clickable { priorityExpanded = true })
                            DropdownMenu(
                                expanded = priorityExpanded,
                                onDismissRequest = { priorityExpanded = false },
                                modifier = Modifier.background(InputBackground)
                            ) {
                                priorities.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt, color = Color.White) },
                                        onClick = { priority = opt; priorityExpanded = false }
                                    )
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Est. Minutes", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        CustomTextField(value = estMinutes, onValueChange = { estMinutes = it }, placeholder = "30")
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Due Date
                Text("Due Date", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    CustomTextField(
                        value = dueDate,
                        onValueChange = { },
                        placeholder = "dd-mm-yyyy"
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { datePickerDialog.show() })
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = (-12).dp)
                    )
                }

                Spacer(Modifier.height(32.dp))

                // Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            val mins = estMinutes.toIntOrNull() ?: 30
                            val mappedPriority = when(priority) {
                                "High Priority" -> "high"
                                "Low Priority" -> "low"
                                else -> "medium"
                            }
                            onSubmit(title, description, mappedPriority, mins, dueDate.ifEmpty { String.format(Locale.getDefault(), "%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)) })
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Create Task")
                    }
                    
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp).border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AddExamDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, date: String, subject: String, color: String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val calendar = Calendar.getInstance()
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
    var subject by remember { mutableStateOf("") }
    val colors = listOf(Color(0xFF60A5FA), Color(0xFFEF4444), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFA78BFA))
    val colorNames = listOf("BLUE", "RED", "GREEN", "ORANGE", "PURPLE")
    var selectedColorIndex by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(DialogBackground)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add Exam", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Title
                Text("Exam Title", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                CustomTextField(value = title, onValueChange = { title = it }, placeholder = "e.g. Maths Final")

                Spacer(Modifier.height(16.dp))

                // Date & Subject Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Date", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Box {
                            CustomTextField(value = date, onValueChange = { }, placeholder = "dd-mm-yyyy")
                            Box(modifier = Modifier.matchParentSize().clickable { datePickerDialog.show() })
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp).align(Alignment.CenterEnd).offset((-12).dp))
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Subject", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        CustomTextField(value = subject, onValueChange = { subject = it }, placeholder = "General")
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Color Tags
                Text("Color Tag", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    colors.forEachIndexed { idx, color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(if (idx == selectedColorIndex) 2.dp else 0.dp, Color.White, CircleShape)
                                .clickable { selectedColorIndex = idx }
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        val finalDate = if(date.isNotEmpty()) date else SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        onSubmit(title, finalDate, subject, colorNames[selectedColorIndex])
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Add Countdown")
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    initialFocus: Int,
    initialShort: Int,
    initialLong: Int,
    onDismiss: () -> Unit,
    onSubmit: (focus: Int, shortBreak: Int, longBreak: Int) -> Unit
) {
    var focus by remember { mutableStateOf(initialFocus.toString()) }
    var shortBreak by remember { mutableStateOf(initialShort.toString()) }
    var longBreak by remember { mutableStateOf(initialLong.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .clip(RoundedCornerShape(16.dp))
                .background(DialogBackground)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚙", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Settings", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text("Focus Time (minutes)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                CustomTextField(value = focus, onValueChange = { focus = it })

                Spacer(Modifier.height(16.dp))

                Text("Short Break (minutes)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                CustomTextField(value = shortBreak, onValueChange = { shortBreak = it })

                Spacer(Modifier.height(16.dp))

                Text("Long Break (minutes)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                CustomTextField(value = longBreak, onValueChange = { longBreak = it })

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "↻ Reset to defaults",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        focus = "25"
                        shortBreak = "5"
                        longBreak = "15"
                    }
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        onSubmit(
                            focus.toIntOrNull() ?: 25,
                            shortBreak.toIntOrNull() ?: 5,
                            longBreak.toIntOrNull() ?: 15
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Save Settings")
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(InputBackground)
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .padding(12.dp),
        contentAlignment = Alignment.TopStart
    ) {
        if (value.isEmpty()) {
            Text(placeholder, color = PlaceholderColor, fontSize = 14.sp)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
            cursorBrush = SolidColor(Color.White),
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth().let { if (!singleLine) it.fillMaxHeight() else it }
        )
    }
}
