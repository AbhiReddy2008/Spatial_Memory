package com.example.spatialmemory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
fun ActionCard(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit
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
            .padding(17.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(44.dp)
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

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = Color(0xFF9CA7B5),
                fontSize = 12.sp
            )
        }

        Text(
            text = "›",
            color = Color(0xFF9CA7B5),
            fontSize = 24.sp
        )
    }
}