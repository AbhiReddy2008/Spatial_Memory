package com.example.spatialmemory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MemoryCard(
    title: String,
    location: String,
    time: String,
    icon: String = "▣",
    onClick: () -> Unit,
    onNavigate: (() -> Unit)? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF151B22),
                RoundedCornerShape(18.dp)
            )
            .clickable {
                onClick()
            }
            .padding(15.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        /*
         * Object icon
         */

        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    Color.White.copy(alpha = 0.06f),
                    RoundedCornerShape(13.dp)
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = icon,
                color = Color.White,
                fontSize = 21.sp
            )
        }


        Spacer(
            modifier = Modifier.size(13.dp)
        )


        /*
         * Memory information
         */

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.size(3.dp)
            )

            Text(
                text = location,
                color = Color(0xFF9CA7B5),
                fontSize = 12.sp
            )

            Text(
                text = time,
                color = Color(0xFF6F7B89),
                fontSize = 10.sp
            )
        }


        /*
         * Navigate button
         */

        if (onNavigate != null) {

            androidx.compose.material3.TextButton(
                onClick = onNavigate
            ) {

                Text(
                    text = "Navigate",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}