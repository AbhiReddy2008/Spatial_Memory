package com.example.spatialmemory.camera

object CameraInterface {

    private var frameCount = 0

    private var lastLogTime = 0L

    fun onFrame(frame: CameraFrame) {

        frameCount++

        val currentTime =
            System.currentTimeMillis()

        /*
         * Log only once every second.
         */

        if (currentTime - lastLogTime >= 1000) {

            lastLogTime = currentTime

            println(
                "CAMERA INTERFACE >>> " +
                        "frames=$frameCount " +
                        "imageBytes=${frame.imageData.size} " +
                        "size=${frame.width}x${frame.height} " +
                        "rotation=${frame.rotationDegrees}"
            )
        }

        /*
         * The actual image is available here:
         *
         * frame.imageData
         *
         * This ByteArray contains the JPEG image.
         *
         * Chavi's CV module will eventually
         * consume this data.
         */
    }
}