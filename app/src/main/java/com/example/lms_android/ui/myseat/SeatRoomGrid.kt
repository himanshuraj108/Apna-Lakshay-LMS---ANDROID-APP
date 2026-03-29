package com.example.lms_android.ui.myseat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lms_android.data.models.ShiftResponse
import com.example.lms_android.data.models.RoomDetail
import com.example.lms_android.data.models.RoomSeat

// Colors matching React design
private val colorAvailable = Color(0xFF10B981)
private val colorAvailableBg = Color(0xFF10B981).copy(alpha = 0.3f)
private val colorOccupied = Color(0xFFEF4444)
private val colorOccupiedBg = Color(0xFFEF4444).copy(alpha = 0.3f)
private val colorPartial = Color(0xFFF97316)
private val colorPartialBg = Color(0xFFF97316).copy(alpha = 0.3f)

private val colorHighlightedLine = Color(0xFF60A5FA)
private val colorHighlightedBg = Color(0xFF60A5FA).copy(alpha = 0.4f)

@Composable
fun SeatRoomGrid(
    room: RoomDetail,
    shifts: List<ShiftResponse>,
    highlightSeatId: String,
    onSeatClick: (String) -> Unit = {}
) {
    val doorPosition = room.doorPosition?.lowercase() ?: "south"
    val allSeats = room.seats ?: emptyList()

    val northSeats = allSeats.filter { it.position?.wall == "north" }
        .sortedBy { it.position?.index ?: 0 }
    val southSeats = allSeats.filter { it.position?.wall == "south" }
        .sortedBy { it.position?.index ?: 0 }
    val eastSeats  = allSeats.filter { it.position?.wall == "east" }
        .sortedBy { it.position?.index ?: 0 }
    val westSeats  = allSeats.filter { it.position?.wall == "west" }
        .sortedBy { it.position?.index ?: 0 }

    // Scrollable container (horizontal) to handle small screens since the map is wide
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Room Box
        Box(
            modifier = Modifier
                .width(360.dp)
                .height(240.dp)
                .background(Color.White.copy(alpha = 0.05f))
                .border(2.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
        ) {

            // ── North Wall ─────────────
            Box(
                modifier = Modifier.fillMaxWidth().height(50.dp).align(Alignment.TopCenter)
            ) {
                if (doorPosition == "north") {
                    DoorSpace(Modifier.align(Alignment.Center))
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        SeatGroupRow(seats = northSeats.take(northSeats.size / 2), shifts, highlightSeatId)
                        SeatGroupRow(seats = northSeats.drop(northSeats.size / 2), shifts, highlightSeatId)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Top
                    ) {
                        SeatGroupRow(seats = northSeats, shifts, highlightSeatId)
                    }
                }
            }

            // ── South Wall ─────────────
            Box(
                modifier = Modifier.fillMaxWidth().height(50.dp).align(Alignment.BottomCenter)
            ) {
                if (doorPosition == "south") {
                    DoorSpace(Modifier.align(Alignment.Center))
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        SeatGroupRow(seats = southSeats.take(southSeats.size / 2), shifts, highlightSeatId)
                        SeatGroupRow(seats = southSeats.drop(southSeats.size / 2), shifts, highlightSeatId)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        SeatGroupRow(seats = southSeats, shifts, highlightSeatId)
                    }
                }
            }

            // ── East Wall ─────────────
            Box(
                modifier = Modifier.width(50.dp).fillMaxHeight().align(Alignment.CenterEnd).padding(vertical = 50.dp)
            ) {
                if (doorPosition == "east") {
                    DoorSpace(Modifier.align(Alignment.Center).fillMaxHeight().width(20.dp))
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        SeatGroupCol(seats = eastSeats.take(eastSeats.size / 2), shifts, highlightSeatId)
                        SeatGroupCol(seats = eastSeats.drop(eastSeats.size / 2), shifts, highlightSeatId)
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.End
                    ) {
                        SeatGroupCol(seats = eastSeats, shifts, highlightSeatId)
                    }
                }
            }

            // ── West Wall ─────────────
            Box(
                modifier = Modifier.width(50.dp).fillMaxHeight().align(Alignment.CenterStart).padding(vertical = 50.dp)
            ) {
                if (doorPosition == "west") {
                    DoorSpace(Modifier.align(Alignment.Center).fillMaxHeight().width(20.dp))
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start
                    ) {
                        SeatGroupCol(seats = westSeats.take(westSeats.size / 2), shifts, highlightSeatId)
                        SeatGroupCol(seats = westSeats.drop(westSeats.size / 2), shifts, highlightSeatId)
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.Start
                    ) {
                        SeatGroupCol(seats = westSeats, shifts, highlightSeatId)
                    }
                }
            }

            // ── Room Interior (Center) ──
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(180.dp)
                    .height(100.dp)
                    .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Room Interior", color = Color(0xFF9CA3AF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "${room.dimensions?.width ?: 4}m × ${room.dimensions?.height ?: 4}m",
                        color = Color(0xFF6B7280),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DoorSpace(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color(0xFFEAB308), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("DOOR", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SeatGroupRow(seats: List<RoomSeat>, shifts: List<ShiftResponse>, highlightId: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        seats.forEach { SeatNode(it, shifts, highlightId) }
    }
}

@Composable
private fun SeatGroupCol(seats: List<RoomSeat>, shifts: List<ShiftResponse>, highlightId: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        seats.forEach { SeatNode(it, shifts, highlightId) }
    }
}

@Composable
private fun SeatNode(seat: RoomSeat, shifts: List<ShiftResponse>, highlightId: String) {
    val isHighlighted = seat._id == highlightId

    // Logic to determine seat occupancy color
    // Fully occupied (red) = all shifts blocked or isOccupied. 
    // Partially (orange) = some active shifts.
    // Available (green) = no active shifts.
    val isFullyOccupied = seat.isOccupied == true || seat.isFullyBlocked == true
    val isPartial = seat.activeShifts?.isNotEmpty() == true && !isFullyOccupied

    val strokeColor = when {
        isHighlighted -> colorHighlightedLine
        isFullyOccupied -> colorOccupied
        isPartial -> colorPartial
        else -> colorAvailable
    }
    
    val bgColor = when {
        isHighlighted -> colorHighlightedBg
        isFullyOccupied -> colorOccupiedBg
        isPartial -> colorPartialBg
        else -> colorAvailableBg
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(
                width = if (isHighlighted) 2.dp else 1.dp,
                color = if (isHighlighted) strokeColor.copy(alpha = pulseAlpha) else strokeColor,
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(
                imageVector = Icons.Default.Bed,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (isHighlighted) Color.White else strokeColor
            )
            Text(
                text = seat.number,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) Color.White else strokeColor
            )
        }
        
        // Status dot for non-highlighted, non-empty seats
        if (!isHighlighted && (isFullyOccupied || isPartial)) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .background(strokeColor, RoundedCornerShape(3.dp))
            )
        }
    }
}
