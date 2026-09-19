package com.example.spatialmemory.imu

data class MotionEvent(
    val type: MotionType,
    val value: Float,
    val timestamp: Long,
    val source: String
)

enum class MotionType {
    STATIONARY,
    MOVING,
    TURNING_LEFT,
    TURNING_RIGHT
}