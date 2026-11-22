package com.example.medicare_assistant.profile.logic

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.profile.view.ProfileActions
import com.example.medicare_assistant.profile.view.ProfileScreen

@Composable
fun ProfileRoute(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onOpenAllergies: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        if (granted.values.any { it }) viewModel.refreshLocation() else viewModel.onLocationPermissionDenied()
    }

    if (state.loaded) {
        ProfileScreen(
            state = state,
            actions = ProfileActions(


                onBack = onBack,


                onEdit = onEdit,


                onRefreshLocation = {
                    if (LocationHelper.hasPermission(context)) {
                        viewModel.refreshLocation()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },


                onAddAllergy = onOpenAllergies
            )
        )
    }
}