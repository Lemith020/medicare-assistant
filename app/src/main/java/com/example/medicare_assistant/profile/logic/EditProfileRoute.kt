package com.example.medicare_assistant.profile.logic

import android.Manifest
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
import com.example.medicare_assistant.profile.view.EditProfileActions
import com.example.medicare_assistant.profile.view.EditProfileScreen

@Composable
fun EditProfileRoute(
    formKey: Int,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current


    val viewModel: EditProfileViewModel = viewModel(key = "edit_profile_$formKey")
    val state by viewModel.state.collectAsState()

    var cameraTarget by rememberSaveable { mutableStateOf<Uri?>(null) }

    LaunchedEffect(formKey) { viewModel.start() }


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


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        if (granted.values.any { it }) viewModel.useCurrentLocation() else viewModel.onLocationPermissionDenied()
    }

    if (state.loaded) {
        EditProfileScreen(
            state = state,
            actions = EditProfileActions(


                onBack = onBack,


                onTakePhoto = {
                    val target = PhotoHelper.newCameraTarget(context)
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

                // ===== [Full name field] =====
                onNameChange = viewModel::onNameChange,

                // ===== [Age field] =====
                onAgeChange = viewModel::onAgeChange,

                // ===== [Phone number field] =====
                onPhoneChange = viewModel::onPhoneChange,

                // ===== [Address field] =====
                onAddressChange = viewModel::onAddressChange,


                onUseCurrentLocation = {
                    if (LocationHelper.hasPermission(context)) {
                        viewModel.useCurrentLocation()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },


                onSave = { viewModel.save(onSaved) }
            )
        )
    }
}