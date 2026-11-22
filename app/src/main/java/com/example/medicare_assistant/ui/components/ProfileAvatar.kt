package com.example.medicare_assistant.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 104.dp,
    fontSize: TextUnit = 38.sp
) {
    val initial = name.trim().firstOrNull()?.uppercase() ?: "?"

    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = Color(0xFFFCD815),
        border = BorderStroke(3.dp, Color(0xFF1E1B0E))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = initial,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1B0E)
            )
        }
    }
}

@Composable
fun ProfileAvatar(
    initial: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    diameter: Dp = 52.dp,
    onClick: (() -> Unit)? = null
) {
    val charText = initial ?: "N"
    val baseModifier = if (onClick != null) {
        modifier.clickable(role = Role.Button, onClick = onClick).semantics { this.contentDescription = contentDescription }
    } else {
        modifier.semantics { this.contentDescription = contentDescription }
    }
    ProfileAvatar(
        name = charText,
        modifier = baseModifier,
        size = diameter,
        fontSize = (diameter.value * 0.4f).sp
    )
}
