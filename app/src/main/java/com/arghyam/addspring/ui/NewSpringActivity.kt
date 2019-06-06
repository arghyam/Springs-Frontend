package com.arghyam.addspring.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.addspring.adapters.ImageUploaderAdapter
import com.arghyam.addspring.entities.ImageEntity
import com.arghyam.addspring.model.RequestSpringDataModel
import com.arghyam.addspring.model.SpringModel
import com.arghyam.addspring.repository.CreateSpringRepository
import com.arghyam.addspring.viewmodel.CreateSpringViewModel
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants.CREATE_SPRING_ID
import com.arghyam.commons.utils.Constants.LOCATION_PERMISSION_NOT_GRANTED
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_ON_RESULT_CODE
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_RESULT_CODE
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.ui.activity.LandingActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.content_new_spring.*
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class NewSpringActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    @Inject
    lateinit var createSpringRepository: CreateSpringRepository

    private var createSpringViewModel: CreateSpringViewModel? = null

    private var imagesList: ArrayList<String> = ArrayList()

    private val TAG = "MainActivity"
    private var mGoogleApiClient: GoogleApiClient? = null
    private var googleApiClient: GoogleApiClient? = null
    private var mLocationManager: LocationManager? = null
    lateinit var mLocation: Location
    var count: Int = 1
    var imageList = ArrayList<ImageEntity>()

    lateinit var locationManager: LocationManager

    override fun onStart() {
        super.onStart()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
    }

    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {
        Log.i(TAG, "Connection Suspended")
        mGoogleApiClient!!.connect()


    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.errorCode)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_spring)
        image_upload_layout.setOnClickListener {
            var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, 123)
        }
        init()
    }

    private fun init() {
        initComponent()
        initToolbar()
        initRecyclerView()
        initLocation()
        initLocationClick()
        initRepository()

        initCreateSpringSubmit()
        initApiResponseCalls()
    }

    private fun initApiResponseCalls() {
        createSpringViewModel?.getCreateSpringResponse()?.observe(this@NewSpringActivity, Observer {
            Log.e("karthik", it?.response?.responseCode)
            saveCreateSpringData(it)
        })

        createSpringViewModel?.getSpringError()?.observe(this@NewSpringActivity, Observer {
            Log.e("error", it)
        })
    }

    private fun saveCreateSpringData(responseModel: ResponseModel) {
        Log.d("saveUserProfileData", "saveUserProfileData")
        if (responseModel.response.responseCode.equals("200")) {
            val intent = Intent(this@NewSpringActivity, LandingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initCreateSpringSubmit() {
        add_spring_submit.setOnClickListener {
            createSpringOnClick()
        }
    }


    private fun createSpringOnClick() {
        imagesList.add("Image 1")
        imagesList.add("Image 2")
        var createSpringObject = RequestModel(
            id = CREATE_SPRING_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = RequestSpringDataModel(
                springs = SpringModel(

                    springName = spring_name.text.toString(),
                    ownership = findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString(),
                    images = imagesList,
                    latitude = "${mLocation.latitude}",
                    longitude = "${mLocation.longitude}"

                )
            )
        )
        createSpringViewModel?.createSpringApi(this, createSpringObject)

    }

    private fun initToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private fun initLocationClick() {
        img_GPS.setOnClickListener {
            getGoogleClient()
            tv_reposition.text = "fetching information..."
        }
    }

    private fun initLocation() {
        location_layout.setOnClickListener {
            getGoogleClient()
        }

    }

    private fun toggleLocation() {
        card_device.visibility = View.VISIBLE
        tv_coordinates.visibility = View.VISIBLE
        tv_address.visibility = View.VISIBLE
        address_layout.visibility = View.VISIBLE
        tv_address.visibility = View.VISIBLE
        address_layout.visibility = View.VISIBLE
        tl_cooridinates.visibility = View.VISIBLE
        location_layout.visibility = View.GONE
    }


    private fun getGoogleClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getLocation()

    }


    private fun turnOnLocation() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(applicationContext).addApi(LocationServices.API).build()
            googleApiClient?.connect()
            var locationRequest: LocationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 30 * 1000
            locationRequest.fastestInterval = 5 * 1000
            var builder: LocationSettingsRequest.Builder =
                LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)
            var result: PendingResult<LocationSettingsResult> =
                LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback {
                when (it.status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                        getGoogleClient()
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            // Ask to turn on GPS automatically
                            it.status.startResolutionForResult(
                                this@NewSpringActivity,
                                PERMISSION_LOCATION_ON_RESULT_CODE
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }
                }
            }
        }
    }


    private fun getLocation() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(permissionListener).check()
    }

    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted(response: PermissionGrantedResponse) {
            if (ArghyamUtils().isLocationEnabled(this@NewSpringActivity)) {
                getFusedClient()
            } else {
                turnOnLocation()
            }
        }

        override fun onPermissionDenied(response: PermissionDeniedResponse) {
            if (response.isPermanentlyDenied) {
                ArghyamUtils().longToast(this@NewSpringActivity, LOCATION_PERMISSION_NOT_GRANTED)
                ArghyamUtils().openSettings(this@NewSpringActivity)
            }
        }

        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
            token.continuePermissionRequest()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getFusedClient() {
        var fusedLocationProviderClient:
                FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    toggleLocation()
                    // Logic to handle location object
                    mLocation = location
                    latitude.text = ": ${mLocation.latitude}"
                    longitude.text = ": ${mLocation.longitude}"
                    tv_accuracy.text = "Device accuracy  : ${mLocation.accuracy}mts"
                    tv_reposition.text = "Click on to reposition your gps"
                }
            }
    }


    private fun initRecyclerView() {

        imageRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ImageUploaderAdapter(imageList)
        imageRecyclerView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            123 -> {
                var bmp = data!!.extras.get("data") as Bitmap
                compressBitmap(bmp, 5)
                imageList.add(ImageEntity(count, bmp, "Image" + String.format("%04d", count) + ".jpg", 0))
                count++
            }
            PERMISSION_LOCATION_RESULT_CODE,
            PERMISSION_LOCATION_ON_RESULT_CODE -> {
                getGoogleClient()
            }
        }
    }

    private fun compressBitmap(bmp: Bitmap, quality: Int): Bitmap {
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun initRepository() {
        createSpringViewModel = ViewModelProviders.of(this).get(CreateSpringViewModel::class.java)
        createSpringViewModel?.setCreateSpringRepository(createSpringRepository)
    }

}
