package com.example.spatialmemory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import com.example.spatialmemory.ui.components.BottomNavigationBar
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.collectAsState
import com.example.spatialmemory.ui.components.SessionStatusBar

import com.example.spatialmemory.ui.screens.ActiveMemoryScreen
import com.example.spatialmemory.ui.screens.HomeScreen
import com.example.spatialmemory.session.MemorySessionManager


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            checkSelfPermission(Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
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

@Composable
fun SpatialMemoryApp() {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // =========================================================
    // SESSION MANAGER
    // =========================================================

    val sessionManager = remember {
        MemorySessionManager(context)
    }


    // =========================================================
    // CURRENT SCREEN
    // =========================================================

    var currentScreen by remember {
        mutableStateOf("Home")
    }


    // =========================================================
    // SESSION STATE
    // =========================================================

    val isSessionActive by
    sessionManager.isSessionActive.collectAsState()

    val cameraPaused by
    sessionManager.cameraPaused.collectAsState()

    val sessionElapsedTime by
    sessionManager.sessionElapsedTime.collectAsState()


    // =========================================================
    // MAIN APP LAYOUT
    // =========================================================

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // =====================================================
        // CURRENT SCREEN
        // =====================================================

        Box(
            modifier = Modifier.weight(1f)
        ) {

            when (currentScreen) {

                // =================================================
                // HOME
                // =================================================

                "Home" -> {

                    HomeScreen(

                        onStartMemory = {

                            sessionManager.startSession(
                                lifecycleOwner
                            )

                            currentScreen = "Session"
                        },

                        onFindSomething = {

                            currentScreen = "Find"
                        },

                        onMemories = {

                            currentScreen = "Memories"
                        },

                        onSettings = {

                            currentScreen = "Settings"
                        }
                    )
                }


                // =================================================
                // ACTIVE MEMORY
                // =================================================

                "Session" -> {

                    ActiveMemoryScreen(

                        sessionManager =
                            sessionManager,

                        onResumeCamera = {

                            sessionManager.resumeCamera(
                                lifecycleOwner
                            )
                        },

                        onStopSession = {

                            sessionManager.stopSession()

                            currentScreen = "Home"
                        }
                    )
                }


                // =================================================
                // FIND
                // =================================================

                "Find" -> {

                    HomeScreen(

                        onStartMemory = {

                            if (!isSessionActive) {

                                sessionManager.startSession(
                                    lifecycleOwner
                                )
                            }

                            currentScreen = "Session"
                        },

                        onFindSomething = {},

                        onMemories = {

                            currentScreen = "Memories"
                        },

                        onSettings = {

                            currentScreen = "Settings"
                        }
                    )
                }


                // =================================================
                // MEMORIES
                // =================================================

                "Memories" -> {

                    HomeScreen(

                        onStartMemory = {

                            if (!isSessionActive) {

                                sessionManager.startSession(
                                    lifecycleOwner
                                )
                            }

                            currentScreen = "Session"
                        },

                        onFindSomething = {

                            currentScreen = "Find"
                        },

                        onMemories = {},

                        onSettings = {

                            currentScreen = "Settings"
                        }
                    )
                }


                // =================================================
                // SETTINGS
                // =================================================

                "Settings" -> {

                    HomeScreen(

                        onStartMemory = {

                            if (!isSessionActive) {

                                sessionManager.startSession(
                                    lifecycleOwner
                                )
                            }

                            currentScreen = "Session"
                        },

                        onFindSomething = {

                            currentScreen = "Find"
                        },

                        onMemories = {

                            currentScreen = "Memories"
                        },

                        onSettings = {}
                    )
                }
            }
        }


        // =========================================================
        // PERSISTENT SESSION BAR
        // =========================================================

        if (isSessionActive) {

            SessionStatusBar(

                elapsedTime =
                    sessionElapsedTime,

                cameraPaused =
                    cameraPaused,

                onPauseClick = {

                    if (cameraPaused) {

                        sessionManager.resumeCamera(
                            lifecycleOwner
                        )

                    } else {

                        sessionManager.pauseCamera()
                    }
                }
            )
        }


        // =========================================================
        // BOTTOM NAVIGATION
        // =========================================================

        BottomNavigationBar(

            selectedItem =
                when (currentScreen) {

                    "Home" -> "Home"

                    "Memories" -> "Memories"

                    "Find" -> "Find"

                    "Settings" -> "Settings"

                    "Session" -> "Session"

                    else -> "Home"
                },

            onHomeClick = {

                currentScreen = "Home"
            },

            onMemoriesClick = {

                currentScreen = "Memories"
            },

            onFindClick = {

                currentScreen = "Find"
            },

            onSettingsClick = {

                currentScreen = "Settings"
            }
        )
    }
}