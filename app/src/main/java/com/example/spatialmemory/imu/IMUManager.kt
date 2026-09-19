package com.example.spatialmemory.imu

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class IMUManager(
    private val context: Context
) {

    private val sensorManager =
        context.getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager

    private val motionProcessor =
        MotionProcessor()

    private var isRunning = false


    private val sensorListener =
        object : SensorEventListener {

            override fun onSensorChanged(
                event: SensorEvent
            ) {

                when (event.sensor.type) {

                    Sensor.TYPE_LINEAR_ACCELERATION -> {

                        val motionEvent =
                            motionProcessor
                                .processLinearAcceleration(
                                    x = event.values[0],
                                    y = event.values[1],
                                    z = event.values[2],
                                    timestamp = event.timestamp
                                )

                        IMUInterface.onMotionEvent(
                            motionEvent
                        )
                    }


                    Sensor.TYPE_GYROSCOPE -> {

                        val motionEvent =
                            motionProcessor
                                .processGyroscope(
                                    z = event.values[2],
                                    timestamp = event.timestamp
                                )

                        IMUInterface.onMotionEvent(
                            motionEvent
                        )
                    }


                    Sensor.TYPE_ROTATION_VECTOR -> {

                        // Rotation data is being received.
                        // Detailed logging is disabled to prevent
                        // Logcat flooding.
                    }
                }
            }


            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int
            ) {
                // Nothing required for now.
            }
        }


    fun startSensors() {

        if (isRunning) {
            return
        }


        val linearAcceleration =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_LINEAR_ACCELERATION
            )


        val gyroscope =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_GYROSCOPE
            )


        val rotationVector =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR
            )


        linearAcceleration?.let {

            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }


        gyroscope?.let {

            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }


        rotationVector?.let {

            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }


        isRunning = true


        println(
            "IMU MANAGER >>> SENSORS STARTED"
        )
    }


    fun stopSensors() {

        if (!isRunning) {
            return
        }


        sensorManager.unregisterListener(
            sensorListener
        )


        isRunning = false


        println(
            "IMU MANAGER >>> SENSORS STOPPED"
        )
    }
}