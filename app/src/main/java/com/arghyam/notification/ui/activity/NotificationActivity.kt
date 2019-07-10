package com.arghyam.notification.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.GET_ALL_SPRINGS_ID
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.notification.adapter.NotificationAdapter
import com.arghyam.notification.model.*
import com.arghyam.notification.repository.NotificationRepository
import com.arghyam.notification.viewmodel.NotificationViewModel
import com.arghyam.springdetails.models.RequestSpringDetailsDataModel
import com.arghyam.springdetails.models.SpringDetailsModel
import com.arghyam.springdetails.models.SpringProfileResponse
import com.arghyam.springdetails.repository.SpringDetailsRepository
import com.arghyam.springdetails.viewmodel.SpringDetailsViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.notification_listview.*
import javax.inject.Inject

class NotificationActivity : AppCompatActivity() {

    var springCode: String? = ""
    private var springName: String? = ""


    private var adapter: NotificationAdapter? = null
    var dataModels: ArrayList<NotificationDataModel>? = ArrayList()
    private lateinit var listView: ListView
    private var notificationList = ArrayList<AllNotificationModel>()

    private var springDetailsViewModel: SpringDetailsViewModel? = null

    private var notificationViewModel: NotificationViewModel? = null


    @Inject
    lateinit var notificationRepository: NotificationRepository
    @Inject
    lateinit var springDetailsRepository: SpringDetailsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        initToolBar()

        init()
    }

    private fun init() {
        initComponent()
//        getSpringId()
        initRepository()

        initNotificationApi()
        initNotificationResponse()

//        initSpringDetails()
        initSpringDetailsResponse()

    }

    private fun initNotificationResponse() {
        notificationViewModel?.getNotificationResponse()?.observe(this, Observer {

            saveNotificationlData(it)
        })
        notificationViewModel?.getNotifyError()?.observe(this, Observer {
            Log.e("error---", it)
            if (notificationViewModel?.getNotifyError()?.hasObservers()!!) {
                notificationViewModel?.getNotifyError()?.removeObservers(this)
            }
        })
    }

    private fun saveNotificationlData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            Log.d("success_i", "yes")

            var notificationResponseModel: NotificationResponseModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<NotificationResponseModel>() {}.type
            )
            for (i in 0 until notificationResponseModel.notifications.size){
                springCode = notificationResponseModel.notifications[i].springCode
                initSpringDetails()
            }
            loadNotification(notificationResponseModel)

        }

    }

    private fun loadNotification(notificationResponseModel: NotificationResponseModel) {
        listView = notification_list
        Log.e("Anirudh", "activityloaded")
        if (notificationResponseModel.notifications.isNotEmpty()) {
            Log.e("Anirudh",notificationResponseModel.notifications.size.toString())
            for (i in 0 until notificationResponseModel.notifications.size) {
                Log.e("Anirudh spring",notificationResponseModel.notifications[i].springCode)
                if (notificationResponseModel.notifications[i].status == "Created" &&
                    SharedPreferenceFactory(this@NotificationActivity)
                        .getString(Constants.USER_ID) == "f4e5dd50-606b-4726-95c3-aa92bd0bc94e") {
                    springCode = notificationResponseModel.notifications[i].springCode
                    dataModels?.add(
                        NotificationDataModel(springName +
                                " discharge data was submitted by " + notificationResponseModel.notifications[i].userName,
                            "11:45 AM",
                            "Apr 21, 2019"
                        )
                    )
                    adapter = this.dataModels?.let { NotificationAdapter(applicationContext, it) }
                    listView.adapter = adapter
                    Log.e("Anirudh", listView.adapter.toString())

                }
            }
            listView.onItemClickListener =
                AdapterView.OnItemClickListener {adapterView: AdapterView<*>, view: View, position: Int, l: Long ->
                    val (notification, time, date) = dataModels!![position]
                    Log.e("Anirudh", notificationResponseModel.notifications[position].dischargeDataOsid + "  "+ notificationResponseModel.notifications[position].springCode)
                    val intent = Intent(this, DisplayDischargeDataActivity::class.java)
                    intent.putExtra("DischargeOSID",notificationResponseModel.notifications[position].dischargeDataOsid)
                    intent.putExtra("SpringCode", notificationResponseModel.notifications[position].springCode)
                    startActivity(intent)
                }

        }
    }

    private fun initNotificationApi() {
        var notificationObject = RequestModel(
            id = GET_ALL_SPRINGS_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = notificationSpringModel(
                notifications = NotificationModel(
                    type =  "notifications"
                )
            )
        )


        notificationViewModel?.notificationApi(this, notificationObject)


    }

    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun getSpringId() {
        var dataIntent: Intent = intent
        springCode = dataIntent.getStringExtra("SpringCode")
        Log.e("code", "" + springCode)
    }

    private fun initSpringDetailsResponse() {
        springDetailsViewModel?.getSpringDetailsResponse()?.observe(this, Observer {
            //            initDischargeAdapter(it)
            saveSpringDetailsData(it)
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

        Log.e("springResponse", ""+ springProfileResponse.springName)
        springName = springProfileResponse.springName
        adapter?.notifyDataSetChanged()
//        initDischargeData(springProfileResponse)
//        dischargeSample(springProfileResponse)
    }


    private fun initSpringDetails() {

        Log.e("SpringCode", "Spring " + springCode)
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
                    springCode = springCode
                )
            )
        )
        springDetailsViewModel?.springDetailsApi(this, springDetailObject)

    }

    private fun initRepository() {
        springDetailsViewModel = ViewModelProviders.of(this).get(SpringDetailsViewModel::class.java)
        springDetailsViewModel?.setSpringDetailsRepository(springDetailsRepository)


        notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        notificationViewModel?.setNotificationRepository(notificationRepository)


    }

        private fun initToolBar() {
            setSupportActionBar(notification_toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        override fun onSupportNavigateUp(): Boolean {
            onBackPressed()
            return true
        }



}




