package com.example.spatialmemory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.compose.runtime.LaunchedEffect

import com.example.spatialmemory.camera.CameraManager
import com.example.spatialmemory.imu.IMUManager


class MainActivity : ComponentActivity() {

    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                setContent {
                    SpatialMemoryApp()
                }
            }
        }


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        if (
            checkSelfPermission(
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            setContent {
                SpatialMemoryApp()
            }

        } else {

            cameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }
}


/* ============================================================
   MAIN APP
   ============================================================ */

@androidx.compose.runtime.Composable
fun SpatialMemoryApp() {

    var sessionStarted by remember {
        mutableStateOf(false)
    }

    var cameraPaused by remember {
        mutableStateOf(false)
    }


    if (!sessionStarted) {

        StartScreen(
            onStart = {
                sessionStarted = true
            }
        )

    } else {

        ActiveSessionScreen(
            cameraPaused = cameraPaused,

            onPauseCamera = {
                cameraPaused = true
            },

            onResumeCamera = {
                cameraPaused = false
            },

            onStopSession = {
                sessionStarted = false
                cameraPaused = false
            }
        )
    }
}


/* ============================================================
   START SCREEN
   ============================================================ */

@androidx.compose.runtime.Composable
fun StartScreen(
    onStart: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF0B0F14)
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = "SPATIAL MEMORY",
                color = Color.White,
                fontSize = 30.sp
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            Text(
                text =
                    "Your memory of the physical world",

                color = Color.LightGray,

                fontSize = 15.sp
            )


            Spacer(
                modifier = Modifier.height(50.dp)
            )


            Button(
                onClick = onStart,

                modifier = Modifier
                    .size(
                        width = 220.dp,
                        height = 60.dp
                    ),

                shape =
                    RoundedCornerShape(18.dp)
            ) {

                Text(
                    text = "START",
                    fontSize = 20.sp
                )
            }
        }
    }
}


/* ============================================================
   ACTIVE SESSION
   ============================================================ */

@androidx.compose.runtime.Composable
fun ActiveSessionScreen(
    cameraPaused: Boolean,
    onPauseCamera: () -> Unit,
    onResumeCamera: () -> Unit,
    onStopSession: () -> Unit
) {

    val context =
        LocalContext.current


    val lifecycleOwner =
        androidx.lifecycle.compose.LocalLifecycleOwner.current


    val cameraManager =
        remember {
            CameraManager(context)
        }


    val imuManager =
        remember {
            IMUManager(context)
        }


    /*
     * Start IMU when active session begins.
     */

    DisposableEffect(Unit) {

        imuManager.startSensors()

        onDispose {

            imuManager.stopSensors()

            cameraManager.stopCamera()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF0B0F14)
            )
    ) {


        /* ====================================================
           HEADER
           ==================================================== */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                ),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "SPATIAL MEMORY",
                color = Color.White,
                fontSize = 21.sp
            )


            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            if (cameraPaused)
                                Color.Gray
                            else
                                Color.Green,

                            RoundedCornerShape(50)
                        )
                )


                Spacer(
                    modifier = Modifier.size(7.dp)
                )


                Text(
                    text =
                        if (cameraPaused)
                            "CAMERA PAUSED"
                        else
                            "ACTIVE",

                    color = Color.LightGray,

                    fontSize = 13.sp
                )
            }
        }


        /* ====================================================
           CAMERA
           ==================================================== */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    horizontal = 12.dp
                ),

            contentAlignment =
                Alignment.Center
        ) {

            if (!cameraPaused) {

                CameraPreview(
                    cameraManager =
                        cameraManager,

                    lifecycleOwner =
                        lifecycleOwner
                )

            } else {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color(0xFF171D24),
                            RoundedCornerShape(20.dp)
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "CAMERA PAUSED",

                            color = Color.White,

                            fontSize = 24.sp
                        )


                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )


                        Text(
                            text =
                                "Visual processing is temporarily disabled.",

                            color =
                                Color.LightGray,

                            fontSize = 14.sp
                        )
                    }
                }
            }
        }


        /* ====================================================
           CONTROLS
           ==================================================== */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {


            Button(
                onClick = {

                    if (cameraPaused) {

                        onResumeCamera()

                    } else {

                        cameraManager.stopCamera()

                        onPauseCamera()
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                shape =
                    RoundedCornerShape(16.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            if (cameraPaused)
                                Color(0xFF2563EB)
                            else
                                Color(0xFF374151)
                    )
            ) {

                Text(
                    text =
                        if (cameraPaused)
                            "RESUME CAMERA"
                        else
                            "PAUSE CAMERA",

                    fontSize = 17.sp
                )
            }


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            Button(
                onClick = {

                    cameraManager.stopCamera()

                    imuManager.stopSensors()

                    onStopSession()
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                shape =
                    RoundedCornerShape(16.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color(0xFF7F1D1D)
                    )
            ) {

                Text(
                    text = "STOP SESSION"
                )
            }
        }
    }
}


/* ============================================================
   CAMERA PREVIEW
   ============================================================ */

@androidx.compose.runtime.Composable
fun CameraPreview(
    cameraManager: CameraManager,
    lifecycleOwner: LifecycleOwner
) {

    val context =
        LocalContext.current


    val previewView =
        remember {
            PreviewView(context)
        }


    /*
     * Start camera when this screen
     * becomes visible.
     */

    LaunchedEffect(Unit) {

        cameraManager.startCamera(
            lifecycleOwner =
                lifecycleOwner,

            previewView =
                previewView
        )
    }


    AndroidView(
        factory = {
            previewView
        },

        modifier =
            Modifier.fillMaxSize()
    )
}