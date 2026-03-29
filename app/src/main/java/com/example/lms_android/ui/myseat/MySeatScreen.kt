package com.example.lms_android.ui.myseat

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lms_android.data.models.MySeatDetail
import com.example.lms_android.data.models.ShiftResponse
import com.example.lms_android.ui.home.bgDark
import com.example.lms_android.ui.home.colorTextSecondary

val cardBg = Color(0xFF13151D)

@Composable
fun MySeatScreen(
    onNavigateBack: () -> Unit,
    viewModel: MySeatViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
    ) {
        when (val s = state) {
            is MySeatState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7C3AED))
                }
            }
            is MySeatState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFF87171), modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("No Seat Assigned", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(s.message, color = colorTextSecondary)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = onNavigateBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))) {
                            Text("Go Back")
                        }
                    }
                }
            }
            is MySeatState.Success -> {
                if (s.mySeatData.seat == null) {
                    // No Seat Assigned View
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.SearchOff, null, tint = colorTextSecondary, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("No Seat Assigned", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Contact admin to get a seat allocated.", color = colorTextSecondary, fontSize = 14.sp)
                        Spacer(Modifier.height(32.dp))
                        Button(onClick = onNavigateBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))) {
                            Text("Back to Dashboard")
                        }
                    }
                } else {
                    MySeatContent(
                        seat = s.mySeatData.seat,
                        shifts = s.availableShifts,
                        onBack = onNavigateBack
                    )
                }
            }
        }
    }
}

@Composable
private fun MySeatContent(
    seat: MySeatDetail,
    shifts: List<ShiftResponse>,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // ── Header ────────────────────────────────────────────────────────
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
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("My Seat", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("Your assigned study spot", color = colorTextSecondary, fontSize = 13.sp)
            }
        }

        // ── Hero Card ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF2E1065), Color(0xFF1E1B4B))))
                .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            // Glow effect
            Box(modifier = Modifier.absoluteOffset(x = 100.dp, y = (-20).dp).size(150.dp).background(Color(0xFF7C3AED).copy(alpha = 0.15f), CircleShape))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4F46E5)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Bed, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("SEAT NO.", color = Color(0xFFA78BFA).copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(seat.number, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    }
                }
                
                Spacer(Modifier.height(20.dp))
                Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
                Spacer(Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val pulse by rememberInfiniteTransition(label = "").animateFloat(initialValue = 0.4f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "")
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF34D399).copy(alpha = pulse), CircleShape))
                        Spacer(Modifier.width(6.dp))
                        Text("Active", color = Color(0xFF34D399), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(seat.shift?.uppercase() ?: "", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Detail Chips ──────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val loc = "${seat.floor?.name ?: "Unknown"}, ${seat.room?.name ?: ""}"
            DetailChip(icon = Icons.Default.LocationOn, label = "LOCATION", value = loc, accentColor = Color(0xFF3B82F6))
            DetailChip(icon = Icons.Default.AccessTime, label = "SHIFT", value = seat.shift ?: "—", accentColor = Color(0xFF10B981))
            val priceStr = "₹${seat.price ?: 800}"
            DetailChip(icon = Icons.Default.Payments, label = "MONTHLY FEE", value = priceStr, accentColor = Color(0xFFF59E0B))
        }

        Spacer(Modifier.height(24.dp))

        // ── Pricing Plans ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(cardBg)
                .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(20.dp))
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(24.dp).background(Color(0xFFF59E0B).copy(alpha = 0.15f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Payments, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(14.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Pricing Plans", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Divider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
                
                Column(modifier = Modifier.padding(16.dp)) {
                    shifts.forEach { shift ->
                        val isCurrent = seat.shiftId == shift._id || seat.shift == shift.name
                        val shiftPrice = seat.shiftPrices?.get(shift._id) ?: seat.basePrices?.get(shift._id) ?: 800
                        val timeRange = "${shift.startTime} - ${shift.endTime}"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCurrent) Color(0xFF10B981).copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f))
                                .border(1.dp, if (isCurrent) Color(0xFF10B981).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(shift.name, color = if (isCurrent) Color(0xFF4ADE80) else Color(0xFFD1D5DB), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Spacer(Modifier.height(2.dp))
                                Text(timeRange, color = colorTextSecondary, fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isCurrent) {
                                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("₹$shiftPrice", color = if (isCurrent) Color.White else Color(0xFF9CA3AF), fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Room Map ──────────────────────────────────────────────────────
        if (seat.room != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardBg)
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(20.dp))
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(24.dp).background(Color(0xFF3B82F6).copy(alpha = 0.15f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ViewModule, null, tint = Color(0xFF60A5FA), modifier = Modifier.size(14.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("Seat Location Map", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Box(modifier = Modifier.background(Color(0xFF3B82F6).copy(alpha = 0.1f), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.2f), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                            Text(seat.room.name ?: "Room", color = Color(0xFF60A5FA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Divider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
                    
                    // Render Map Grid Custom Compose Layout!
                    SeatRoomGrid(
                        room = seat.room,
                        shifts = shifts,
                        highlightSeatId = seat._id
                    )

                    // Legend Footer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(Color(0xFF7C3AED).copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bed, null, tint = Color(0xFFA78BFA), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Your seat ", color = Color(0xFFA78BFA), fontSize = 13.sp)
                            Text("#${seat.number}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
                            Text(" is highlighted on the map.", color = Color(0xFFA78BFA), fontSize = 13.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun DetailChip(icon: ImageVector, label: String, value: String, accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(accentColor.copy(alpha = 0.05f), Color.White.copy(alpha = 0.02f))))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // Left accent line
        Box(modifier = Modifier.align(Alignment.CenterStart).height(32.dp).width(3.dp).background(accentColor, RoundedCornerShape(2.dp)))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 12.dp)) {
            Box(
                modifier = Modifier.size(36.dp).background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, color = colorTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
