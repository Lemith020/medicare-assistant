package com.example.medicare_assistant.profile.view.components

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
fun LocationSetCard(
    locationText: String,
    updatedAt: Long?,
    locating: Boolean,
    @StringRes message: Int?,
    onUseCurrentLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    val resources = LocalContext.current.resources

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.edit_location_title),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.edit_location_note),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        if (locationText.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = locationText,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (updatedAt != null) {
                Text(
                    text = relativeText(resources, updatedAt),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
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
            onClick = onUseCurrentLocation,
            enabled = !locating,
            modifier = Modifier.heightIn(min = 48.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Text(
                text = stringResource(if (locating) R.string.profile_locating else R.string.edit_use_location),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}