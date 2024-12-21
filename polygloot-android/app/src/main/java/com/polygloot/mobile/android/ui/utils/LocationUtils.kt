package com.polygloot.mobile.android.ui.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.LocaleList
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.polygloot.mobile.android.ui.utils.Consts.Companion.REQUEST_CODE_GOOGLE_PLAY_SERVICES
import com.polygloot.mobile.android.ui.utils.Consts.Companion.REQUEST_CODE_LOCATION_SETTINGS
import com.polygloot.mobile.android.ui.utils.Consts.Companion.countryToSupportedLanguage
import java.util.AbstractMap.SimpleEntry
import java.util.Locale

class LocationUtils(
    private val activity: ComponentActivity,
    private val targetLanguageCallBack: (Map.Entry<String, String>?) -> Unit,
    private val sourceLanguageCallback: (Map.Entry<String, String>?) -> Unit,
) {

    private var isLocationRetrieved: Boolean = false

    init {
        getCurrentLocation()
        getConfigLocation()
    }

    companion object {
        fun isLocationPermissionGranted(context: Context) = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    private fun getCurrentLocation() {
        if (!isLocationPermissionGranted(activity) && !isGooglePlayServicesAvailable()) {
            Log.e("LocationUtils", "Google Play Services are not available.")
            return
        }

        checkLocationSettings {
            retrieveLastLocation()
        }
    }

    private fun getConfigLocation() {
        LocaleList.getDefault()[0]?.let {
            sourceLanguageCallback(SimpleEntry(it.isO3Language, it.displayLanguage))
        }
    }


    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val result = googleApiAvailability.isGooglePlayServicesAvailable(activity)
        if (result != ConnectionResult.SUCCESS) {
            googleApiAvailability.getErrorDialog(
                activity,
                result,
                REQUEST_CODE_GOOGLE_PLAY_SERVICES
            )?.show()
            return false
        }
        return true
    }

    private fun checkLocationSettings(onSuccess: () -> Unit) {
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(activity)

        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    exception.startResolutionForResult(activity, REQUEST_CODE_LOCATION_SETTINGS)
                } else {
                    Log.e(
                        "LocationUtils",
                        "Location settings are not satisfied: ${exception.message}"
                    )
                }
            }
    }

    private fun retrieveLastLocation() {
        if (isLocationRetrieved) return

        fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
            if (location != null) {
                Log.d("LocationUtils", "Lat: ${location.latitude}, Lng: ${location.longitude}")
                targetLanguageCallBack(getLanguageAndISO639OfCountry(location))
                isLocationRetrieved = true
            } else {
                Log.d(
                    "LocationUtils",
                    "No last known location available. Consider requesting location updates."
                )
                targetLanguageCallBack(null)
            }
        }
            ?.addOnFailureListener { exception ->
                Log.e("LocationUtils", "Error retrieving location: ${exception.message}")
                targetLanguageCallBack(null)
            }
    }

    private fun getLanguageAndISO639OfCountry(location: Location): Map.Entry<String, String>? {
        val geocoder = Geocoder(activity, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val countryCode = addresses?.firstOrNull()?.countryCode ?: return null
        return countryToSupportedLanguage(countryCode)
    }
}