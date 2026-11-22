package com.example.medicare_assistant.profile.logic

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

sealed interface LocationResult {
    data class Success(val latitude: Double, val longitude: Double, val text: String) : LocationResult
    data object PermissionDenied : LocationResult
    data object Disabled : LocationResult
    data object Failed : LocationResult
}

object LocationHelper {

    fun hasPermission(context: Context): Boolean =
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    suspend fun fetch(context: Context, locale: Locale): LocationResult {
        val app = context.applicationContext
        if (!hasPermission(app)) return LocationResult.PermissionDenied

        val manager = app.getSystemService(LocationManager::class.java)
        if (!LocationManagerCompat.isLocationEnabled(manager)) return LocationResult.Disabled

        val location = withTimeoutOrNull(15_000L) { currentLocation(app) }
            ?: lastLocation(app)
            ?: return LocationResult.Failed

        val text = reverseGeocode(app, locale, location.latitude, location.longitude)
            ?: "%.4f, %.4f".format(Locale.US, location.latitude, location.longitude)

        return LocationResult.Success(location.latitude, location.longitude, text)
    }

    @SuppressLint("MissingPermission")
    private suspend fun currentLocation(context: Context): Location? = suspendCancellableCoroutine { cont ->
        val client = LocationServices.getFusedLocationProviderClient(context)
        val cancel = CancellationTokenSource()
        cont.invokeOnCancellation { cancel.cancel() }
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancel.token)
            .addOnSuccessListener { cont.resume(it) }
            .addOnFailureListener { cont.resume(null) }
    }

    @SuppressLint("MissingPermission")
    private suspend fun lastLocation(context: Context): Location? = suspendCancellableCoroutine { cont ->
        LocationServices.getFusedLocationProviderClient(context).lastLocation
            .addOnSuccessListener { cont.resume(it) }
            .addOnFailureListener { cont.resume(null) }
    }

    private suspend fun reverseGeocode(context: Context, locale: Locale, lat: Double, lng: Double): String? {
        if (!Geocoder.isPresent()) return null
        val geocoder = Geocoder(context, locale)

        val address: Address? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { cont ->
                geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        cont.resume(addresses.firstOrNull())
                    }

                    override fun onError(errorMessage: String?) {
                        cont.resume(null)
                    }
                })
            }
        } else {
            withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                runCatching { geocoder.getFromLocation(lat, lng, 1)?.firstOrNull() }.getOrNull()
            }
        }

        return address?.let {
            listOfNotNull(it.subLocality, it.locality ?: it.subAdminArea, it.adminArea)
                .filter { part -> part.isNotBlank() }
                .distinct()
                .take(2)
                .joinToString(", ")
                .ifBlank { null }
        }
    }
}