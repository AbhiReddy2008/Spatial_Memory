package com.example.spatialmemory.observation

import java.util.UUID

data class VisualObservation(

    val observationId: String = UUID.randomUUID().toString(),

    val timestamp: Long = System.currentTimeMillis(),

    val objectLabel: String,

    val confidence: Float,

    // Normalized image coordinates: 0.0 to 1.0
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,

    // Reference to the captured image
    val imagePath: String? = null,

    // Camera orientation from IMU, when available
    val cameraYaw: Float? = null,
    val cameraPitch: Float? = null,
    val cameraRoll: Float? = null
)
