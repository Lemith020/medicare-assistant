package com.example.medicare_assistant.profile.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.ui.theme.statusColors

@Composable
fun AllergyChip(name: String, modifier: Modifier = Modifier) {
    val colors = statusColors()
    Text(
        text = name,
        modifier = modifier
            .background(colors.badBackground, CircleShape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        color = colors.badText,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )
}