package com.example.spatialmemory.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

import com.example.spatialmemory.detection.OCRReader

import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors


class CameraManager(
    private val context: Context
) {

    private var cameraProvider: ProcessCameraProvider? = null

    private var imageAnalysis: ImageAnalysis? = null

    private var isRunning = false

    private var frameCount = 0

    private var lastLogTime = 0L

    private var lastFrameSentTime = 0L

    private val analysisExecutor =
        Executors.newSingleThreadExecutor()

    private val ocrReader =
        OCRReader()


    // =========================================================
    // START CAMERA
    // =========================================================

    fun startCamera(
        lifecycleOwner: LifecycleOwner
    ) {

        if (isRunning) {
            return
        }

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val provider =
                cameraProviderFuture.get()

            cameraProvider =
                provider


            // =================================================
            // IMAGE ANALYSIS
            // =================================================

            imageAnalysis =
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(
                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    )
                    .build()

            imageAnalysis?.setAnalyzer(
                analysisExecutor
            ) { image ->

                processFrame(image)
            }


            // =================================================
            // CAMERA
            // =================================================

            val cameraSelector =
                CameraSelector.DEFAULT_BACK_CAMERA

            try {

                provider.unbindAll()

                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageAnalysis
                )

                isRunning = true

                println(
                    "CAMERA MANAGER >>> " +
                            "BACKGROUND CAMERA STARTED"
                )

            } catch (e: Exception) {

                println(
                    "CAMERA MANAGER >>> " +
                            "ERROR: ${e.message}"
                )
            }

        }, ContextCompat.getMainExecutor(context))
    }


    // =========================================================
    // PROCESS CAMERA FRAME
    // =========================================================

    private fun processFrame(
        image: ImageProxy
    ) {

        frameCount++

        val currentTime =
            System.currentTimeMillis()


        // =====================================================
        // CAMERA STATUS
        // =====================================================

        if (
            currentTime - lastLogTime >= 1000
        ) {

            lastLogTime =
                currentTime

            println(
                "CAMERA >>> " +
                        "frames=$frameCount " +
                        "size=${image.width}x${image.height}"
            )
        }


        // =====================================================
        // FRAME RATE
        //
        // 500 ms ≈ 2 FPS
        //
        // We intentionally keep this low while verifying OCR.
        // =====================================================

        if (
            currentTime - lastFrameSentTime < 500
        ) {

            image.close()

            return
        }

        lastFrameSentTime =
            currentTime


        try {

            // =================================================
            // 1. CONVERT CAMERA FRAME TO JPEG
            // =================================================

            val jpegBytes =
                imageProxyToJpeg(image)


            // =================================================
            // 2. CREATE BITMAP COPY
            //
            // OCR works on this Bitmap.
            // It does NOT directly use ImageProxy.
            // =================================================

            val bitmap =
                BitmapFactory.decodeByteArray(
                    jpegBytes,
                    0,
                    jpegBytes.size
                )


            // =================================================
            // 3. OCR
            //
            // imageProxyToJpeg() already rotates the image.
            // Therefore OCR rotation is 0.
            // =================================================

            if (bitmap != null) {

                ocrReader.process(

                    bitmap = bitmap,

                    rotationDegrees = 0

                ) { ocrResults ->

                    println(
                        "OCR >>> " +
                                "detected ${ocrResults.size} " +
                                "text blocks"
                    )

                    for (result in ocrResults) {

                        println(
                            "OCR >>> " +
                                    "text='${result.text}' " +
                                    "box=${result.boundingBox}"
                        )
                    }


                    // =================================================
                    // Bitmap is no longer needed after ML Kit finishes.
                    // =================================================

                    bitmap.recycle()
                }

            } else {

                println(
                    "OCR >>> " +
                            "Could not create bitmap"
                )
            }


            // =================================================
            // 4. CREATE CAMERA FRAME
            //
            // Existing pipeline continues to receive the frame.
            // =================================================

            val frame =
                CameraFrame(

                    imageData =
                        jpegBytes,

                    timestamp =
                        image.imageInfo.timestamp,

                    width =
                        image.width,

                    height =
                        image.height,

                    rotationDegrees =
                        image.imageInfo.rotationDegrees
                )


            // =================================================
            // 5. SEND TO EXISTING PIPELINE
            // =================================================

            CameraInterface.onFrame(
                frame
            )


        } catch (e: Exception) {

            println(
                "CAMERA >>> " +
                        "FRAME PROCESSING ERROR: " +
                        e.message
            )

        } finally {

            // =================================================
            // 6. CLOSE CAMERA IMAGE
            //
            // Safe because OCR is processing the separate Bitmap.
            // =================================================

            image.close()
        }
    }


    // =========================================================
    // YUV → JPEG
    // =========================================================

    private fun imageProxyToJpeg(
        image: ImageProxy
    ): ByteArray {

        val yBuffer =
            image.planes[0].buffer

        val uBuffer =
            image.planes[1].buffer

        val vBuffer =
            image.planes[2].buffer


        val ySize =
            yBuffer.remaining()

        val uSize =
            uBuffer.remaining()

        val vSize =
            vBuffer.remaining()


        val nv21 =
            ByteArray(
                ySize +
                        uSize +
                        vSize
            )


        yBuffer.get(
            nv21,
            0,
            ySize
        )

        vBuffer.get(
            nv21,
            ySize,
            vSize
        )

        uBuffer.get(
            nv21,
            ySize + vSize,
            uSize
        )


        val yuvImage =
            YuvImage(
                nv21,
                ImageFormat.NV21,
                image.width,
                image.height,
                null
            )


        val outputStream =
            ByteArrayOutputStream()


        yuvImage.compressToJpeg(
            Rect(
                0,
                0,
                image.width,
                image.height
            ),
            85,
            outputStream
        )


        var jpegBytes =
            outputStream.toByteArray()


        // =====================================================
        // ROTATE IMAGE
        // =====================================================

        val rotation =
            image.imageInfo.rotationDegrees


        if (rotation != 0) {

            val bitmap =
                BitmapFactory.decodeByteArray(
                    jpegBytes,
                    0,
                    jpegBytes.size
                )


            if (bitmap != null) {

                val matrix =
                    Matrix().apply {

                        postRotate(
                            rotation.toFloat()
                        )
                    }


                val rotatedBitmap =
                    Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        matrix,
                        true
                    )


                val rotatedOutput =
                    ByteArrayOutputStream()


                rotatedBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    85,
                    rotatedOutput
                )


                jpegBytes =
                    rotatedOutput.toByteArray()


                bitmap.recycle()

                rotatedBitmap.recycle()
            }
        }


        outputStream.close()


        return jpegBytes
    }


    // =========================================================
    // STOP CAMERA
    // =========================================================

    fun stopCamera() {

        if (!isRunning) {
            return
        }


        cameraProvider?.unbindAll()

        imageAnalysis?.clearAnalyzer()

        imageAnalysis = null

        isRunning = false


        println(
            "CAMERA MANAGER >>> " +
                    "BACKGROUND CAMERA STOPPED"
        )
    }
}