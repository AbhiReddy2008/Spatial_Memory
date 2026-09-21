package com.example.spatialmemory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusChip(
    text: String,
    active: Boolean = true
) {

    Box(
        modifier = Modifier
            .background(
                color = if (active) {
                    Color(0xFF12301F)
                } else {
                    Color(0xFF252A30)
                },
                shape = RoundedCornerShape(10.dp)
            )
            .padding(
                horizontal = 8.dp,
                vertical = 5.dp
            )
    ) {

        Text(
            text = text,

            color = if (active) {
                Color(0xFF86EFAC)
            } else {
                Color(0xFF9CA3AF)
            },

            fontSize = 9.sp
        )
    }
}