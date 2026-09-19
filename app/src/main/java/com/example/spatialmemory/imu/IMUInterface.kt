package com.example.spatialmemory.imu

object IMUInterface {

    private var lastLogTime = 0L

    private var eventCount = 0

    private var lastType: MotionType? = null


    fun onMotionEvent(event: MotionEvent) {

        /*
         * This is where the complete IMU stream
         * is available to the future spatial model.
         *
         * DO NOT remove this function call.
         */


        eventCount++

        val currentTime =
            System.currentTimeMillis()


        /*
         * Log only once every second.
         */

        if (currentTime - lastLogTime >= 1000) {

            lastLogTime = currentTime

            println(
                "IMU >>> " +
                        "events=$eventCount " +
                        "latest=${event.type} " +
                        "value=${"%.3f".format(event.value)} " +
                        "source=${event.source}"
            )
        }


        /*
         * The actual model connection will
         * eventually go here.
         *
         * For now the MotionEvent is simply
         * available as the function argument.
         */
    }
}