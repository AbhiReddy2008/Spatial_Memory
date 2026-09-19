package com.example.spatialmemory.imu

import kotlin.math.abs
import kotlin.math.sqrt

class MotionProcessor {

    private val movementThreshold = 0.3f

    private val turnThreshold = 0.3f


    fun processLinearAcceleration(
        x: Float,
        y: Float,
        z: Float,
        timestamp: Long
    ): MotionEvent {

        val magnitude =
            sqrt(
                x * x +
                        y * y +
                        z * z
            )


        val type =
            if (magnitude > movementThreshold) {

                MotionType.MOVING

            } else {

                MotionType.STATIONARY
            }


        return MotionEvent(
            type = type,
            value = magnitude,
            timestamp = timestamp,
            source = "LINEAR_ACCELERATION"
        )
    }


    fun processGyroscope(
        z: Float,
        timestamp: Long
    ): MotionEvent {

        val type =
            when {

                z > turnThreshold ->
                    MotionType.TURNING_RIGHT

                z < -turnThreshold ->
                    MotionType.TURNING_LEFT

                else ->
                    MotionType.STATIONARY
            }


        return MotionEvent(
            type = type,
            value = abs(z),
            timestamp = timestamp,
            source = "GYROSCOPE"
        )
    }
}