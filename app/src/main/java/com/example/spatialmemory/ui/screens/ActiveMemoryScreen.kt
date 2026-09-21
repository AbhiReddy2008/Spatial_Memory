package com.example.spatialmemory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spatialmemory.camera.CameraInterface
import com.example.spatialmemory.session.MemorySessionManager
import com.example.spatialmemory.ui.components.MemoryStat
import com.example.spatialmemory.ui.components.StatusChip


@Composable
fun ActiveMemoryScreen(
    sessionManager: MemorySessionManager,
    onResumeCamera: () -> Unit,
    onStopSession: () -> Unit
) {

    // =========================================================
    // SESSION STATE
    // =========================================================

    val cameraPaused by
    sessionManager.cameraPaused.collectAsState()

    val sessionElapsedTime by
    sessionManager.sessionElapsedTime.collectAsState()


    // =========================================================
    // CAMERA / AI DEBUG STATE
    // =========================================================

    val debugState by
    CameraInterface.debugState.collectAsState()


    // =========================================================
    // FORMAT SESSION TIME
    // =========================================================

    val totalSeconds =
        sessionElapsedTime / 1000

    val hours =
        totalSeconds / 3600

    val minutes =
        (totalSeconds % 3600) / 60

    val seconds =
        totalSeconds % 60

    val formattedTime =
        String.format(
            "%02d:%02d:%02d",
            hours,
            minutes,
            seconds
        )


    // =========================================================
    // MAIN SCREEN
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF090D12)
            )
            .padding(
                horizontal = 16.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        // =====================================================
        // TOP BAR
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 18.dp,
                    bottom = 12.dp
                ),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "ACTIVE MEMORY",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        if (cameraPaused)
                            "Camera paused"
                        else
                            "Building spatial memory...",
                    color = Color(0xFF9CA7B5),
                    fontSize = 11.sp
                )
            }


            StatusChip(
                text =
                    if (cameraPaused)
                        "● PAUSED"
                    else
                        "● RECORDING",
                active = !cameraPaused
            )
        }


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // =====================================================
        // SESSION TIMER
        // =====================================================

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(22.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color(0xFF151B22)
                )
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 30.dp,
                            horizontal = 20.dp
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "SESSION TIME",
                    color = Color(0xFF7F8A98),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text = formattedTime,
                    color = Color(0xFF38BDF8),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        if (cameraPaused)
                            "Memory capture paused"
                        else
                            "Spatial memory capture active",

                    color =
                        Color(0xFF9CA7B5),

                    fontSize = 11.sp
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        // =====================================================
        // SYSTEM STATUS
        // =====================================================

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(18.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color(0xFF151B22)
                )
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
            ) {

                Text(
                    text = "SYSTEM STATUS",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    StatusChip(
                        text =
                            if (cameraPaused)
                                "● Camera Paused"
                            else
                                "● Camera Active",

                        active =
                            !cameraPaused
                    )

                    StatusChip(
                        text = "● IMU Active",
                        active = true
                    )

                    StatusChip(
                        text = "● AI Active",
                        active = true
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        // =====================================================
        // MEMORY PROCESSING
        // =====================================================

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(18.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color(0xFF151B22)
                )
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
            ) {

                Text(
                    text = "MEMORY PROCESSING",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    MemoryStat(
                        title = "Frames",
                        value =
                            debugState.framesProcessed
                                .toString()
                    )

                    MemoryStat(
                        title = "Objects",
                        value =
                            debugState.objectsDetected
                                .toString()
                    )

                    MemoryStat(
                        title = "Inference",
                        value =
                            "${debugState.inferenceTimeMs} ms"
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.weight(1f)
        )


        // =====================================================
        // CONTROLS
        // =====================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 18.dp
                    ),

            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {


            // =================================================
            // PAUSE / RESUME
            // =================================================

            Button(
                onClick = {

                    if (cameraPaused) {

                        onResumeCamera()

                    } else {

                        sessionManager.pauseCamera()
                    }
                },

                modifier =
                    Modifier
                        .weight(1f)
                        .height(54.dp),

                shape =
                    RoundedCornerShape(15.dp),

                colors =
                    ButtonDefaults.buttonColors(

                        containerColor =
                            if (cameraPaused)
                                Color(0xFF12301F)
                            else
                                Color(0xFF1E2935)
                    )
            ) {

                Text(
                    text =
                        if (cameraPaused)
                            "RESUME CAMERA"
                        else
                            "PAUSE CAMERA",

                    color =
                        if (cameraPaused)
                            Color(0xFF86EFAC)
                        else
                            Color.White,

                    fontSize = 12.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            // =================================================
            // STOP
            // =================================================

            Button(
                onClick = {
                    onStopSession()
                },

                modifier =
                    Modifier
                        .weight(0.55f)
                        .height(54.dp),

                shape =
                    RoundedCornerShape(15.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color(0xFF3A1618)
                    )
            ) {

                Text(
                    text = "STOP",

                    color =
                        Color(0xFFFCA5A5),

                    fontSize = 12.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}