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
import com.example.spatialmemory.camera.CameraInterface

import com.example.spatialmemory.camera.CameraManager
import com.example.spatialmemory.imu.IMUManager

import com.example.spatialmemory.detection.ObjectDetector
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import android.graphics.Paint
import android.graphics.RectF
import com.example.spatialmemory.detection.Detection
import com.example.spatialmemory.detection.DetectionOverlay


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

    CameraInterface.initialize(context)

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
        

        DetectionDebugPanel()

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


    val detections by CameraInterface.detections.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

    AndroidView(
        factory = {
            previewView
        },

        modifier = Modifier.fillMaxSize()
    )

    DetectionOverlay(
        detections = detections,
        modifier = Modifier.fillMaxSize()
    )
}
}



@androidx.compose.runtime.Composable
fun DetectionDebugPanel() {

    val state by CameraInterface.debugState.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF171D24)
        ),

        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = "SPATIAL MEMORY — DEBUG",

                color = Color.White,

                fontSize = 17.sp,

                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            DebugRow(
                label = "Camera",
                value = state.cameraStatus
            )

            DebugRow(
                label = "YOLO26",
                value = state.modelStatus
            )

            DebugRow(
                label = "Inference",
                value = state.inferenceStatus
            )

            DebugRow(
                label = "Frames processed",
                value = state.framesProcessed.toString()
            )

            DebugRow(
                label = "Objects detected",
                value = state.objectsDetected.toString()
            )

            DebugRow(
                label = "Inference time",
                value = "${state.inferenceTimeMs} ms"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Detected objects:",

                color = Color.LightGray,

                fontSize = 14.sp
            )

            Text(
                text = if (state.detectedLabels.isEmpty()) {
                    "No detections yet"
                } else {
                    state.detectedLabels.joinToString(", ")
                },

                color = Color.Green,

                fontSize = 14.sp
            )

            if (state.errorMessage.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Error: ${state.errorMessage}",

                    color = Color.Red,

                    fontSize = 12.sp
                )
            }
        }
    }
}


@androidx.compose.runtime.Composable
fun DebugRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),

        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,

            color = Color.LightGray,

            fontSize = 13.sp
        )

        Text(
            text = value,

            color = Color.White,

            fontSize = 13.sp
        )
    }
}

@androidx.compose.runtime.Composable
fun DetectionOverlay(
    detections: List<Detection>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {

        val boxPaint = Paint().apply {
            color = android.graphics.Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 4f
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 36f
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val backgroundPaint = Paint().apply {
            color = android.graphics.Color.GREEN
            style = Paint.Style.FILL
        }

        detections.forEach { detection ->

            // Coordinates are normalized between 0 and 1.
            val left = detection.left * size.width
            val top = detection.top * size.height
            val right = detection.right * size.width
            val bottom = detection.bottom * size.height

            drawRect(
                color = Color.Green,
                topLeft = Offset(left, top),
                size = Size(
                    width = right - left,
                    height = bottom - top
                ),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 3.dp.toPx()
                )
            )

            drawIntoCanvas { canvas ->

                val nativeCanvas = canvas.nativeCanvas

                val label =
                    "${detection.label} " +
                    "${(detection.confidence * 100).toInt()}%"

                val textWidth =
                    textPaint.measureText(label)

                val labelTop =
                    (top - 42f).coerceAtLeast(0f)

                nativeCanvas.drawRect(
                    RectF(
                        left,
                        labelTop,
                        left + textWidth + 16f,
                        labelTop + 44f
                    ),
                    backgroundPaint
                )

                nativeCanvas.drawText(
                    label,
                    left + 8f,
                    labelTop + 34f,
                    textPaint
                )
            }
        }
    }
}

