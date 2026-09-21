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
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
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
    
    private val analysisExecutor = Executors.newSingleThreadExecutor()


    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {

        if (isRunning) {
            return
        }

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val provider =
                cameraProviderFuture.get()

            cameraProvider = provider


            val preview =
                Preview.Builder()
                    .build()

            preview.setSurfaceProvider(
                previewView.surfaceProvider
            )


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


            val cameraSelector =
                CameraSelector.DEFAULT_BACK_CAMERA


            try {

                provider.unbindAll()

                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

                isRunning = true

                println(
                    "CAMERA MANAGER >>> CAMERA STARTED"
                )

            } catch (e: Exception) {

                println(
                    "CAMERA MANAGER >>> ERROR: ${e.message}"
                )
            }

        }, ContextCompat.getMainExecutor(context))
    }


    private fun processFrame(
        image: ImageProxy
    ) {

        frameCount++

        val currentTime =
            System.currentTimeMillis()


        /*
         * Log camera status once every second.
         */

        if (currentTime - lastLogTime >= 1000) {

            lastLogTime = currentTime

            println(
                "CAMERA >>> " +
                        "frames=$frameCount " +
                        "size=${image.width}x${image.height}"
            )
        }


        /*
         * Send approximately 5 frames per second.
         */

        if (currentTime - lastFrameSentTime >= 200) {

            lastFrameSentTime = currentTime

            try {

                /*
                 * Convert CameraX YUV frame
                 * into JPEG bytes.
                 */

                val jpegBytes =
                    imageProxyToJpeg(image)


                val frame =
                    CameraFrame(
                        imageData = jpegBytes,

                        timestamp =
                            image.imageInfo.timestamp,

                        width =
                            image.width,

                        height =
                            image.height,

                        rotationDegrees =
                            image.imageInfo.rotationDegrees
                    )


                /*
                 * Send the complete frame
                 * through our camera interface.
                 */

                CameraInterface.onFrame(frame)

            } catch (e: Exception) {

                println(
                    "CAMERA >>> FRAME CONVERSION ERROR: " +
                            e.message
                )
            }
        }


        /*
         * VERY IMPORTANT:
         * ImageProxy must always be closed.
         */

        image.close()
    }


    private fun imageProxyToJpeg(
        image: ImageProxy
    ): ByteArray {

        /*
         * CameraX ImageAnalysis normally
         * provides YUV_420_888.
         */

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
                ySize + uSize + vSize
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


        /*
         * Apply CameraX rotation so that
         * the image data matches the
         * camera orientation.
         */

        val rotation =
            image.imageInfo.rotationDegrees


        if (rotation != 0) {

            val bitmap =
                BitmapFactory.decodeByteArray(
                    jpegBytes,
                    0,
                    jpegBytes.size
                )


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


        outputStream.close()

        return jpegBytes
    }


    fun stopCamera() {

        if (!isRunning) {
            return
        }


        cameraProvider?.unbindAll()

        imageAnalysis?.clearAnalyzer()

        imageAnalysis = null

        isRunning = false


        println(
            "CAMERA MANAGER >>> CAMERA STOPPED"
        )
    }
}