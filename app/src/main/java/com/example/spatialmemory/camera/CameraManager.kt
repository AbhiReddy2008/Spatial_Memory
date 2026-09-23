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
<<<<<<< HEAD
        // 200 ms ≈ 5 FPS
        // =====================================================

        if (
<<<<<<< HEAD
            currentTime - lastFrameSentTime < 200
=======
        // 500 ms ≈ 2 FPS
        //
        // We intentionally keep this low while verifying OCR.
        // =====================================================

        if (
            currentTime - lastFrameSentTime < 500
>>>>>>> chavi
=======
            currentTime - lastFrameSentTime >= 500
>>>>>>> b6c402c708670b79c56057110760e27450e2af4a
        ) {

            image.close()

            return
        }

        lastFrameSentTime =
            currentTime


        try {

            // =================================================
<<<<<<< HEAD
            // 1. CONVERT FRAME TO JPEG
=======
            // 1. CONVERT CAMERA FRAME TO JPEG
>>>>>>> chavi
            // =================================================

            val jpegBytes =
                imageProxyToJpeg(image)


            // =================================================
            // 2. CREATE BITMAP COPY
<<<<<<< HEAD
=======
            //
            // OCR works on this Bitmap.
            // It does NOT directly use ImageProxy.
>>>>>>> chavi
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
<<<<<<< HEAD
            // OCR receives a Bitmap copy.
            // Therefore ImageProxy can be closed safely.
=======
            // imageProxyToJpeg() already rotates the image.
            // Therefore OCR rotation is 0.
>>>>>>> chavi
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

<<<<<<< HEAD
                    // Bitmap is no longer needed
=======

                    // =================================================
                    // Bitmap is no longer needed after ML Kit finishes.
                    // =================================================

>>>>>>> chavi
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
<<<<<<< HEAD
=======
            //
            // Existing pipeline continues to receive the frame.
>>>>>>> chavi
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
<<<<<<< HEAD
            // 5. EXISTING YOLO PIPELINE
=======
            // 5. SEND TO EXISTING PIPELINE
>>>>>>> chavi
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
<<<<<<< HEAD
            // 6. ALWAYS CLOSE IMAGEPROXY
=======
            // 6. CLOSE CAMERA IMAGE
            //
            // Safe because OCR is processing the separate Bitmap.
>>>>>>> chavi
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

<<<<<<< HEAD

        // Close OCR recognizer
        ocrReader.close()


        // Shutdown background executor
        analysisExecutor.shutdown()


=======
>>>>>>> chavi
        isRunning = false


        println(
            "CAMERA MANAGER >>> " +
                    "BACKGROUND CAMERA STOPPED"
        )
    }
}