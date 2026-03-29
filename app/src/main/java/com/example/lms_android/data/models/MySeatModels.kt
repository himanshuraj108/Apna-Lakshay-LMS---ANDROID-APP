package com.example.lms_android.data.models

data class MySeatResponse(
    val success: Boolean,
    val seat: MySeatDetail?
)

data class MySeatDetail(
    val _id: String,
    val number: String,
    val floor: FloorDetail?,
    val room: RoomDetail?,
    val shift: String?,
    val shiftId: String?,
    val price: Int?,
    val basePrices: Map<String, Int>?,
    val shiftPrices: Map<String, Int>?,
    val isOccupied: Boolean?,
    val status: String?,
    val assignments: List<SeatAssignment>? = null,
    val activeShifts: List<String>? = null,
    val isFullyBlocked: Boolean? = false
)

data class SeatAssignment(
    val _id: String,
    val status: String,
    val shift: String?
)



data class FloorDetail(
    val _id: String,
    val name: String?
)

data class RoomDetail(
    val _id: String,
    val name: String?,
    val doorPosition: String?,  // "north", "south", "east", "west"
    val dimensions: Dimensions?,
    val seats: List<RoomSeat>?
)

data class Dimensions(
    val width: Int?,
    val height: Int?
)

data class RoomSeat(
    val _id: String,
    val number: String,
    val position: SeatPosition?,
    val isOccupied: Boolean?,
    val status: String?,
    val assignments: List<SeatAssignment>? = null,
    val activeShifts: List<String>? = null,
    val isFullyBlocked: Boolean? = false
)


