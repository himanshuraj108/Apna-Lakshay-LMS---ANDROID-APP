package com.example.lms_android.ui.public

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lms_android.data.models.SeatResponse
import com.example.lms_android.data.models.ShiftResponse

// Helper for time overlap
fun doTimeRangesOverlap(s1: String?, e1: String?, s2: String?, e2: String?): Boolean {
    if (s1.isNullOrBlank() || e1.isNullOrBlank() || s2.isNullOrBlank() || e2.isNullOrBlank()) return false
    val toMin = { t: String ->
        val parts = t.split(":")
        if (parts.size == 2) parts[0].toIntOrNull()?.times(60)?.plus(parts[1].toIntOrNull() ?: 0) ?: 0 else 0
    }
    return toMin(s1) < toMin(e2) && toMin(s2) < toMin(e1)
}

data class ShiftStatusObj(val label: String, val bgColor: Color, val textColor: Color, val borderColor: Color)

fun getShiftStatus(seat: SeatResponse, shift: ShiftResponse): ShiftStatusObj {
    val shiftId = shift._id ?: shift.id ?: ""
    val isDirectlyBooked = seat.activeShifts.any { it == shiftId || it == shift.legacyName } ||
            seat.assignments.any { it.shift?._id == shiftId }
            
    val isFullDayShift = shiftId == "full" || shift.legacyName == "full_day" || shift.name.lowercase().contains("full")
    val isDirectFullDayBooked = seat.isFullyBlocked && isFullDayShift
    
    val isOverlapOccupied = seat.assignments.any { a ->
        a.status == "active" && a.shift != null && doTimeRangesOverlap(shift.startTime, shift.endTime, a.shift.startTime, a.shift.endTime)
    }
    
    val isPartiallyBooked = seat.assignments.isNotEmpty()

    if (isDirectlyBooked || isDirectFullDayBooked)
        return ShiftStatusObj("Occupied", Color(0xFFEF4444).copy(alpha = 0.15f), Color(0xFFF87171), Color(0xFFEF4444).copy(alpha = 0.25f))
    if (isOverlapOccupied || (seat.isFullyBlocked && !isFullDayShift))
        return ShiftStatusObj("Not Available", Color(0xFF6B7280).copy(alpha = 0.15f), Color(0xFF9CA3AF), Color(0xFF6B7280).copy(alpha = 0.25f))
    if (isFullDayShift && isPartiallyBooked)
        return ShiftStatusObj("Not Available", Color(0xFF6B7280).copy(alpha = 0.15f), Color(0xFF9CA3AF), Color(0xFF6B7280).copy(alpha = 0.25f))
        
    return ShiftStatusObj("Available", Color(0xFF22C55E).copy(alpha = 0.15f), Color(0xFF4ADE80), Color(0xFF22C55E).copy(alpha = 0.25f))
}

@Composable
fun SeatDetailsDialog(seat: SeatResponse, shifts: List<ShiftResponse>, onDismiss: () -> Unit) {
    val isFullyOccupied = shifts.all { getShiftStatus(seat, it).label != "Available" }
    val hasAnyOccupied = seat.assignments.isNotEmpty()
    
    val overallStatusLabel = if (isFullyOccupied) "Fully Occupied" else if (hasAnyOccupied) "Partially Occupied" else "Available"
    val overallDotColor = if (isFullyOccupied) Color(0xFFF87171) else if (hasAnyOccupied) Color(0xFFFBBF24) else Color(0xFF4ADE80)
    val overallPillBg = if (isFullyOccupied) Color(0xFFEF4444).copy(0.1f) else if (hasAnyOccupied) Color(0xFFF59E0B).copy(0.1f) else Color(0xFF22C55E).copy(0.1f)
    val overallPillText = if (isFullyOccupied) Color(0xFFFCA5A5) else if (hasAnyOccupied) Color(0xFFFCD34D) else Color(0xFF86EFAC)
    val overallPillBorder = if (isFullyOccupied) Color(0xFFEF4444).copy(0.2f) else if (hasAnyOccupied) Color(0xFFF59E0B).copy(0.2f) else Color(0xFF22C55E).copy(0.2f)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(24.dp))
                    .background(Color(0xFF0D0D16)) // approximate dark backdrop
            ) {
                // Top Color Accent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Brush.horizontalGradient(listOf(Color(0xFFF97316), Color(0xFFEF4444))))
                )

                // Content Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFEF4444)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Seat ${seat.number}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text("${seat.position?.wall?.replaceFirstChar { it.uppercase() } ?: "Library"} Wall", color = Color(0xFF6B7280), fontSize = 12.sp)
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .background(Color.White.copy(0.05f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF9CA3AF))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(0.06f)))
                    Spacer(modifier = Modifier.height(20.dp))

                    // Overall Status Pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .background(overallPillBg, RoundedCornerShape(12.dp))
                            .border(1.dp, overallPillBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(overallDotColor, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(overallStatusLabel, color = overallPillText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Shift Availability Block
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(0.03f), RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFB923C), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Shift Availability & Pricing", color = Color(0xFFD1D5DB), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }

                        shifts.forEach { shift ->
                            val st = getShiftStatus(seat, shift)
                            val shiftIdStr = shift.id ?: shift._id
                            val price = seat.basePrices[shiftIdStr] ?: seat.shiftPrices[shiftIdStr] ?: 800

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(Color.White.copy(0.03f), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(shift.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text("${shift.startTime} – ${shift.endTime}", color = Color(0xFF6B7280), fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Box(
                                        modifier = Modifier
                                            .background(st.bgColor, RoundedCornerShape(12.dp))
                                            .border(1.dp, st.borderColor, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Text(st.label, color = st.textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("₹$price", color = Color(0xFFD1D5DB), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location bar
                    if (seat.position != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(0.03f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "${seat.position.wall?.replaceFirstChar { it.uppercase() } ?: "Library"} wall · Position ${seat.position.index?.plus(1) ?: 1}",
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Close Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.horizontalGradient(listOf(Color(0xFFF97316), Color(0xFFEF4444))), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Close", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
