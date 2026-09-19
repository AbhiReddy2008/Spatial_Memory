package com.example.spatialmemory.camera

data class CameraFrame(
    val imageData: ByteArray,
    val timestamp: Long,
    val width: Int,
    val height: Int,
    val rotationDegrees: Int
)