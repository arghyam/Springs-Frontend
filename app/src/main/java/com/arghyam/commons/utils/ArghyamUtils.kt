package com.arghyam.commons.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_ON_RESULT_CODE
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat.startActivity
import com.androidadvance.topsnackbar.TSnackbar
import com.arghyam.R
import com.arghyam.iam.ui.LoginActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
        val attrs: IntArray = intArrayOf(R.attr.actionBarSize)
        val snack: TSnackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_LONG)
        val v: View = snack.view
        var params2: CoordinatorLayout.LayoutParams = v.layoutParams as (CoordinatorLayout.LayoutParams)
        params2.setMargins(0, context!!.obtainStyledAttributes(attrs).getDimensionPixelSize(0,-1), 0, 0)
        v.layoutParams = params2
        val textView: TextView = v.findViewById(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snack.setAction(action) {
            context.startActivity(Intent(context, activityname))
            (context as Activity).finish()
        }
        snack.show()
    }

    fun makeAlertDialog(context: Context,title: String,option1:String,option2:String,activityname: Class<*>){
        val dialogBuilder = AlertDialog.Builder(context!!)
        dialogBuilder.setMessage("")

            .setPositiveButton(option1) { dialog, which ->

                context.startActivity(Intent(context, activityname))
                (context as Activity).finish()
                dialog.cancel()
            }
            .setNegativeButton(option2) { dialog, which ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
    }

    fun AlertBox(context: Context?,activity: Activity, title: String, message: String,activityname: Class<*>) {
        val alertbox = AlertDialog.Builder(activity)
        alertbox.setTitle(title)
        alertbox.setCancelable(false)
        alertbox.setMessage(message)
        alertbox.setPositiveButton("OK") { dialog, which ->
            context?.startActivity(Intent(context, activityname))
            (context as Activity).finish()
            dialog.cancel()
        }
        alertbox.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
        alertbox.show()
    }

    fun convertToNames(selectedMonth: ArrayList<String>):ArrayList<String> {
        var selectedMonthNames:ArrayList<String> = ArrayList()
        selectedMonth.sort()
        for (month in selectedMonth) {
            when (month) {
                "1" -> {
                    if (!selectedMonthNames.contains("Jan"))
                        selectedMonthNames.add("Jan")
                }
                "2" -> {
                    if (!selectedMonthNames.contains("Feb"))
                        selectedMonthNames.add("Feb")
                }
                "3" -> {
                    if (!selectedMonthNames.contains("Mar"))
                        selectedMonthNames.add("Mar")
                }
                "4" -> {
                    if (!selectedMonthNames.contains("Apr"))
                        selectedMonthNames.add("Apr")
                }
                "5" -> {
                    if (!selectedMonthNames.contains("May"))
                        selectedMonthNames.add("May")
                }
                "6" -> {
                    if (!selectedMonthNames.contains("Jun"))
                        selectedMonthNames.add("Jun")
                }
                "7" -> {
                    if (!selectedMonthNames.contains("Jul"))
                        selectedMonthNames.add("Jul")
                }
                "8" -> {
                    if (!selectedMonthNames.contains("Aug"))
                        selectedMonthNames.add("Aug")
                }
                "9" -> {
                    if (!selectedMonthNames.contains("Sep"))
                        selectedMonthNames.add("Sep")
                }
                "10" -> {
                    if (!selectedMonthNames.contains("Oct"))
                        selectedMonthNames.add("Oct")
                }
                "11" -> {
                    if (!selectedMonthNames.contains("Nov"))
                        selectedMonthNames.add("Nov")
                }
                "12" -> {
                    if (!selectedMonthNames.contains("Dec"))
                        selectedMonthNames.add("Dec")
                }

            }
        }
        return selectedMonthNames
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