package com.example.medicare_assistant.emergency

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberEmergencyCaller(): (String) -> Unit {
    val context = LocalContext.current
    var pendingNumber by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val number = pendingNumber
        pendingNumber = null
        if (!number.isNullOrBlank()) {
            if (isGranted) {
                placeCall(context, number, directCall = true)
            } else {
                placeCall(context, number, directCall = false)
            }
        }
    }

    return { phoneNumber ->
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            placeCall(context, phoneNumber, directCall = true)
        } else {
            pendingNumber = phoneNumber
            permissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }
}

private fun placeCall(context: Context, phoneNumber: String, directCall: Boolean) {
    val sanitized = phoneNumber.filter { it.isDigit() || it == '+' }
    if (sanitized.isEmpty()) return

    val action = if (directCall) Intent.ACTION_CALL else Intent.ACTION_DIAL
    val intent = Intent(action, Uri.parse("tel:$sanitized")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        val fallback = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$sanitized")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(fallback)
    }
}