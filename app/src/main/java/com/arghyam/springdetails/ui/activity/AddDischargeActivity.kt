package com.arghyam.springdetails.ui.activity

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.Html
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
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
import com.arghyam.addspring.repository.UploadImageRepository
import com.arghyam.addspring.viewmodel.UploadImageViewModel
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.CREATE_DISCHARGE_DATA
import com.arghyam.commons.utils.Constants.NOTIFICATION_COUNT
import com.arghyam.commons.utils.Constants.STOP_WATCH_TIMER_RESULT_CODE
import com.arghyam.commons.utils.DecimalDigitsInputFilter
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.notification.ui.activity.DisplayDischargeDataActivity
import com.arghyam.springdetails.models.AddDischargeResponseModel
import com.arghyam.springdetails.models.DischargeDataModel
import com.arghyam.springdetails.models.DischargeModel
import com.arghyam.springdetails.models.TimerModel
import com.arghyam.springdetails.repository.DischargeDataRepository
import com.arghyam.springdetails.viewmodel.AddDischargeDataViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.content_add_discharge.*
import kotlinx.android.synthetic.main.list_image_uploader.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import javax.inject.Inject

class AddDischargeActivity : AppCompatActivity() {

    private var timerList: ArrayList<TimerModel> = ArrayList()
    private lateinit var springCode: String

    private var imageCount: Int = 0
    var imageList = ArrayList<ImageEntity>()
    lateinit var imageUploaderAdapter: ImageUploaderAdapter
    var count: Int = 1
    private var goBack: Boolean = false
    var imageuploadcount: Int = -1

    private lateinit var uploadImageViewModel: UploadImageViewModel

    lateinit var dischargeDataResponseObject: AddDischargeResponseModel

    @Inject
    lateinit var uploadImageRepository: UploadImageRepository
    private var dischargeTime: ArrayList<String> = ArrayList()
    private var imagesList: ArrayList<String> = ArrayList()
    private lateinit var containerString: String
    private var volOfContainer: Float? = null
    private var litresPerSec: ArrayList<Float> = ArrayList()
    private var springName: String? = null

    @Inject
    lateinit var dischargeDataRepository: DischargeDataRepository

