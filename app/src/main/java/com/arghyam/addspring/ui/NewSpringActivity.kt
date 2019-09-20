package com.arghyam.addspring.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.*
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.OpenableColumns
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
import com.arghyam.commons.utils.Constants.CREATE_SPRING_ID
import com.arghyam.commons.utils.Constants.LOCATION_PERMISSION_NOT_GRANTED
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_ON_RESULT_CODE
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_RESULT_CODE
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.model.AllSpringDataModel
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.content_new_spring.*
import kotlinx.android.synthetic.main.list_image_uploader.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import javax.inject.Inject

@Suppress("DEPRECATION")
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

    private var coordinateList = ArrayList<AllSpringDataModel>()


    val REQUEST_CODE = 4
    private val TAG = "MainActivity"
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationManager: LocationManager? = null
    private var isLocationTurnedOn: Boolean = false
    private var isLocationNotAccepted: Boolean = false
    private var clickable = true

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
        gotoSpringDetailsActivty(createSpringResponseObject)
    }


    private fun gotoSpringDetailsActivty(createSpringResponseObject: CreateSpringResponseObject) {
        val intent = Intent(this@NewSpringActivity, SpringDetailsActivity::class.java)

        springName = spring_name.text.toString().trim()
        intent.putExtra("SpringCode", createSpringResponseObject.springCode)
        intent.putExtra("SpringName", springName)
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
                ArghyamUtils().longToast(this@NewSpringActivity, "Please upload the address")
            } else if (clickable) {
                createSpringOnClick()
                add_spring_submit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                ArghyamUtils().longToast(this@NewSpringActivity, "New spring added succesfully")
                clickable = false
            }
        }
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
        Log.e("NewSpringActivity", "  imagelist size " + imageList.size)

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
                    submittedBy = "",
                    ownershipType = findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString(),
                    images = imagesList,
                    userId = SharedPreferenceFactory(this@NewSpringActivity).getString(Constants.USER_ID)!!

                )
            )
        )
        Log.e("NewSpringActivity", "  imagelist size " + imageList.size)

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
            tv_reposition.text = "fetching address information..."
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
        tv_address.visibility = VISIBLE
        address_layout.visibility = VISIBLE
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
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(cameraPermissionListener).check()
    }

    val PICTURE_RESULT = 0
    private val values = ContentValues()
    var imageUri: Uri? = null
    var body: MultipartBody.Part? = null

    private val cameraPermissionListener = object : MultiplePermissionsListener {
        override fun onPermissionRationaleShouldBeShown(
            permissions: MutableList<PermissionRequest>?,
            token: PermissionToken?
        ) {
            token?.continuePermissionRequest()
        }

        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            i.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            startActivityForResult(i, PICTURE_RESULT)
        }

    }

    @SuppressLint("MissingPermission")
    private fun getFusedClient() {
        var fusedLocationProviderClient:
                FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener(this) { location ->
                // Got last known address. In some rare situations this can be null.
                if (location != null) {
                    toggleLocation()
                    val geocoder: Geocoder = Geocoder(this)
                    val address = geocoder.getFromLocation(location.latitude,location.longitude,1)
                    tv_address_location.text = address[0].getAddressLine(0)
                            // Logic to handle address object
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
            //To change body of created functions use File | Settings | File Templates.
        }

        override fun retry(position: Int) {
            //To change body of created functions use File | Settings | File Templates.
        }

        override fun onRemove(position: Int) {
            onImageRemove(position)
            imageCount--
            imageuploadcount--
        }

        override fun onSuccess(position: Int) {
            //To change body of created functions use File | Settings | File Templates.
        }
    }

    private fun onImageRemove(position: Int) {
        imageList.removeAt(position)
        imagesList.removeAt(position)
        imageUploaderAdapter.notifyItemRemoved(position)
        imageUploaderAdapter.notifyItemRangeChanged(position, imageList.size)
        isvalid()
        onremoved(position)
    }

    private fun onremoved(position: Int) {
        imageRecyclerView[position].progress.visibility = VISIBLE
        imageRecyclerView[position].image_loader.visibility = GONE
        imageRecyclerView[position].upload_status.text = "Uploading"

        imageUploaderAdapter.notifyItemRemoved(position)
        imageUploaderAdapter.notifyItemRangeChanged(position, imageList.size)

        if (position == 0 && imageList.size > 0) {
            imageRecyclerView[position].progress.visibility = GONE
            imageRecyclerView[position].image_loader.visibility = VISIBLE
            imageRecyclerView[position].upload_status.text = "Uploaded"

            imageRecyclerView[position + 1].progress.visibility = VISIBLE
            imageRecyclerView[position + 1].image_loader.visibility = GONE
            imageRecyclerView[position + 1].upload_status.text = "Uploading"

            imageUploaderAdapter.notifyDataSetChanged()
        }


    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(contentUri, proj, null, null, null)
        val columnIndex = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst();
        return cursor.getString(columnIndex)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICTURE_RESULT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val thumbnail = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    val path = imageUri?.let { getRealPathFromURI(it) }
                    Log.e("NewSpring",path)
                    if (null != thumbnail) {
                        Log.e("NewSpring", "onActivityResult")
                        onImageReceive(thumbnail, path)
                        imageCount++
                    } else
                        Log.e("NewSpring", "onActivityResult null")
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

    private fun onImageReceive(data: Bitmap?, path: String?) {
        val bitmap: Bitmap? = data
        body = getMultipartBodyFromBitmap(path)
        makeUploadApiCall(bitmap)

    }

    private fun makeUploadApiCall(bitmap: Bitmap?) {
        if (null == bitmap) {
            Log.e("NewSpring", "bitmap is null")
        }
        if (null == body) {
            Log.e("NewSpring", "body is null")
        }
        if (null != bitmap && null != body) {
            Log.e("NewSpring", "onImageReceive")
            addBitmapToList(bitmap)
            MyAsyncTask().execute()
        } else
            Log.e("NewSpring", "onImageReceive null")

    }

    private fun updateProgressbar(progress: Int) {
        runOnUiThread {
            imageList[imageuploadcount].uploadPercentage = progress
            imageUploaderAdapter.notifyDataSetChanged()
        }
    }

    inner class MyAsyncTask : AsyncTask<Void, Int, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            var a = 0
            uploadImageViewModel.uploadImageApi(this@NewSpringActivity, body!!)
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
        }
    }

    private fun getMultipartBodyFromBitmap(path: String?): MultipartBody.Part? {
        val file = File(path)

        var bitmap = BitmapFactory.decodeFile(file.path)
        val stream:OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG,30,stream)
        stream.flush()
        stream.close()

        if (file.length() < FILE_SIZE_LIMIT) {
            var reqFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            body = MultipartBody.Part.createFormData("file", file.name, reqFile)
        }
        return body
    }


    private fun addBitmapToList(bitmap: Bitmap?) {
        imageList.add(ImageEntity(count, bitmap!!, "Image" + String.format("%04d", count) + ".jpg", 0))
        imageUploaderAdapter.notifyDataSetChanged()
        count++
        imageuploadcount++

    }


    private fun initRepository() {
        createSpringViewModel = ViewModelProviders.of(this).get(CreateSpringViewModel::class.java)
        createSpringViewModel?.setCreateSpringRepository(createSpringRepository)
        uploadImageViewModel = ViewModelProviders.of(this).get(UploadImageViewModel::class.java)
        uploadImageViewModel.setUploadImageRepository(uploadImageRepository)

    }

    private fun hideSoftKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }



}

