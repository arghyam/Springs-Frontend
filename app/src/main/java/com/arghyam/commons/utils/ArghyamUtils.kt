package com.arghyam.commons.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_ON_RESULT_CODE
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import java.io.*
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

    fun getDataUriForImages(context: Context, bitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(Uri.parse(path), proj, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }

    fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        var bmp: Bitmap? = null
        var `is`: InputStream? = null
        if (uri != null) {
            try {
                `is` = context.contentResolver.openInputStream(uri)
                bmp = BitmapFactory.decodeStream(`is`)
            } catch (e: FileNotFoundException) {
                Log.e("getBitFileNotFound ", e.toString())
            } finally {
                try {
                    `is`!!.close()
                } catch (e: IOException) {
                    Log.e("getBitmapFromUri: ", e.toString())
                }

            }
        }

        return bmp
    }

    fun createImageFile(): File? {
        var image: File? = null
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )
            image = File.createTempFile(
                imageFileName, // prefix
                ".jpg", // suffix
                storageDir      // directory
            )
        } catch (exception: IOException) {
            image = null
        }
        return image
    }

}