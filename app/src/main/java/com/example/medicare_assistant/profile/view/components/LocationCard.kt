package com.example.medicare_assistant.profile.view.components

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R

@Composable
fun LocationCard(
    locationText: String,
    updatedAt: Long?,
    locating: Boolean,
    @StringRes message: Int?,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    val resources = LocalContext.current.resources
    val empty = locationText.isBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_location),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        Text(
            text = if (empty) stringResource(R.string.profile_not_set) else locationText,
            color = if (empty) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        if (updatedAt != null) {
            Text(
                text = relativeText(resources, updatedAt),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        if (message != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(message),
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = onRefresh,
            enabled = !locating,
            modifier = Modifier.heightIn(min = 48.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Text(
                text = stringResource(if (locating) R.string.profile_locating else R.string.profile_refresh_location),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

internal fun relativeText(resources: Resources, updatedAt: Long): String {
    val minutes = ((System.currentTimeMillis() - updatedAt) / 60_000L).coerceAtLeast(0L)
    return when {
        minutes < 1 -> resources.getString(R.string.location_updated_just_now)
        minutes < 60 -> resources.getQuantityString(
            R.plurals.location_updated_minutes, minutes.toInt(), minutes.toInt()
        )
        minutes < 1440 -> {
            val hours = (minutes / 60).toInt()
            resources.getQuantityString(R.plurals.location_updated_hours, hours, hours)
        }
        else -> {
            val days = (minutes / 1440).toInt()
            resources.getQuantityString(R.plurals.location_updated_days, days, days)
        }
    }
}