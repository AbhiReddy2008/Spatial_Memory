package com.example.spatialmemory.session

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.example.spatialmemory.camera.CameraInterface
import com.example.spatialmemory.camera.CameraManager
import com.example.spatialmemory.imu.IMUManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MemorySessionManager(
    private val context: Context
) {

    // =========================================================
    // HARDWARE MANAGERS
    // =========================================================

    private val cameraManager =
        CameraManager(context)

    private val imuManager =
        IMUManager(context)


    // =========================================================
    // SESSION SCOPE
    // =========================================================

    private val sessionScope =
        CoroutineScope(
            SupervisorJob() +
                    Dispatchers.Main.immediate
        )

    private var timerJob: Job? = null


    // =========================================================
    // SESSION STATE
    // =========================================================

    private val _isSessionActive =
        MutableStateFlow(false)

    val isSessionActive: StateFlow<Boolean> =
        _isSessionActive.asStateFlow()


    private val _cameraPaused =
        MutableStateFlow(false)

    val cameraPaused: StateFlow<Boolean> =
        _cameraPaused.asStateFlow()


    private val _sessionElapsedTime =
        MutableStateFlow(0L)

    val sessionElapsedTime: StateFlow<Long> =
        _sessionElapsedTime.asStateFlow()


    // =========================================================
    // SESSION START TIME
    // =========================================================

    private var sessionStartTime =
        0L


    // =========================================================
    // START SESSION
    // =========================================================

    fun startSession(
        lifecycleOwner: LifecycleOwner
    ) {

        // Prevent starting twice
        if (_isSessionActive.value) {
            return
        }


        println(
            "SESSION >>> STARTING"
        )


        CameraInterface.initialize(
            context
        )


        // Start IMU
        imuManager.startSensors()


        // Start background camera analysis
        cameraManager.startCamera(
            lifecycleOwner = lifecycleOwner
        )


        // Update state
        _isSessionActive.value = true

        _cameraPaused.value = false


        sessionStartTime =
            System.currentTimeMillis()


        _sessionElapsedTime.value = 0L


        startTimer()


        println(
            "SESSION >>> STARTED"
        )
    }


    // =========================================================
    // PAUSE CAMERA
    // =========================================================

    fun pauseCamera() {

        if (!_isSessionActive.value) {
            return
        }


        if (_cameraPaused.value) {
            return
        }


        println(
            "SESSION >>> CAMERA PAUSED"
        )


        /*
         * Only the camera stops.
         *
         * IMU continues running.
         * Session timer continues.
         * Session itself remains active.
         */

        cameraManager.stopCamera()


        _cameraPaused.value = true
    }


    // =========================================================
    // RESUME CAMERA
    // =========================================================

    fun resumeCamera(
        lifecycleOwner: LifecycleOwner
    ) {

        if (!_isSessionActive.value) {
            return
        }


        if (!_cameraPaused.value) {
            return
        }


        println(
            "SESSION >>> CAMERA RESUMING"
        )


        cameraManager.startCamera(
            lifecycleOwner = lifecycleOwner
        )


        _cameraPaused.value = false
    }


    // =========================================================
    // STOP SESSION
    // =========================================================

    fun stopSession() {

        if (!_isSessionActive.value) {
            return
        }


        println(
            "SESSION >>> STOPPING"
        )


        // Stop camera
        cameraManager.stopCamera()


        // Stop IMU
        imuManager.stopSensors()


        // Stop timer
        timerJob?.cancel()
        timerJob = null


        // Reset state
        _isSessionActive.value = false

        _cameraPaused.value = false

        _sessionElapsedTime.value = 0L

        sessionStartTime = 0L


        println(
            "SESSION >>> STOPPED"
        )
    }


    // =========================================================
    // SESSION TIMER
    // =========================================================

    private fun startTimer() {

        timerJob?.cancel()


        timerJob =
            sessionScope.launch {

                while (isActive) {

                    if (_isSessionActive.value) {

                        _sessionElapsedTime.value =
                            System.currentTimeMillis() -
                                    sessionStartTime
                    }


                    delay(1000)
                }
            }
    }
}