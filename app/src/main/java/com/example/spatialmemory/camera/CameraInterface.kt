package com.example.spatialmemory.camera

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

import com.example.spatialmemory.detection.Detection
import com.example.spatialmemory.detection.ObjectDetector

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DetectionDebugState(
    val cameraStatus: String = "Waiting for camera",
    val modelStatus: String = "Not initialized",
    val inferenceStatus: String = "Waiting",
    val framesProcessed: Int = 0,
    val objectsDetected: Int = 0,
    val inferenceTimeMs: Long = 0,
    val detectedLabels: List<String> = emptyList(),
    val errorMessage: String = ""
)

object CameraInterface {

    private const val TAG = "SPATIAL_DETECTION"

    @Volatile
    private var detector: ObjectDetector? = null

    private var frameCount = 0

    private val mainHandler =
        Handler(Looper.getMainLooper())

    private val _detections =
        MutableStateFlow<List<Detection>>(emptyList())

    val detections: StateFlow<List<Detection>> =
        _detections.asStateFlow()

    private val _debugState =
        MutableStateFlow(DetectionDebugState())

    val debugState: StateFlow<DetectionDebugState> =
        _debugState.asStateFlow()


    fun initialize(context: Context) {

        if (detector != null) return

        val newDetector =
            ObjectDetector(context.applicationContext)

        detector = newDetector

        updateState {
            copy(
                modelStatus = "Loading model...",
                inferenceStatus = "Waiting"
            )
        }

        Thread {

            try {

                newDetector.loadModel()

                updateState {
                    copy(
                        modelStatus = "Model initialization finished"
                    )
                }

                Log.d(TAG, "Model initialization finished")

            } catch (e: Exception) {

                Log.e(TAG, "Model initialization error", e)

                updateState {
                    copy(
                        modelStatus = "Model loading failed",
                        errorMessage = e.message ?: "Unknown error"
                    )
                }
            }

        }.start()
    }


    fun onFrame(frame: CameraFrame) {

        frameCount++

        updateState {
            copy(
                cameraStatus = "Receiving frames"
            )
        }

        val activeDetector = detector

        if (activeDetector == null) {

            updateState {
                copy(
                    inferenceStatus = "Detector not initialized"
                )
            }

            return
        }

        val startTime = System.currentTimeMillis()

        try {

            val results =
                activeDetector.detect(frame.imageData)

            val elapsed =
                System.currentTimeMillis() - startTime

            // Publish the latest detections to the UI.
            _detections.value = results

            updateState {

                copy(
                    inferenceStatus = "Completed",
                    framesProcessed = frameCount,
                    objectsDetected = results.size,
                    inferenceTimeMs = elapsed,
                    detectedLabels = results
                        .take(5)
                        .map { it.label }
                        .distinct(),
                    errorMessage = ""
                )
            }

            Log.d(
                TAG,
                "Frame=$frameCount, " +
                    "detections=${results.size}, " +
                    "time=${elapsed}ms"
            )

        } catch (e: Exception) {

            Log.e(TAG, "Inference failed", e)

            updateState {
                copy(
                    inferenceStatus = "Failed",
                    errorMessage = e.message ?: "Unknown inference error"
                )
            }
        }
    }


    private fun updateState(
        update: DetectionDebugState.() -> DetectionDebugState
    ) {

        mainHandler.post {
            _debugState.value =
                _debugState.value.update()
        }
    }
}