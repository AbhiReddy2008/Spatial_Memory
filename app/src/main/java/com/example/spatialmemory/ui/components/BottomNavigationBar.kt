package com.example.spatialmemory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onHomeClick: () -> Unit,
    onMemoriesClick: () -> Unit,
    onFindClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF151B22))
            .padding(
                vertical = 9.dp,
                horizontal = 8.dp
            ),

        horizontalArrangement =
            Arrangement.SpaceEvenly,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        BottomNavigationItem(
            icon = "⌂",
            label = "Home",
            selected = selectedItem == "Home",
            onClick = onHomeClick
        )

        BottomNavigationItem(
            icon = "▣",
            label = "Memories",
            selected = selectedItem == "Memories",
            onClick = onMemoriesClick
        )

        BottomNavigationItem(
            icon = "⌕",
            label = "Find",
            selected = selectedItem == "Find",
            onClick = onFindClick
        )

        BottomNavigationItem(
            icon = "⚙",
            label = "Settings",
            selected = selectedItem == "Settings",
            onClick = onSettingsClick
        )
    }
}


@Composable
private fun BottomNavigationItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 12.dp,
                vertical = 4.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = icon,

            color =
                if (selected)
                    Color(0xFF38BDF8)
                else
                    Color(0xFF9CA7B5),

            fontSize = 21.sp
        )

        Text(
            text = label,

            color =
                if (selected)
                    Color(0xFF38BDF8)
                else
                    Color(0xFF9CA7B5),

            fontSize = 10.sp,

            fontWeight =
                if (selected)
                    FontWeight.Bold
                else
                    FontWeight.Normal
        )
    }
}