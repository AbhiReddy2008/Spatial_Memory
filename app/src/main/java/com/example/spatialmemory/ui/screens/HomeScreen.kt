package com.example.spatialmemory.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spatialmemory.ui.components.ActionCard
import com.example.spatialmemory.ui.components.BottomNavigationBar

private val Background = Color(0xFF090D12)
private val CardColor = Color(0xFF151B22)
private val CardColor2 = Color(0xFF1B232D)
private val Primary = Color(0xFF38BDF8)
private val TextPrimary = Color(0xFFF5F7FA)
private val TextSecondary = Color(0xFF9CA7B5)
private val Green = Color(0xFF4ADE80)


@Composable
fun HomeScreen(
    onStartMemory: () -> Unit,
    onFindSomething: () -> Unit,
    onMemories: () -> Unit,
    onSettings: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // =====================================================
        // TOP BAR
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                ),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "SPATIAL",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "MEMORY",
                    color = Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .background(
                            Green,
                            RoundedCornerShape(50)
                        )
                )

                Spacer(
                    modifier = Modifier.size(7.dp)
                )

                Text(
                    text = "SYSTEM READY",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }


        // =====================================================
        // MAIN CONTENT
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            Text(
                text = "Remember your",
                color = TextSecondary,
                fontSize = 19.sp
            )

            Text(
                text = "physical world.",
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            Text(
                text =
                    "Your phone remembers where you saw\n" +
                            "objects and helps you find them later.",

                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            // =================================================
            // START MEMORY CARD
            // =================================================

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(24.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor = CardColor2
                    )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Primary.copy(
                                        alpha = 0.15f
                                    ),
                                    RoundedCornerShape(15.dp)
                                ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text = "●",
                                color = Primary,
                                fontSize = 20.sp
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.size(14.dp)
                        )


                        Column {

                            Text(
                                text =
                                    "Ready to remember?",

                                color =
                                    TextPrimary,

                                fontSize = 18.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    "Camera + IMU will start recording",

                                color =
                                    TextSecondary,

                                fontSize = 12.sp
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )


                    Button(
                        onClick = onStartMemory,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),

                        shape =
                            RoundedCornerShape(15.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    Primary
                            )
                    ) {

                        Text(
                            text = "START MEMORY",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            // =================================================
            // FIND SOMETHING
            // =================================================

            ActionCard(
                title = "Find Something",
                subtitle =
                    "Find a remembered object or place",

                icon = "⌕",

                onClick =
                    onFindSomething
            )


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // =================================================
            // MY MEMORIES
            // =================================================

            ActionCard(
                title = "My Memories",

                subtitle =
                    "12 objects and locations remembered",

                icon = "▣",

                onClick =
                    onMemories
            )


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // =================================================
            // RECENT ROUTE
            // =================================================

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            CardColor
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(17.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                Green.copy(
                                    alpha = 0.12f
                                ),
                                RoundedCornerShape(13.dp)
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "↗",
                            color = Green,
                            fontSize = 20.sp
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.size(13.dp)
                    )


                    Column {

                        Text(
                            text = "Recent Route",

                            color =
                                TextPrimary,

                            fontSize = 15.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Main Entrance → Classroom",

                            color =
                                TextSecondary,

                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}