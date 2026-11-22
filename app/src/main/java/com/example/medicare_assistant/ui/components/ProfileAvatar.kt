package com.example.medicare_assistant.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfileAvatar(
    initial: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    photoPath: String? = null,
    diameter: Dp = 52.dp,
    onClick: (() -> Unit)? = null
) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, photoPath) {
        value = if (photoPath == null) {
            null
        } else {
            withContext(Dispatchers.IO) { BitmapFactory.decodeFile(photoPath)?.asImageBitmap() }
        }
    }

    val base = modifier
        .size(diameter)
        .clip(CircleShape)
        .background(LuminousYellow)
        .border(3.dp, Ink, CircleShape)

    val clickable = if (onClick != null) base.clickable(role = Role.Button, onClick = onClick) else base

    Box(
        modifier = clickable.semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        val image = bitmap
        when {
            image != null -> Image(
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            initial.isNullOrBlank() -> Canvas(Modifier.size(diameter * 0.55f)) {
                val w = size.width
                val h = size.height
                drawCircle(Ink, radius = w * 0.22f, center = Offset(w / 2f, h * 0.3f))
                drawArc(
                    color = Ink,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(w * 0.1f, h * 0.62f),
                    size = Size(w * 0.8f, h * 0.8f)
                )
            }
            else -> Text(
                text = initial,
                color = Ink,
                fontSize = (diameter.value * 0.4f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}