package com.arghyam.notification.ui.activity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.REVIEWER_DATA_ID
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.ui.fragment.HomeFragment
import com.arghyam.notification.model.ReviewerDataModel
import com.arghyam.notification.model.ReviewerModel
import com.arghyam.notification.repository.ReviewerDataRepository
import com.arghyam.notification.viewmodel.ReviewerDataViewModel
import com.arghyam.springdetails.models.DischargeData
import com.arghyam.springdetails.models.RequestSpringDetailsDataModel
import com.arghyam.springdetails.models.SpringDetailsModel
import com.arghyam.springdetails.models.SpringProfileResponse
import com.arghyam.springdetails.repository.SpringDetailsRepository
import com.arghyam.springdetails.ui.fragments.DischargeDataFragment
import com.arghyam.springdetails.viewmodel.SpringDetailsViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_display_discharge_data.*
import javax.inject.Inject
import android.R.attr.fragment
import androidx.fragment.app.Fragment
import com.arghyam.landing.ui.activity.LandingActivity


class DisplayDischargeDataActivity : AppCompatActivity() {

    private lateinit var springCode: String

    private lateinit var userId: String

    private var springDetailsViewModel: SpringDetailsViewModel? = null

    private var reviewerDataViewModel: ReviewerDataViewModel? = null

    private var status: String = ""


    @Inject
    lateinit var springDetailsRepository: SpringDetailsRepository

    @Inject
    lateinit var reviewerDataRepository: ReviewerDataRepository

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_discharge_data)

        initToolBar()
        init()
    }

    private fun init() {
        initComponent()
//        getSpringId()
        initRepository()

        initSpringDetails()

        initSpringDetailsResponse()
        initClick()


    }

    private fun initClick() {
        reject_button.setOnClickListener {

            val status = "Rejected"
            reviewerApi(status)
            reviewerResponse()

        }

        accept_button.setOnClickListener {

            val status = "Accepted"
            reviewerApi(status)
            reviewerResponse()

        }
    }

    private fun reviewerResponse() {
        reviewerDataViewModel?.getReviewerDataResponse()?.observe(this@DisplayDischargeDataActivity, Observer {
            reviewDataResponse(it)
            if (reviewerDataViewModel?.getReviewerDataResponse()?.hasObservers()!!) {
                reviewerDataViewModel?.getReviewerDataResponse()?.removeObservers(this@DisplayDischargeDataActivity)
            }
        })
        reviewerDataViewModel?.getReviewError()?.observe(this@DisplayDischargeDataActivity, Observer {
            Log.e("error", it)
        })
    }

    private fun reviewDataResponse(responseModel: ResponseModel) {
        Log.d("response---data", responseModel.response.responseCode)
        Log.d("response---data", responseModel.response.responseStatus)

        if(responseModel.response.responseCode == "451"){
            ArghyamUtils().longToast(this, getString(R.string.reviewer_rejected))
            gotoLandngActivity(responseModel)

        }


        if(responseModel.response.responseCode == "200"){
            ArghyamUtils().longToast(this, getString(R.string.reviewer_accepted))
            gotoLandngActivity(responseModel)

        }

//        showNotification(responseModel)

    }

    private fun gotoLandngActivity(responseModel: ResponseModel) {

//        val fragment = HomeFragment.newInstance()
////        addFragment(fragment)

        var intent = Intent(this@DisplayDischargeDataActivity, LandingActivity::class.java)
        startActivity(intent)

    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }

    private fun showNotification(responseModel: ResponseModel) {


        Log.d("notification", "notification rejected")

        if (responseModel.response.responseCode == "451") {

            var builder: NotificationCompat.Builder =
                NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_ui_bell) //set icon for notification
                    .setContentTitle("Notification") //set title of notification
                    .setContentText("Rejected discharge data")//this is notification message
                    .setAutoCancel(true) // makes auto cancel of notification
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT) //set priority of notification

            var notificationIntent = Intent(this@DisplayDischargeDataActivity, DischargeDataFragment::class.java)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            //notification message will get at NotificationView
            notificationIntent.putExtra("message", "Rejected discharge data")
            notificationIntent.putExtra("responseStatus", responseModel.response.responseStatus)


            var pendingIntent: PendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            builder.setContentIntent(pendingIntent)


            // Add as notification
            var manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, builder.build())
        }


    }

    private fun reviewerApi(status: String) {

        var reviewerDataObject = RequestModel(
            id = REVIEWER_DATA_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = ReviewerDataModel(
                Reviewer = ReviewerModel(

                    osid = "1-94a05215-9b31-4309-ae4d-73b64188bd15",
                    userId = SharedPreferenceFactory(this@DisplayDischargeDataActivity).getString(Constants.USER_ID)!!,
                    status = status


                )
            )
        )
        Log.d("status-----", status)
        reviewerDataViewModel?.reviewerApi(this, reviewerDataObject)

    }

    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }


    private fun getSpringId() {
        var dataIntent: Intent = intent
        springCode = dataIntent.getStringExtra("SpringCode")
        userId = dataIntent.getStringExtra("userId")

        Log.d("userId--", userId)
        Log.e("Code", springCode)
    }

    private fun initSpringDetailsResponse() {
        springDetailsViewModel?.getSpringDetailsResponse()?.observe(this, Observer {
            //            initDischargeAdapter(it)
            saveSpringDetailsData(it)
            if (springDetailsViewModel?.getSpringDetailsResponse()?.hasObservers()!!) {
                springDetailsViewModel?.getSpringDetailsResponse()?.removeObservers(this)
            }
        })
        springDetailsViewModel?.getSpringError()?.observe(this, Observer {
            Log.e("stefy error", it)
        })

        springDetailsViewModel?.getSpringFailure()?.observe(this, Observer {
            Log.e("stefy===", it)
        })
    }


    private fun saveSpringDetailsData(responseModel: ResponseModel) {

        var springProfileResponse: SpringProfileResponse = Gson().fromJson(
            ArghyamUtils().convertToString(responseModel.response.responseObject),
            object : TypeToken<SpringProfileResponse>() {}.type
        )
//        initDischargeData(springProfileResponse)
        dischargeSample(springProfileResponse)
    }

    private fun dischargeSample(springProfileResponse: SpringProfileResponse): ArrayList<DischargeData> {

        Log.d("dischargeData--", springProfileResponse.extraInformation.dischargeData.toString())
        return springProfileResponse.extraInformation.dischargeData
    }


    private fun initSpringDetails() {

//        Log.e("SpringCode", "Spring " + springCode)
        var springDetailObject = RequestModel(
            id = Constants.GET_ALL_SPRINGS_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = RequestSpringDetailsDataModel(
                springs = SpringDetailsModel(
                    springCode = "0hf8GI"
                )
            )
        )
        springDetailsViewModel?.springDetailsApi(this, springDetailObject)

    }

    private fun initRepository() {
        springDetailsViewModel = ViewModelProviders.of(this).get(SpringDetailsViewModel::class.java)
        springDetailsViewModel?.setSpringDetailsRepository(springDetailsRepository)

        reviewerDataViewModel = ViewModelProviders.of(this).get(ReviewerDataViewModel::class.java)
        reviewerDataViewModel?.setReviewerDataRepository(reviewerDataRepository)
    }

    private fun initToolBar() {
        setSupportActionBar(display_discharge_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
