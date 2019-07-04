package com.arghyam.addspring.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
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
import com.arghyam.addspring.repository.GetSpringOptionalRepository
import com.arghyam.addspring.repository.UploadImageRepository
import com.arghyam.addspring.viewmodel.CreateSpringViewModel
import com.arghyam.addspring.viewmodel.GetSpringOptionalViewModel
import com.arghyam.addspring.viewmodel.UploadImageViewModel
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.CAMERA_PERMISSION_NOT_GRANTED
import com.arghyam.commons.utils.Constants.CREATE_SPRING_ID
import com.arghyam.commons.utils.Constants.LOCATION_PERMISSION_NOT_GRANTED
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_ON_RESULT_CODE
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_RESULT_CODE
import com.arghyam.commons.utils.Constants.REQUEST_IMAGE_CAPTURE
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.model.AllSpringDataModel
import com.arghyam.landing.model.AllSpringDetailsModel
import com.arghyam.landing.model.AllSpringModel
import com.arghyam.landing.model.GetAllSpringsModel
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
import kotlinx.android.synthetic.main.content_new_spring.*
import kotlinx.android.synthetic.main.list_image_uploader.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class NewSpringActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {


    private var goBack: Boolean = false
    private var imageCount: Int = 0
    val FILE_SIZE_LIMIT: Long = 6291456

    @Inject
    lateinit var createSpringRepository: CreateSpringRepository

    private var getSpringOptionalViewModel: GetSpringOptionalViewModel? = null


    @Inject
    lateinit var uploadImageRepository: UploadImageRepository

    @Inject
    lateinit var getSpringOptionalRepository: GetSpringOptionalRepository

    private var createSpringViewModel: CreateSpringViewModel? = null

    private lateinit var uploadImageViewModel: UploadImageViewModel

    private var imagesList: ArrayList<String> = ArrayList()
    private var springName: String? = null

    private var photoFile: File? = null

    private var coordinateList = ArrayList<AllSpringDataModel>()


    val REQUEST_CODE = 4
    private val TAG = "MainActivity"
    private var mGoogleApiClient: GoogleApiClient? = null
    private var googleApiClient: GoogleApiClient? = null
    private var mLocationManager: LocationManager? = null
    private var isLocationTurnedOn: Boolean = false
    private var isLocationNotAccepted: Boolean = false

    private var mLocation: Location? = null
    var count: Int = 1
    var imageuploadcount: Int = -1
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
        getSpringOptionalRequest()
        initGetAllSpring()
        initUploadImageClick()
        initApiResponseCalls()
        initUploadImageApis()
        initCreateSpringSubmit()
        initListener()
    }

    private fun initListener() {
        isTextWritten()
        initClick()
    }

    private fun initGetAllSpring() {
        getSpringOptionalViewModel?.getSpringOptionalResponse()?.observe(this, Observer {

            saveGetSpringsOptionalData(it)
//            if (getAllSpringViewModel?.getAllSpringResponse()?.hasObservers()!!) {
//                getAllSpringViewModel?.getAllSpringResponse()?.removeObservers(this)
//            }
        })
        getSpringOptionalViewModel?.getSpringOptionalError()?.observe(this, Observer {
            Log.e("error---", it)
            if (getSpringOptionalViewModel?.getSpringOptionalError()?.hasObservers()!!) {
                getSpringOptionalViewModel?.getSpringOptionalError()?.removeObservers(this)
            }
        })
    }

    private fun saveGetSpringsOptionalData(responseModel: ResponseModel) {

        if (responseModel.response.responseCode == "200") {

            Log.d("success_i", "yes")


            var responseSpringData: AllSpringDetailsModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<AllSpringDetailsModel>() {}.type
            )

            Log.d("saveOptionalData--", responseSpringData.toString())

            getListOfCordinates(responseSpringData)

        }

    }
    val latitude_values = ArrayList<Double>()
    val longitude_values = ArrayList<Double>()

    private fun getListOfCordinates(responseSpringData: AllSpringDetailsModel) {

        coordinateList.addAll(responseSpringData.springs)
        Log.d("coordinateLis--t",coordinateList.toString())

        for (cordinates in coordinateList) {
            if(!latitude_values.contains(ArghyamUtils().round(cordinates.latitude,3))){
                latitude_values.add(ArghyamUtils().round(cordinates.latitude,3))
            }
            if(!longitude_values.contains(ArghyamUtils().round(cordinates.longitude,3))){
                longitude_values.add(ArghyamUtils().round(cordinates.longitude,3))
            }
        }
        Log.d("response--latitude", latitude_values.toString())
        Log.d("response--longitude", longitude_values.toString())

    }


    private fun initDefaultLocation() {
        tv_reposition.text = "Click on to reposition your gps"
        img_GPS.setImageResource(R.drawable.ic_location)
    }

    private fun initUploadImageClick() {
        image_upload_layout.setOnClickListener {
            var validated: Boolean = validateListener()
            if (validated) {
                valid()
            } else
                notvalid()
            if (imageCount < 2)
                openCamera()
            else {
                ArghyamUtils().shortToast(this, "You can capture maximum of two photographs")
            }
        }
    }

    private fun isTextWritten() {
        spring_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}


            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isvalid()
            }
        })
    }

    private fun initClick() {
        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            initRadioButtonListeners()
            hideSoftKeyboard()
        }
    }

    private fun initRadioButtonListeners() {
        isvalid()
    }

    private fun notvalid() {
        add_spring_submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
    }

    private fun valid() {
        add_spring_submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
    }

    private fun initUploadImageApis() {
        uploadImageViewModel.getUploadImageResponse().observe(this@NewSpringActivity, Observer {
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

        springName = spring_name.text.toString().trim()
        intent.putExtra("SpringCode", createSpringResponseObject.springCode)
        intent.putExtra("springName", springName)
        Log.e("Code", createSpringResponseObject.springCode)
        startActivity(intent)
        finish()
    }

    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initCreateSpringSubmit() {
        add_spring_submit.setOnClickListener {

            if (spring_name.text == null || spring_name.text.toString().trim().equals("")) {
                ArghyamUtils().longToast(this@NewSpringActivity, "Please enter the spring name")
            } else if (spring_name.text.toString().trim().length < 3) {
                ArghyamUtils().longToast(this@NewSpringActivity, "Spring name should contain atleast 3 characters")

            } else if (spring_name.text.toString().startsWith(" ")) {
                ArghyamUtils().longToast(this@NewSpringActivity, "Spring name should not start with space")
            } else if (radioGroup.checkedRadioButtonId == -1) {
                ArghyamUtils().longToast(this@NewSpringActivity, "Please select the ownership type")
            } else if (imageList.size <= 0) {
                ArghyamUtils().longToast(this@NewSpringActivity, "Please upload the spring image")

            } else if (mLocation == null) {
                ArghyamUtils().longToast(this@NewSpringActivity, "Please upload the location")

            }
            else if (checkDistance()){
                showDialogbox()
            }
            else {
                createSpringOnClick()
                add_spring_submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                ArghyamUtils().longToast(this@NewSpringActivity, "New spring added succesfully")
            }
        }
    }

    private fun checkDistance(): Boolean {

        if (mLocation != null && longitude_values !=null && latitude_values!=null ) {

            for (i in 0 until  latitude_values.size) {

                var currentLat = mLocation!!.latitude
                var currentLon = mLocation!!.longitude

                var loc1 = Location("")
                loc1.latitude = currentLat
                loc1.longitude = currentLon

                var existingLat = latitude_values
                var existingLon = longitude_values

                val loc2 = Location("")
                loc2.latitude = existingLat[i]
                loc2.longitude = existingLon[i]

                val distanceInMeters = loc1.distanceTo(loc2)
                Log.e("Anirudh", "distance$distanceInMeters")


                if (distanceInMeters <= 50) {

                    Log.e("Anirudh", distanceInMeters.toString() + "    "+loc1.latitude +"   "+loc1.longitude +  "    "+loc2.latitude+"    "+loc2.longitude)
                    Log.e("Anirudh", "less than 50")
                    return true
                } else
                    Log.e("Anirudh", distanceInMeters.toString())
            }
        }
        return false
    }

    private fun showDialogbox() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("A spring already exists in close proximity. Are you sure it is not the same spring?")

            .setPositiveButton("YES") { dialog, which ->
                dialog.cancel()
                createSpringOnClick()
                ArghyamUtils().longToast(this@NewSpringActivity, "New spring added succesfully")
            }
            .setNegativeButton("CANCEL") { dialog, which ->
                this.finish()
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Are you sure ?")
        alert.show()
    }

    private fun validateListener(): Boolean {
        return springNameListener() && imageUploadListener() && ownershipTypeListener() && locationListener()
    }

    private fun locationListener(): Boolean {
        return mLocation != null
    }

    private fun imageUploadListener(): Boolean {
        return imagesList.size > 0
    }

    private fun ownershipTypeListener(): Boolean {
        return (radioGroup.checkedRadioButtonId != -1)
    }

    private fun springNameListener(): Boolean {
        return !(spring_name.text == null || spring_name.text.toString().trim().equals("") || spring_name.text.toString().trim().length < 3)
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

                    springName = spring_name.text.toString().trim(),
                    tenantId = "",
                    orgId = "",
                    latitude = mLocation!!.latitude,
                    longitude = mLocation!!.longitude,
                    elevation = mLocation!!.altitude,
                    accuracy = mLocation!!.accuracy,
                    village = "",
                    ownershipType = findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString(),
                    images = imagesList,
                    userId = SharedPreferenceFactory(this@NewSpringActivity).getString(Constants.USER_ID)!!
                )
            )
        )
        createSpringViewModel?.createSpringApi(this, createSpringObject)

    }

    private fun initToolbar() {
        val toolbar = toolbar as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = "New Spring"
    }

    override fun onSupportNavigateUp(): Boolean {
        if (goBack) {
            onBackPressed()
        } else {
            ArghyamUtils().longToast(this, "Are you sure you want to go back? You will lose the Entered Data")
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
            hideSoftKeyboard()
            getGoogleClient()
            tv_reposition.text = "fetching location information..."
            img_GPS.setImageResource(R.drawable.ic_location_refresh)
            img_GPS.setBackgroundResource(R.drawable.circle_border)
        }
    }

    private fun isvalid() {
        var validated: Boolean = validateListener()
        if (validated) {
            valid()
        } else {
            notvalid()
        }
    }

    private fun initLocation() {
        location_layout.setOnClickListener {
            hideSoftKeyboard()
            getGoogleClient()
        }
    }

    private fun toggleLocation() {
        card_device.visibility = VISIBLE
        tv_coordinates.visibility = VISIBLE
        tl_cooridinates.visibility = VISIBLE
        location_layout.visibility = GONE
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

                    if (mLocation!!.accuracy < 25) {
                        tv_reposition.text = "Done"
                        img_GPS.setImageResource(R.drawable.ic_location_done)
                        img_GPS.setBackgroundResource(0)

                    } else {

                        var text: String = "Click on $ to reposition your gps"
                        var index: Int = text.indexOf("$")
                        var span: Spannable = Spannable.Factory.getInstance().newSpannable(text)
                        span.setSpan(
                            ImageSpan(baseContext, R.drawable.ic_reposition),
                            index, index + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        tv_reposition.text = span

//                        tv_reposition.text = "Click on to reposition your gps"
                        img_GPS.setImageResource(R.drawable.ic_location)
                        ArghyamUtils().longToast(applicationContext, "Preferred device accuracy is less than 25mts")

                    }
                    isvalid()
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
            imageCount--
            imageuploadcount--
        }

        override fun onSuccess(position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private fun onImageRemove(position: Int) {
        imageList.removeAt(position)
        imageUploaderAdapter.notifyItemRemoved(position)
        imageUploaderAdapter.notifyItemRangeChanged(position, imageList.size)
        isvalid()
        onremoved(position)
    }

    private fun onremoved(position: Int) {
        imageRecyclerView[position].progress.visibility = VISIBLE
        imageRecyclerView[position].image_loader.visibility = GONE
        imageRecyclerView[position].upload_status.text = "uploading"
        if (position == 0 && imageList.size > 0) {
            imageRecyclerView[position].progress.visibility = GONE
            imageRecyclerView[position].image_loader.visibility = VISIBLE
            imageRecyclerView[position].upload_status.text = ""

            imageRecyclerView[position + 1].progress.visibility = VISIBLE
            imageRecyclerView[position + 1].image_loader.visibility = GONE
            imageRecyclerView[position + 1].upload_status.text = "uploading"

            imageUploaderAdapter.notifyDataSetChanged()
        }
        imageUploaderAdapter.notifyItemRemoved(position)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    onImageReceive(data)
                    imageCount++
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
                    Log.d("bundleSpring", bundle.toString())
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        isLocationAccepted()
        isLocationRejected()
        isvalid()
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

    var body: MultipartBody.Part? = null
    private fun makeUploadApiCall(bitmap: Bitmap?) {
        body = getMultipartBodyFromBitmap(bitmap)
        MyAsyncTask().execute()
    }

    private fun updateProgressbar(progress: Int) {
        runOnUiThread(object : Runnable {
            override fun run() {
                imageList[imageuploadcount].uploadPercentage = progress
                imageUploaderAdapter.notifyDataSetChanged()
            }
        })
    }

    inner class MyAsyncTask : AsyncTask<Void, Int, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            var a = 0
            uploadImageViewModel.uploadImageApi(this@NewSpringActivity, body!!)
            Thread.sleep(100)
            while (a < 100) {
                Thread.sleep(10)
                updateProgressbar(++a)
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            updateProgressbar(100)
            //set result in textView
            imageRecyclerView[imageuploadcount].progress.visibility = GONE
            imageRecyclerView[imageuploadcount].image_loader.visibility = VISIBLE
            imageRecyclerView[imageuploadcount].upload_status.text = "Uploaded"
//            Log.e("Anirudh imgupload size f", imageRecyclerView.size.toString())

        }
    }

    private fun getMultipartBodyFromBitmap(bitmap: Bitmap?): MultipartBody.Part? {
        var body: MultipartBody.Part? = null
        try {
            var filesDir: File = applicationContext.filesDir
            var file = File(filesDir, "SPRING_NAME_" + String.format("%3d", count) + ".png")
            val bos = ByteArrayOutputStream()
            var mBitMap: Bitmap = bitmap!!
            mBitMap.compress(Bitmap.CompressFormat.PNG, 0, bos)
            var bitmapdata = bos.toByteArray()
            var fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()

            if (file.length() < FILE_SIZE_LIMIT) {
                var reqFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
//                var reqFile = ProgressRequestBody(file, this)
                body = MultipartBody.Part.createFormData("file", file.name, reqFile)
            }
        } catch (ex: Exception) {
        }
        return body
    }


    private fun addBitmapToList(bitmap: Bitmap?) {
        imageList.add(ImageEntity(count, bitmap!!, "Image" + String.format("%04d", count) + ".jpg", 0))
        imageUploaderAdapter.notifyDataSetChanged()
        count++
        imageuploadcount++

    }

    private fun getSpringOptionalRequest() {
        var getAllSpringObject = RequestModel(
            id = Constants.GET_ALL_SPRINGS_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = GetAllSpringsModel(
                springs = AllSpringModel(
                    type = "springs"
                )
            )
        )
        getSpringOptionalViewModel?.springOptionalApi(this, getAllSpringObject)

    }

    private fun initRepository() {
        createSpringViewModel = ViewModelProviders.of(this).get(CreateSpringViewModel::class.java)
        createSpringViewModel?.setCreateSpringRepository(createSpringRepository)
        uploadImageViewModel = ViewModelProviders.of(this).get(UploadImageViewModel::class.java)
        uploadImageViewModel.setUploadImageRepository(uploadImageRepository)

        getSpringOptionalViewModel = ViewModelProviders.of(this).get(GetSpringOptionalViewModel::class.java)
        getSpringOptionalViewModel?.setSpringOptionalRepository(getSpringOptionalRepository)
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}

