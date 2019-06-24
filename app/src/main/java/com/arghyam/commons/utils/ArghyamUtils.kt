package com.arghyam.commons.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_ON_RESULT_CODE
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginTop
import com.androidadvance.topsnackbar.TSnackbar
import com.arghyam.R
import kotlinx.android.synthetic.main.activity_main.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


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


    fun makeSnackbar(view: View, message: String, action: String, context: Context?, activityname: Class<*>) {
        val snack: TSnackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_LONG)
        val v: View = snack.view
        var params2: CoordinatorLayout.LayoutParams = v.layoutParams as (CoordinatorLayout.LayoutParams)
        params2.setMargins(0,198,0,0)
        v.layoutParams=params2
        val textView: TextView = v.findViewById(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snack.setAction(action) {
            context?.startActivity(Intent(context, activityname))
            (context as Activity).finish()
        }
        snack.show()
    }

    fun getDate(dateString: String): String {
        val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
        formatter.timeZone = TimeZone.getTimeZone("UTC ")
        var value: Date? = null
        try {
            value = formatter.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        dateFormatter.timeZone = TimeZone.getDefault()
        if (value != null)
            return dateFormatter.format(value)
        else
            return ""
    }

    fun getTime(dateString: String): String {
        val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
        formatter.timeZone = TimeZone.getTimeZone("UTC ")
        var value: Date? = null
        try {
            value = formatter.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val dateFormatter = SimpleDateFormat("hh:mm a")
        dateFormatter.timeZone = TimeZone.getDefault()
        if (value != null)
            return dateFormatter.format(value)
        else
            return ""
    }

}