    private var addDischargeDataViewModel: AddDischargeDataViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_discharge)
        init()
    }

    private fun getSpringId() {
        var dataIntent: Intent = intent
        springCode = dataIntent.getStringExtra("SpringCode")
        springName = dataIntent.getStringExtra("springCode")
        Log.e("Anirudh", "" + springCode)
    }

    private fun init() {
        initApplicationComponent()
        getSpringId()
        initRepository()
        initViewComponents()
        initListeners()
        initToolbar()
        initRecyclerView()
        initUploadImageApis()
        initApiResponseCalls()
        initClicks()
        initvolumecontrol()
        initSet()
    }

    private fun initSet() {
        val dischargeSpring = "Add discharge details for <b> $springName </b>"
        add_discharge_name.text = Html.fromHtml(dischargeSpring)
    }

    private fun initvolumecontrol() {
        volumeOfContainer.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(3, 2))
    }

    private fun validateData(): Boolean {
        return (
                imageList.size != 0 && timerList.size != 0 && volumeCheck())
    }

    private fun volumeCheck(): Boolean {

        if (!volumeOfContainer.text.toString().trim().equals("")) {

            if (!volumeOfContainer.text.toString().trim().equals(".") &&
                !volumeOfContainer.text.toString().trim().equals("0") &&
                volumeOfContainer.text.toString().toFloat() > 0.1
            )

                return true

        }

        return false
    }

    private fun updateSubmitColor() {
        if (validateData()) {
            submit_discharge_data.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        } else {
            submit_discharge_data.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
        }
    }

    private fun initListeners() {
        initEditTextListener()
        initStopWatchListener()
        updateSubmitColor()

    }

    private fun initStopWatchListener() {
        updateSubmitColor()
    }


    private fun initEditTextListener() {
        volumeOfContainer.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                updateSubmitColor()
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                updateSubmitColor()
            }
        })
    }

    private fun initToolbar() {
        val toolbar = mtoolbar as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = "Add Discharge Data"
    }

    private fun initUploadImageApis() {
        uploadImageViewModel.getUploadImageResponse().observe(this@AddDischargeActivity, Observer {
            //            Log.e("stefy", it?.response!!.imageUrl)
            imagesList.add(it.response.imageUrl)
        })
        uploadImageViewModel.getImageError().observe(this@AddDischargeActivity, Observer {
            Log.e("stefy error", it)
        })
    }

    private fun initRecyclerView() {
        imageDischargeRecyclerView.layoutManager = LinearLayoutManager(this)
        imageUploaderAdapter = ImageUploaderAdapter(this@AddDischargeActivity, imageList, imageUploadInterface)
        imageDischargeRecyclerView.adapter = imageUploaderAdapter
    }

    private val imageUploadInterface = object : ImageUploadInterface {
        override fun onSuccess(position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onCancel(position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun retry(position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onRemove(position: Int) {
            onImageRemove(position)
            imageuploadcount--

        }
    }

    private fun onImageRemove(position: Int) {
        imageList.removeAt(position)
        imageUploaderAdapter.notifyItemRemoved(position)
        imageUploaderAdapter.notifyItemRangeChanged(position, imageList.size)
        updateSubmitColor()
        imageCount--
        onremoved(position)
    }

    private fun onremoved(position: Int) {
        imageDischargeRecyclerView[position].progress.visibility = VISIBLE
        imageDischargeRecyclerView[position].image_loader.visibility = GONE
        imageDischargeRecyclerView[position].upload_status.text = "Uploading"
        if (position == 0 && imageList.size > 0) {
            imageDischargeRecyclerView[position].progress.visibility = GONE
            imageDischargeRecyclerView[position].image_loader.visibility = VISIBLE
            imageDischargeRecyclerView[position].upload_status.text = "Uploaded"

            imageDischargeRecyclerView[position + 1].progress.visibility = VISIBLE
            imageDischargeRecyclerView[position + 1].image_loader.visibility = GONE
            imageDischargeRecyclerView[position + 1].upload_status.text = "Uploading"

            imageUploaderAdapter.notifyDataSetChanged()
        }
        imageUploaderAdapter.notifyItemRemoved(position)


    }


    //    override fun onSupportNavigateUp(): Boolean {
//
//        onBackPressed()
//        return true
//        initApiCalls()
//    }
    override fun onSupportNavigateUp(): Boolean {
        if (goBack) {
            finish()
        } else {
            ArghyamUtils().longToast(this, "Are you sure you want to go back? You will lose the Entered Data")
            startTimer()
        }
        goBack = true
        return true
        initApiCalls()
    }

    override fun onBackPressed() {
        if (goBack) {
            finish()
        } else {
            ArghyamUtils().longToast(this, "Are you sure you want to go back? You will lose the Entered Data")
            startTimer()
        }
        goBack = true
    }


    private fun startTimer() {
        Handler().postDelayed({
            goBack = false
        }, 2000)
    }


    private fun initApiCalls() {
        addDischargeDataViewModel?.getDischargeSuccess()?.observe(this@AddDischargeActivity, Observer {

        })
        addDischargeDataViewModel?.getDischargeError()?.observe(this@AddDischargeActivity, Observer {

        })
    }

    private fun initApplicationComponent() {
        var dataIntent: Intent = intent
        springCode = dataIntent.getStringExtra("SpringCode")
        Log.d("Anirudh", "" + springCode)
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initClicks() {
        initStopWatchButton()
        initViewAttempts()
        initAddDischargeDataSubmit()
        initUploadImageClick()

    }

    private fun initUploadImageClick() {
        image_capture.setOnClickListener {
            if (imageCount < 2)
                openCamera()
            else {
                ArghyamUtils().shortToast(this, "You can capture maximum of two photographs")
            }
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
            startActivityForResult(i, Constants.REQUEST_IMAGE_CAPTURE)
        }

        override fun onPermissionDenied(response: PermissionDeniedResponse) {
            if (response.isPermanentlyDenied) {
                ArghyamUtils().longToast(this@AddDischargeActivity, Constants.CAMERA_PERMISSION_NOT_GRANTED)
                ArghyamUtils().openSettings(this@AddDischargeActivity)
            }
        }

        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
            token.continuePermissionRequest()
        }
    }

    private fun initViewAttempts() {
        view_attempts.setOnClickListener {
            if (attempts.visibility == VISIBLE) {
                attempts.visibility = GONE
                view_attempts_arrow.rotation = 0F
            } else {
                attempts.visibility = VISIBLE
                view_attempts_arrow.rotation = 180F
            }
        }
    }

    private fun initViewComponents() {
        attempt_details.visibility = GONE
    }

    private fun initStopWatchButton() {
        stop_watch.setOnClickListener {
            var intent = Intent(this@AddDischargeActivity, TimerActivity::class.java)
            var args = Bundle()
            args.putSerializable("ArrayList", timerList as Serializable)
            intent.putExtra("Bundle", args)
            startActivityForResult(intent, STOP_WATCH_TIMER_RESULT_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    onImageReceive(data)
                    imageCount++
                }
                updateSubmitColor()
            }

            STOP_WATCH_TIMER_RESULT_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    attempt_details.visibility = View.VISIBLE
                    attempts.visibility = View.GONE
                    timerList = data?.getSerializableExtra("timerList") as ArrayList<TimerModel>
                    initTimerData()
                }
                updateSubmitColor()
            }
        }
    }

    private fun onImageReceive(intent: Intent?) {
        var bitmap: Bitmap? = intent!!.extras.get("data") as Bitmap
        addBitmapToList(bitmap)
        makeUploadApiCall(bitmap)
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

    var body: MultipartBody.Part? = null
    private fun makeUploadApiCall(bitmap: Bitmap?) {
        body = getMultipartBodyFromBitmap(bitmap)
        MyAsyncTask().execute()

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
            uploadImageViewModel.uploadImageApi(this@AddDischargeActivity, body!!)
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
            imageDischargeRecyclerView[imageuploadcount].progress.visibility = GONE
            imageDischargeRecyclerView[imageuploadcount].image_loader.visibility = VISIBLE
            imageDischargeRecyclerView[imageuploadcount].upload_status.text = "Uploaded"
//            Log.e("Anirudh imgupload size f", imageRecyclerView.size.toString())

        }
    }


    private fun addBitmapToList(bitmap: Bitmap?) {
        imageList.add(ImageEntity(count, bitmap!!, "Image" + String.format("%04d", count) + ".jpg", 0))
        imageUploaderAdapter.notifyDataSetChanged()
        count++
        imageuploadcount++

    }

    private fun initTimerData() {
        attempt_details.visibility = VISIBLE
        average_body.visibility = VISIBLE
        view_attempts.visibility = VISIBLE
        averageTimer.text = ArghyamUtils().secondsToMinutes(timerList.map { item -> item.seconds }.average().toInt())
        attempt1.text = ArghyamUtils().secondsToMinutes(timerList[0].seconds)
        attempt2.text = ArghyamUtils().secondsToMinutes(timerList[1].seconds)
        attempt3.text = ArghyamUtils().secondsToMinutes(timerList[2].seconds)
        attempts.visibility = GONE
    }


    private fun initRepository() {
        addDischargeDataViewModel = ViewModelProviders.of(this).get(AddDischargeDataViewModel::class.java)
        addDischargeDataViewModel?.setDischargeDataViewModel(dischargeDataRepository)
        uploadImageViewModel = ViewModelProviders.of(this).get(UploadImageViewModel::class.java)
        uploadImageViewModel.setUploadImageRepository(uploadImageRepository)
    }

    private fun initAddDischargeDataSubmit() {

        submit_discharge_data.setOnClickListener {
            assignDischargeData()
            if (volOfContainer != null && validateData()) {
                addDischargeDataOnClick()
                ArghyamUtils().longToast(this@AddDischargeActivity, "success")

            } else
                showToast()

        }
    }

    private fun showToast() {
        if (volumeOfContainer.text.toString().equals("")) {
            ArghyamUtils().longToast(this, "Add the volume of the container")
            return
        }
        if (timerList.size == 0) {
            ArghyamUtils().longToast(this, "Add the discharge time")
            return
        }
        if (imageList.size == 0) {
            ArghyamUtils().longToast(this, "Add atleast one image of the discharge")
            return
        }
        if (!validateData()) {
            ArghyamUtils().longToast(this@AddDischargeActivity, "Volume of container is invalid")
            return

        }


    }


    private fun initApiResponseCalls() {
        addDischargeDataViewModel?.getDischargeSuccess()?.observe(this@AddDischargeActivity, Observer {
            dischargeDataResponse(it)
            if (addDischargeDataViewModel?.getDischargeSuccess()?.hasObservers()!!) {
                addDischargeDataViewModel?.getDischargeSuccess()?.removeObservers(this@AddDischargeActivity)
            }
        })
        addDischargeDataViewModel?.getDischargeError()?.observe(this@AddDischargeActivity, Observer {
            Log.e("error", it)
        })
    }

    private fun dischargeDataResponse(responseModel: ResponseModel) {
        dischargeDataResponseObject = Gson().fromJson(
            ArghyamUtils().convertToString(responseModel.response.responseObject),
            object : TypeToken<AddDischargeResponseModel>() {}.type
        )

        springCode = dischargeDataResponseObject.springCode
        gotoSpringDetailsActivity(dischargeDataResponseObject)

    }

    private fun gotoSpringDetailsActivity(dischargeDataResponseObject: AddDischargeResponseModel) {


//        showNotification(dischargeDataResponseObject)


        SharedPreferenceFactory(this).getInt(NOTIFICATION_COUNT)?.let {
            SharedPreferenceFactory(this@AddDischargeActivity).setInt(
                NOTIFICATION_COUNT,
                it + 1
            )
        }

        val intent = Intent(this@AddDischargeActivity, SpringDetailsActivity::class.java)
        intent.putExtra("SpringCode", springCode)
        intent.putExtra("springCode", springName)
        intent.putExtra("SpringCode", dischargeDataResponseObject.springCode)
        Log.e("Code", dischargeDataResponseObject.springCode)
        startActivity(intent)
        finish()
    }



    private fun assignDischargeData() {
        containerString = volumeOfContainer.text.toString()
        if (!containerString.equals("") && !containerString.equals("."))
            volOfContainer = containerString.toFloat()
        if (timerList.size != 0) {
            dischargeTime.add(timerList[0].seconds.toString())
            dischargeTime.add(timerList[1].seconds.toString())
            dischargeTime.add(timerList[2].seconds.toString())
        }
        if (!containerString.equals("") && !containerString.equals(".") && timerList.size != 0) {
            val lps: Float = volOfContainer!! / timerList.map { item -> item.seconds }.average().toInt()
            litresPerSec.add(lps)
            litresPerSec.add(lps)

        }
    }


    private fun addDischargeDataOnClick() {
        val months: ArrayList<String> = ArrayList()
        months.add("January")
        months.add("April")
        var createSpringObject = RequestModel(
            id = CREATE_DISCHARGE_DATA,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = DischargeDataModel(
                dischargeData = DischargeModel(
                    springCode = springCode,
                    dischargeTime = dischargeTime,
                    volumeOfContainer = volOfContainer,
                    litresPerSecond = litresPerSec,
                    status = "Created",
                    seasonality = "Sessional",
                    months = months,
                    images = imagesList,
                    userId = SharedPreferenceFactory(this@AddDischargeActivity).getString(Constants.USER_ID)!!
                )
            )
        )
        addDischargeDataViewModel?.addDischargeApi(this, springCode, createSpringObject)
    }
}