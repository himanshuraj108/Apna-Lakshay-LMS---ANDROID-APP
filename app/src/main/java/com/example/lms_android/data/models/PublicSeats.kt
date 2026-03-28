package com.example.lms_android.data.models

data class PublicSeatsResponse(
    val success: Boolean,
    val maintenance: Boolean? = false,
    val message: String? = null,
    val floors: List<FloorResponse> = emptyList()
)

data class FloorResponse(
    val _id: String,
    val name: String,
    val level: Int,
    val rooms: List<RoomResponse>
)

data class RoomResponse(
    val _id: String,
    val name: String,
    val dimensions: RoomDimensions?,
    val doorPosition: String?, // north, south, east, west
    val seats: List<SeatResponse>
)

data class RoomDimensions(
    val width: Int,
    val height: Int
)

data class SeatResponse(
    val _id: String,
    val number: String,
    val isOccupied: Boolean,
    val status: String, // vacant, partial, occupied
    val isFullyBlocked: Boolean,
    val position: SeatPosition?,
    val activeShifts: List<String> = emptyList(),
    val shift: String? = null, // Display string for shift
    val assignments: List<AssignmentResponse> = emptyList(),
    val basePrices: Map<String, Int> = emptyMap(),
    val shiftPrices: Map<String, Int> = emptyMap()
)

data class AssignmentResponse(
    val status: String,
    val shift: ShiftSummary?,
    val legacyShift: String?
)

data class ShiftSummary(
    val _id: String,
    val name: String,
    val startTime: String,
    val endTime: String
)

data class SeatPosition(
    val wall: String?, // north, south, east, west
    val index: Int?
)

data class PublicShiftsResponse(
    val success: Boolean,
    val shifts: List<ShiftResponse>
)

data class ShiftResponse(
    val _id: String,
    val id: String?,
    val name: String,
    val startTime: String,
    val endTime: String,
    val isActive: Boolean,
    val legacyName: String?
)
