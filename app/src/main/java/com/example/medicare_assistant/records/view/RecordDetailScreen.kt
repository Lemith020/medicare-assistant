package com.example.medicare_assistant.records.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.core.formatDate
import com.example.medicare_assistant.profile.view.components.InfoCard
import com.example.medicare_assistant.profile.view.components.PageTopBar
import com.example.medicare_assistant.records.data.MedicalRecord
import com.example.medicare_assistant.records.view.components.CategoryTag
import com.example.medicare_assistant.records.view.components.RecordImage
import com.example.medicare_assistant.records.view.components.ZoomableImageDialog
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow

data class RecordDetailUiState(
    val loaded: Boolean = false,
    val record: MedicalRecord? = null
)

data class RecordDetailActions(
    val onBack: () -> Unit,
    val onEdit: () -> Unit,
    val onDelete: () -> Unit
)

@Composable
fun RecordDetailScreen(record: MedicalRecord, actions: RecordDetailActions) {
    val locale = LocalConfiguration.current.locales[0]
    var showZoom by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    val photoDescription = stringResource(R.string.record_photo_preview_desc)
    val zoomHint = stringResource(R.string.record_detail_photo_hint)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PageTopBar(
                onBack = actions.onBack,
                trailing = {
                    Button(
                        onClick = actions.onEdit,
                        modifier = Modifier.heightIn(min = 48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LuminousYellow, contentColor = Ink)
                    ) {
                        Text(
                            text = stringResource(R.string.action_edit),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )

            CategoryTag(record.category)

            Text(
                text = record.title,
                modifier = Modifier.semantics { heading() },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            InfoCard(label = stringResource(R.string.record_detail_source), value = record.source)
            InfoCard(label = stringResource(R.string.record_detail_date), value = formatDate(record.date, locale))
            if (record.notes.isNotBlank()) {
                InfoCard(label = stringResource(R.string.record_detail_notes), value = record.notes)
            }

            val shape = RoundedCornerShape(18.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, shape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                    .padding(12.dp)
            ) {
                if (record.imagePath != null) {
                    RecordImage(
                        path = record.imagePath,
                        maxSidePx = 1200,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 460.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(role = Role.Button) { showZoom = true }
                            .semantics { contentDescription = "$photoDescription. $zoomHint" }
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = zoomHint,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.record_detail_no_photo),
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }
            }

            OutlinedButton(
                onClick = { confirmDelete = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = stringResource(R.string.record_delete),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showZoom && record.imagePath != null) {
        ZoomableImageDialog(
            path = record.imagePath,
            description = photoDescription,
            onDismiss = { showZoom = false }
        )
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.record_delete_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.record_delete_message, record.title), fontSize = 16.sp) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    actions.onDelete()
                }) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}