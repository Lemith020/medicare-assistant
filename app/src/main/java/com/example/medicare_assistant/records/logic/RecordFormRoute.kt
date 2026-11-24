package com.example.medicare_assistant.records.logic

import android.content.ActivityNotFoundException
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.R
import com.example.medicare_assistant.records.view.RecordFormActions
import com.example.medicare_assistant.records.view.RecordFormScreen

@Composable
fun RecordFormRoute(
    editId: Long?,
    formKey: Int,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: RecordFormViewModel = viewModel(key = "record_form_$formKey")
    val state by viewModel.state.collectAsState()

    var cameraTarget by rememberSaveable { mutableStateOf<Uri?>(null) }

    LaunchedEffect(formKey) { viewModel.start(editId) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.onPhotoPicked(uri)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { taken ->
        val uri = cameraTarget
        if (taken && uri != null) viewModel.onPhotoPicked(uri)
    }

    if (state.loaded) {
        RecordFormScreen(
            state = state,
            actions = RecordFormActions(
                onBack = onBack,
                onCategoryChange = viewModel::onCategoryChange,
                onTitleChange = viewModel::onTitleChange,
                onSourceChange = viewModel::onSourceChange,
                onDateChange = viewModel::onDateChange,
                onNotesChange = viewModel::onNotesChange,
                onTakePhoto = {
                    val target = RecordImageHelper.newCameraTarget(context)
                    cameraTarget = target
                    try {
                        cameraLauncher.launch(target)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, R.string.edit_error_no_camera, Toast.LENGTH_SHORT).show()
                    }
                },
                onChoosePhoto = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemovePhoto = viewModel::onRemovePhoto,
                onSave = { viewModel.save(onSaved) }
            )
        )
    }
}