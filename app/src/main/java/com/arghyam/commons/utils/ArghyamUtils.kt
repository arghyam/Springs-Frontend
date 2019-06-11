package com.arghyam.commons.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.LocationManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_ON_RESULT_CODE
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.snackbar.Snackbar
import java.io.File
import androidx.core.content.ContextCompat.startActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat.startActivity




class ArghyamUtils {

    private var googleApiClient: GoogleApiClient? = null
    private lateinit var locationManager: LocationManager

    fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }

    fun permissionGranted(context: Context, permission: String): Int {
        return context.checkCallingOrSelfPermission(permission)
    }

    fun longToast(context: Context, title: String) {
        Toast.makeText(context, title, Toast.LENGTH_LONG).show()
    }

    fun shortToast(context: Context, title: String) {
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
    }

    fun openSettings(context: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivityForResult(intent, Constants.PERMISSION_LOCATION_RESULT_CODE)
    }

    fun turnOnLocation(activity: Activity) {
        if (googleApiClient == null) {
            googleApiClient =
                GoogleApiClient.Builder(activity.applicationContext).addApi(LocationServices.API).build()
            googleApiClient?.connect()
            var locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 30 * 1000
            locationRequest.fastestInterval = 5 * 1000
            var builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)
            var result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback {
                Log.e("location", "" + it.status.statusCode)
                when (it.status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> {

                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            it.status.startResolutionForResult(
                                activity,
                                PERMISSION_LOCATION_ON_RESULT_CODE
                            )
                        } catch (e: IntentSender.SendIntentException) {

                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }
                }
            }
        }
    }

    fun isLocationEnabled(activity: Activity): Boolean {
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun convertToString(data: Any): String {
        val ow = ObjectMapper().writer().withDefaultPrettyPrinter()
        return ow.writeValueAsString(data)
    }

    fun secondsToMinutes(timeInSeconds: Int): String {
        var minutes = timeInSeconds / 60
        var seconds = timeInSeconds % 60
        return "" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
    }

    fun makeSnackbar(view: View, message:String,action:String,activity: Context?,activityname:Class<*>){
        view?.let { it1 ->
            Snackbar.make(it1, message, Snackbar.LENGTH_LONG)
                .setAction(action) {
                    activity?.startActivity(Intent(activity, activityname))}.show()

        }
    }
}