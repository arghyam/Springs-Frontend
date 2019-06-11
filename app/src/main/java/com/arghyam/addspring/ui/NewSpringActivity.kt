package com.arghyam.addspring.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
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
import com.arghyam.addspring.interfaces.ImageUploadInterface
import com.arghyam.addspring.model.CreateSpringResponseObject
import com.arghyam.addspring.model.RequestSpringDataModel
import com.arghyam.addspring.model.SpringModel
import com.arghyam.addspring.repository.CreateSpringRepository
import com.arghyam.addspring.repository.UploadImageRepository
import com.arghyam.addspring.viewmodel.CreateSpringViewModel
import com.arghyam.addspring.viewmodel.UploadImageViewModel
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.CAMERA_PERMISSION_NOT_GRANTED
import com.arghyam.commons.utils.Constants.CREATE_SPRING_ID
import com.arghyam.commons.utils.Constants.LOCATION_PERMISSION_NOT_GRANTED
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_ON_RESULT_CODE
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_RESULT_CODE
import com.arghyam.commons.utils.Constants.REQUEST_IMAGE_CAPTURE
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.content_add_additional_details.*
import kotlinx.android.synthetic.main.content_new_spring.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class NewSpringActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener{

    private var goBack: Boolean = false
    @Inject
    lateinit var createSpringRepository: CreateSpringRepository

    @Inject
    lateinit var uploadImageRepository: UploadImageRepository

    private var createSpringViewModel: CreateSpringViewModel? = null

    private lateinit var uploadImageViewModel: UploadImageViewModel

    private var imagesList: ArrayList<String> = ArrayList()
    private var photoFile: File? = null

    val REQUEST_CODE = 4
    private val TAG = "MainActivity"
    private var mGoogleApiClient: GoogleApiClient? = null
    private var googleApiClient: GoogleApiClient? = null
    private var mLocationManager: LocationManager? = null
    private var isLocationTurnedOn: Boolean = false
    private var isLocationNotAccepted: Boolean = false

    private var mLocation: Location?= null
    var count: Int = 1
    var imageList = ArrayList<ImageEntity>()

    lateinit var imageUploaderAdapter: ImageUploaderAdapter

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
        init()
    }

    private fun init() {
        initComponent()
        initToolbar()
        initRecyclerView()
        initDefaultLocation()
        initLocation()
        initLocationClick()
        initRepository()
        initUploadImageClick()
        initApiResponseCalls()
        initUploadImageApis()
        initCreateSpringSubmit()

    }

    private fun initDefaultLocation() {
        tv_reposition.text = "Click on to reposition your gps"
        img_GPS.setImageResource(R.drawable.ic_location)
    }

    private fun initUploadImageClick() {
        image_upload_layout.setOnClickListener {
            openCamera()
        }
    }

    private fun initUploadImageApis() {
        uploadImageViewModel.getUploadImageResponse().observe(this@NewSpringActivity, Observer {
            Log.e("stefy", it?.response!!.imageUrl)
            imagesList.add(it.response.imageUrl)
        })
        uploadImageViewModel.getImageError().observe(this@NewSpringActivity, Observer {
            Log.e("stefy error", it)
        })
    }

    private fun initApiResponseCalls() {
        createSpringViewModel?.getCreateSpringResponse()?.observe(this@NewSpringActivity, Observer {
            saveCreateSpringData(it)
            if (createSpringViewModel?.getCreateSpringResponse()?.hasObservers()!!) {
                createSpringViewModel?.getCreateSpringResponse()?.removeObservers(this@NewSpringActivity)
            }
        })
        createSpringViewModel?.getSpringError()?.observe(this@NewSpringActivity, Observer {
            Log.e("error", it)
        })
    }

    private fun saveCreateSpringData(responseModel: ResponseModel) {
        var createSpringResponseObject: CreateSpringResponseObject = Gson().fromJson(
            ArghyamUtils().convertToString(responseModel.response.responseObject),
            object : TypeToken<CreateSpringResponseObject>() {}.type
        )
//        gotoLandingActivity(createSpringResponseObject)
        gotoSpringDetailsActivty(createSpringResponseObject)
    }


    private fun gotoSpringDetailsActivty(createSpringResponseObject: CreateSpringResponseObject) {
        val intent = Intent(this@NewSpringActivity, SpringDetailsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initCreateSpringSubmit() {
        add_spring_submit.setOnClickListener {

            if (radioGroup.checkedRadioButtonId == -1) {
                ArghyamUtils().longToast(this@NewSpringActivity, "Please select the Ownership type")
            } else if(imageList.size <= 0) {
                ArghyamUtils().longToast(this@NewSpringActivity, "Please upload the Spring image")

            }else if(mLocation== null){
                ArghyamUtils().longToast(this@NewSpringActivity, "Please upload the location")

            }else {
                createSpringOnClick()
                add_spring_submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                ArghyamUtils().longToast(this@NewSpringActivity, "New spring added succesfully")


            }
        }
    }


    private fun createSpringOnClick() {
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

                    tenantId = Constants.TENANTID,
                    orgId = Constants.ORGID,
                    latitude = mLocation!!.latitude,
                    longitude = mLocation!!.longitude,
                    elevation = mLocation!!.altitude,
                    accuracy = mLocation!!.accuracy,
                    village = Constants.VILLAGE,
                    ownershipType = findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString(),
                    images = imagesList

                )
            )
        )
        createSpringViewModel?.createSpringApi(this, createSpringObject)

    }

    private fun initToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        if (goBack) {
            onBackPressed()
        } else {
            ArghyamUtils().longToast(this, "Are you sure you want to go back?")
            startTimer()
        }
        goBack = true
        return true
    }

    private fun startTimer() {
        Handler().postDelayed({
            goBack = false
        }, 2000)
    }

    private fun initLocationClick() {
        img_GPS.setOnClickListener {
            getGoogleClient()
            tv_reposition.text = "fetching location information..."
            img_GPS.setImageResource(R.drawable.ic_location_refresh)
            img_GPS.setBackgroundResource(R.drawable.circle_border)
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
//        tv_address.visibility = View.VISIBLE
//        address_layout.visibility = View.VISIBLE
//        tv_address.visibility = View.VISIBLE
//        address_layout.visibility = View.VISIBLE
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
                ArghyamUtils().turnOnLocation(this@NewSpringActivity)
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

    private fun openCamera() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(cameraPermissionListener).check()
    }

    private val cameraPermissionListener = object : PermissionListener {
        override fun onPermissionGranted(response: PermissionGrantedResponse) {
            var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE)
        }

        override fun onPermissionDenied(response: PermissionDeniedResponse) {
            if (response.isPermanentlyDenied) {
                ArghyamUtils().longToast(this@NewSpringActivity, CAMERA_PERMISSION_NOT_GRANTED)
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
                    latitude.text = ": ${mLocation!!.latitude}"
                    longitude.text = ": ${mLocation!!.longitude}"
                    altitude.text = ": ${mLocation!!.altitude} mts"
                    tv_accuracy.text = "Device accuracy  : ${mLocation!!.accuracy}mts"

                    if (mLocation!!.accuracy < 50) {
                        tv_reposition.text = "Done"
                        img_GPS.setImageResource(R.drawable.ic_location_done)
                        img_GPS.setBackgroundResource(0)

                    } else {
                        tv_reposition.text = "Click on to reposition your gps"
                        img_GPS.setImageResource(R.drawable.ic_location)
                        ArghyamUtils().longToast(applicationContext, "Preferred device accuracy is less than 50mts")

                    }

                } else {
                    getFusedClient()
                }
            }
    }


    private fun initRecyclerView() {
        imageRecyclerView.layoutManager = LinearLayoutManager(this)
        imageUploaderAdapter = ImageUploaderAdapter(this@NewSpringActivity, imageList, imageUploadInterface)
        imageRecyclerView.adapter = imageUploaderAdapter
    }

    private val imageUploadInterface = object : ImageUploadInterface {
        override fun onCancel(position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun retry(position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onRemove(position: Int) {
            onImageRemove(position)
        }
    }


    private fun onImageRemove(position: Int) {
        imageList.removeAt(position)
        imageUploaderAdapter.notifyItemRemoved(position)
        imageUploaderAdapter.notifyItemRangeChanged(position, imageList.size)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    onImageReceive(data)
                }
            }
            PERMISSION_LOCATION_RESULT_CODE -> {
                getGoogleClient()
            }
            PERMISSION_LOCATION_ON_RESULT_CODE -> {
                when (resultCode) {

                    -1 -> {
                        isLocationTurnedOn = true
                    }
                    0 -> {
                        isLocationNotAccepted = true
                    }
                }
            }
            REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    var bundle = data?.getBundleExtra("DataBundle")
                    Log.d("bundleSpring",bundle.toString())
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        isLocationAccepted()
        isLocationRejected()
    }

    private fun isLocationRejected() {
        if (isLocationNotAccepted) {
            ArghyamUtils().turnOnLocation(this@NewSpringActivity)
            isLocationNotAccepted = false
        }
    }

    private fun isLocationAccepted() {
        if (isLocationTurnedOn) {
            getFusedClient()
            isLocationTurnedOn = false
        }
    }

    private fun onImageReceive(intent: Intent?) {
        var bitmap: Bitmap? = intent!!.extras.get("data") as Bitmap
        addBitmapToList(bitmap)
        makeUploadApiCall(bitmap)
    }

    private fun makeUploadApiCall(bitmap: Bitmap?) {
        var body: MultipartBody.Part? = getMultipartBodyFromBitmap(bitmap)
        if (null != body) {
            uploadImageViewModel.uploadImageApi(this@NewSpringActivity, body)
        }
    }

    private fun getMultipartBodyFromBitmap(bitmap: Bitmap?): MultipartBody.Part? {
        var body: MultipartBody.Part? = null
        try {
            var filesDir: File = applicationContext.filesDir
            var file: File = File(filesDir, "SPRING_NAME_" + String.format("%3d", count) + ".png")
            val bos: ByteArrayOutputStream = ByteArrayOutputStream()
            var mBitMap: Bitmap = bitmap!!
            mBitMap.compress(Bitmap.CompressFormat.PNG, 0, bos)
            var bitmapdata = bos.toByteArray()
            var fos: FileOutputStream = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            var reqFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            body = MultipartBody.Part.createFormData("file", file.name, reqFile)
        } catch (ex: Exception) {

        }
        return body
    }

    private fun addBitmapToList(bitmap: Bitmap?) {
        imageList.add(ImageEntity(count, bitmap!!, "Image" + String.format("%04d", count) + ".jpg", 0))
        imageUploaderAdapter.notifyDataSetChanged()
        count++
    }


    private fun initRepository() {
        createSpringViewModel = ViewModelProviders.of(this).get(CreateSpringViewModel::class.java)
        createSpringViewModel?.setCreateSpringRepository(createSpringRepository)
        uploadImageViewModel = ViewModelProviders.of(this).get(UploadImageViewModel::class.java)
        uploadImageViewModel.setUploadImageRepository(uploadImageRepository)
    }

}

