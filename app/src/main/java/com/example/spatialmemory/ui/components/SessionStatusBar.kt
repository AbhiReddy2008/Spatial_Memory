package com.example.spatialmemory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable


@Composable
fun SessionStatusBar(
    elapsedTime: Long,
    cameraPaused: Boolean,
    onPauseClick: () -> Unit
) {

    val totalSeconds =
        elapsedTime / 1000

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


    Row(

        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    if (cameraPaused)
                        Color(0xFF252A30)
                    else
                        Color(0xFF12301F),

                shape =
                    RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 14.dp,
                vertical = 10.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically,

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {


        // =====================================================
        // LEFT SIDE
        // =====================================================

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text =
                    if (cameraPaused)
                        "●"
                    else
                        "●",

                color =
                    if (cameraPaused)
                        Color(0xFFFBBF24)
                    else
                        Color(0xFF4ADE80),

                fontSize = 12.sp
            )


            Text(
                text =
                    if (cameraPaused)
                        " MEMORY PAUSED"
                    else
                        " MEMORY ACTIVE",

                color =
                    Color.White,

                fontSize = 11.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }


        // =====================================================
        // TIMER
        // =====================================================

        Text(
            text = formattedTime,

            color =
                Color(0xFFBAE6FD),

            fontSize = 12.sp,

            fontWeight =
                FontWeight.Bold
        )


        // =====================================================
        // PAUSE / RESUME
        // =====================================================

        androidx.compose.material3.TextButton(
            onClick = onPauseClick
        ) {

            Text(
                text =
                    if (cameraPaused)
                        "RESUME"
                    else
                        "PAUSE",

                color =
                    if (cameraPaused)
                        Color(0xFF86EFAC)
                    else
                        Color(0xFFBAE6FD),

                fontSize = 10.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}