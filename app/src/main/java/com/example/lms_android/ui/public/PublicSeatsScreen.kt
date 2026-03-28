package com.example.lms_android.ui.public

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.FloorResponse
import com.example.lms_android.data.models.RoomResponse
import com.example.lms_android.data.models.SeatResponse
import com.example.lms_android.data.models.ShiftResponse
import kotlinx.coroutines.launch

@Composable
fun PublicSeatsScreen(onNavigateLogin: () -> Unit) {
    val scope = rememberCoroutineScope()
    var floors by remember { mutableStateOf<List<FloorResponse>>(emptyList()) }
    var shifts by remember { mutableStateOf<List<ShiftResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedFloorIndex by remember { mutableStateOf(0) }
    var selectedSeat by remember { mutableStateOf<SeatResponse?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isMaintenance by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val shiftsResponse = ApiClient.apiService.getPublicShifts()
            if (shiftsResponse.success) shifts = shiftsResponse.shifts
            
            val response = ApiClient.apiService.getPublicSeats()
            if (response.maintenance == true) {
                isMaintenance = true
            } else {
                floors = response.floors
            }
        } catch (e: Exception) {
            errorMsg = "Failed to load seats data. Check connection."
        } finally {
            isLoading = false
        }
    }

    // Outer Background matches standard LMS background styling
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030712)) // Deep dark generic background matching React tailwind
    ) {
        // Red/Orange Radial Gradient mock logic
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFF97316).copy(alpha = 0.09f), Color.Transparent),
                        radius = 1000f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // -- Top Navbar --
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row {
                        Text("Apna ", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text(
                            "Lakshay",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            style = androidx.compose.ui.text.TextStyle(
                                brush = Brush.horizontalGradient(listOf(Color(0xFFFB923C), Color(0xFFF87171)))
                            )
                        )
                    }
                    Text("LIBRARY MANAGEMENT SYSTEM", color = Color(0xFF6B7280), fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Library Seat Availability", color = Color(0xFFD1D5DB), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateLogin,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.background(
                        brush = Brush.horizontalGradient(listOf(Color(0xFFF97316), Color(0xFFEF4444))),
                        shape = RoundedCornerShape(20.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LOGIN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFB923C))
                }
            } else if (isMaintenance) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("System is under maintenance. Please try again later.", color = Color.White)
                }
            } else if (errorMsg != null) {
                Text(errorMsg!!, color = Color.Red, modifier = Modifier.padding(16.dp))
            } else if (floors.isNotEmpty()) {
                // -- Floor Tabs --
                ScrollableTabRow(
                    selectedTabIndex = selectedFloorIndex,
                    containerColor = Color.Transparent,
                    edgePadding = 0.dp,
                    indicator = { },
                    divider = { }
                ) {
                    floors.forEachIndexed { index, floor ->
                        val isSelected = index == selectedFloorIndex
                        Tab(
                            selected = isSelected,
                            onClick = { selectedFloorIndex = index },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Brush.horizontalGradient(listOf(Color(0xFFF97316), Color(0xFFEF4444)))
                                    else Brush.horizontalGradient(listOf(Color.White.copy(0.05f), Color.White.copy(0.05f)))
                                )
                                .border(1.dp, if (isSelected) Color.Transparent else Color.White.copy(0.1f), RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                floor.name,
                                color = if (isSelected) Color.White else Color(0xFF9CA3AF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // -- Rooms --
                val selectedFloor = floors[selectedFloorIndex]
                selectedFloor.rooms.forEach { room ->
                    RoomLayout(room = room) { seat ->
                        selectedSeat = seat
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // -- Floor Summary --
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Floor Summary", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        val totalSeats = selectedFloor.rooms.sumOf { it.seats.size }
                        val occupied = selectedFloor.rooms.sumOf { r -> r.seats.count { it.status == "occupied" || it.status == "partial" } }
                        val available = selectedFloor.rooms.sumOf { r -> r.seats.count { it.status == "vacant" } }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            SummaryBox("TOTAL SEATS", totalSeats.toString(), Color.White, Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(12.dp))
                            SummaryBox("OCCUPIED", occupied.toString(), Color(0xFFF87171), Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(12.dp))
                            SummaryBox("AVAILABLE", available.toString(), Color(0xFF4ADE80), Modifier.weight(1f))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        selectedSeat?.let { seat ->
            SeatDetailsDialog(
                seat = seat,
                shifts = shifts,
                onDismiss = { selectedSeat = null }
            )
        }
    }
}

@Composable
fun SummaryBox(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = Color(0xFF6B7280), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = valueColor, fontSize = 24.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun RoomLayout(room: RoomResponse, onSeatClick: (SeatResponse) -> Unit) {
    val totalSeats = room.seats.size
    val northSeats = room.seats.filter { it.position?.wall == "north" }.sortedBy { it.position?.index ?: 0 }
    val southSeats = room.seats.filter { it.position?.wall == "south" }.sortedBy { it.position?.index ?: 0 }
    val eastSeats = room.seats.filter { it.position?.wall == "east" }.sortedBy { it.position?.index ?: 0 }
    val westSeats = room.seats.filter { it.position?.wall == "west" }.sortedBy { it.position?.index ?: 0 }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(Color(0xFF1F2937), Color(0xFF111827))),
                RoundedCornerShape(16.dp)
            )
            .border(2.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            // Room Title
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(Color(0xFF111827), RoundedCornerShape(8.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("${room.name} ($totalSeats Seats)", color = Color(0xFF9CA3AF), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // The Graphical Box map is generally wide, allow horizontal scrolling to prevent squishing
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                Box(
                    modifier = Modifier
                        .width(420.dp)
                        .height(260.dp)
                ) {
                    
                    // Interior Region (drawn first so it is underneath)
                    Box(modifier = Modifier.fillMaxSize().padding(60.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(Color(0xFF374151).copy(alpha=0.4f), Color(0xFF1F2937).copy(alpha=0.4f))), RoundedCornerShape(12.dp))
                                .border(2.dp, Color.White.copy(alpha=0.2f), RoundedCornerShape(12.dp)), // dashed not easily supported out of box, use solid
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Room Interior", color = Color(0xFF9CA3AF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${room.dimensions?.width ?: 4}m × ${room.dimensions?.height ?: 4}m", color = Color(0xFF6B7280), fontSize = 12.sp)
                            }
                        }
                    }

                    // North Wall
                    Box(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().height(60.dp)) {
                        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                            if (room.doorPosition == "north") {
                                // Draw seats left, door middle, seats right
                                val mid = northSeats.size / 2
                                val left = northSeats.take(mid)
                                val right = northSeats.drop(mid)
                                left.forEach { SeatComposable(it, onSeatClick) }
                                DoorBadge()
                                right.forEach { SeatComposable(it, onSeatClick) }
                            } else {
                                northSeats.forEach { SeatComposable(it, onSeatClick) }
                            }
                        }
                    }

                    // South Wall
                    Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(60.dp)) {
                        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                            if (room.doorPosition == "south") {
                                val mid = southSeats.size / 2
                                val left = southSeats.take(mid)
                                val right = southSeats.drop(mid)
                                left.forEach { SeatComposable(it, onSeatClick) }
                                DoorBadge()
                                right.forEach { SeatComposable(it, onSeatClick) }
                            } else {
                                southSeats.forEach { SeatComposable(it, onSeatClick) }
                            }
                        }
                    }

                    // West Wall
                    Box(modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight().width(60.dp)) {
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                            if (room.doorPosition == "west") {
                                val mid = westSeats.size / 2
                                val top = westSeats.take(mid)
                                val bottom = westSeats.drop(mid)
                                top.forEach { SeatComposable(it, onSeatClick) }
                                DoorBadge()
                                bottom.forEach { SeatComposable(it, onSeatClick) }
                            } else {
                                westSeats.forEach { SeatComposable(it, onSeatClick) }
                            }
                        }
                    }

                    // East Wall
                    Box(modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().width(60.dp)) {
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                            if (room.doorPosition == "east") {
                                val mid = eastSeats.size / 2
                                val top = eastSeats.take(mid)
                                val bottom = eastSeats.drop(mid)
                                top.forEach { SeatComposable(it, onSeatClick) }
                                DoorBadge()
                                bottom.forEach { SeatComposable(it, onSeatClick) }
                            } else {
                                eastSeats.forEach { SeatComposable(it, onSeatClick) }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                LegendItem("Available", Color(0xFF22C55E))
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem("Partially", Color(0xFFF97316)) 
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem("Occupied", Color(0xFFEF4444))
            }
        }
    }
}

@Composable
fun DoorBadge() {
    Box(
        modifier = Modifier
            .background(Color(0xFFEAB308), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text("DOOR", color = Color(0xFF111827), fontSize = 10.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color.copy(alpha=0.3f), RoundedCornerShape(2.dp)).border(2.dp, color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, color = Color(0xFF9CA3AF), fontSize = 10.sp)
    }
}

@Composable
fun SeatComposable(seat: SeatResponse, onSeatClick: (SeatResponse) -> Unit) {
    val statusColor = when (seat.status) {
        "occupied" -> Color(0xFFEF4444)
        "partial" -> Color(0xFFF97316)
        else -> Color(0xFF22C55E)
    }

    Box(
        modifier = Modifier
            .background(statusColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .border(2.dp, statusColor, RoundedCornerShape(8.dp))
            .clickable { onSeatClick(seat) }
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.White.copy(alpha=0.8f), modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(seat.number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        // Red occupied dot
        if (seat.status != "vacant") {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-10).dp)
                    .size(10.dp)
                    .background(Color(0xFFDC2626), CircleShape)
                    .border(2.dp, Color(0xFF111827), CircleShape)
            )
        }
    }
}
