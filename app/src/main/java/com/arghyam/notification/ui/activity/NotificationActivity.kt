package com.arghyam.notification.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.notification.`interface`.NotificationInterface
import com.arghyam.notification.adapter.NotificationAdapter
import com.arghyam.notification.repository.NotificationRepository
import com.arghyam.notification.viewmodel.NotificationViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.activity_notification.custom_toolbar
import javax.inject.Inject
import com.arghyam.notification.model.*
import com.arghyam.notification.repository.PrivateReviewRepository
import com.arghyam.notification.viewmodel.PrivateReviewViewModel


class NotificationActivity : AppCompatActivity() {

    var springCode: String? = ""
    private var springName: String? = ""


    private var adapter: NotificationAdapter? = null
    var dataModels: ArrayList<NotificationDataModel>? = ArrayList()
    private lateinit var listView: ListView

    private var privateReviewViewModel: PrivateReviewViewModel? = null
    private val deletedNotificationList= ArrayList<AllNotificationModel>()
    private var notificationViewModel: NotificationViewModel? = null

    @Inject
    lateinit var notificationRepository: NotificationRepository
    @Inject
    lateinit var privateReviewRepository: PrivateReviewRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        initToolBar()

        init()
    }

    private fun init() {
        initComponent()
        notification_progressBar.visibility = VISIBLE
        initRepository()
        initObservers()
        initNotificationApi()

    }

    fun privateAccessReview(status: String,springCode:String,osid:String,adminId:String,userId:String) {
        var notificationObject = RequestModel(
            id = GET_ALL_SPRINGS_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = PrivateReviewModel(
                Reviewer = PrivateReviewDataModel(
                    status = status,
                    springCode = springCode,
                    osid = osid,
                    adminId = adminId,
                    userId = userId
                )
            )
        )
        privateReviewViewModel?.privateReviewApi(notificationObject)
        if (status.toLowerCase() == "accepted")
            ArghyamUtils().longToast(this,"You have approved the request to view spring details")
        else if (status.toLowerCase() == "rejected")
            ArghyamUtils().longToast(this,"You have rejected the request to view spring details")
        val intent = Intent(this, LandingActivity::class.java)
        startActivity(intent)
    }

    private fun initObservers() {
        //Notification Observers
        notificationViewModel?.getNotificationResponse()?.observe(this, Observer {
            notification_progressBar.visibility = GONE
            saveNotificationData(it)
        })
        notificationViewModel?.getNotifyError()?.observe(this, Observer {
            Log.e("error---", it)
            if (notificationViewModel?.getNotifyError()?.hasObservers()!!) {
                notificationViewModel?.getNotifyError()?.removeObservers(this)
            }
        })
        //Private Review Observers
        privateReviewViewModel?.getPrivateReviewResponse()?.observe(this, Observer {
            Log.d("Private Review", "success")
        })
        privateReviewViewModel?.getPrivateReviewerError()?.observe(this, Observer {
            Log.e("error---", it)
            if (privateReviewViewModel?.getPrivateReviewerError()?.hasObservers()!!) {
                privateReviewViewModel?.getPrivateReviewerError()?.removeObservers(this)
            }
        })
    }

    private fun saveNotificationData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            Log.d("success_i", "yes")

            var notificationResponseModel: NotificationResponseModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<NotificationResponseModel>() {}.type
            )
            for (i in 0 until notificationResponseModel.notifications.size) {
                springCode = notificationResponseModel.notifications[i].springCode
            }
            loadNotification(notificationResponseModel)

        }

    }

    private fun loadNotification(notificationResponseModel: NotificationResponseModel) {
        listView = notification_list
        if (notificationResponseModel.notifications.isNotEmpty()) {
            noNotifications.visibility= GONE
            notification_list.visibility = VISIBLE

            for(i in 0 until notificationResponseModel.notifications.size){
                if (notificationResponseModel.notifications[i].status.toLowerCase()!="done"){
                    deletedNotificationList.add(notificationResponseModel.notifications[i])
                }
            }
            for (i in 0 until deletedNotificationList.size) {
                    Log.e("Notification spring","notification"+ deletedNotificationList[i].firstName+"     "
                            +deletedNotificationList[i].dischargeDataOsid+"    "
                            +deletedNotificationList[i].status)
                    springCode = deletedNotificationList[i].springCode
                    dataModels?.add(
                        NotificationDataModel(
                            " "+ deletedNotificationList[i].notificationTitle ,
                            ArghyamUtils().epochToTime(deletedNotificationList[i].createdAt),
                            ArghyamUtils().epochToDateFormat(deletedNotificationList[i].createdAt),
                            deletedNotificationList[i].springCode,
                            deletedNotificationList[i].osid,
                            deletedNotificationList[i].userId,
                            deletedNotificationList[i].requesterId
                        )
                    )
                    adapter = this.dataModels?.let { NotificationAdapter(applicationContext, it,notificationInterface) }
                    listView.adapter = adapter
            }
            listView.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView: AdapterView<*>, view: View, position: Int, l: Long ->
                    val (notification, time, date) = dataModels!![position]
                    Log.e(
                        "Notification spring", "notification"+
                                deletedNotificationList[position].dischargeDataOsid + "  " + deletedNotificationList[position].firstName+"    "
                                +deletedNotificationList[position].status
                    )
                    val intent = Intent(this, DisplayDischargeDataActivity::class.java)
                    intent.putExtra(
                        "DischargeOSID",
                        deletedNotificationList[position].dischargeDataOsid
                    )
                    intent.putExtra(
                        "osid",
                        deletedNotificationList[position].osid
                    )
                    intent.putExtra(
                        "submittedBy",
                        deletedNotificationList[position].firstName
                    )
                    intent.putExtra("SpringCode", deletedNotificationList[position].springCode)
                    intent.putExtra("submittedById",deletedNotificationList[position].userId)
                    if(deletedNotificationList[position].status == "Created") {
                        startActivity(intent)
                        finish()
                    }
                    else
                        listView.isClickable = false
                }

        } else{
            noNotifications.visibility= VISIBLE
            notification_list.visibility = GONE
        }
    }

    private fun initNotificationApi() {

        var userId = SharedPreferenceFactory(this@NotificationActivity).getString(Constants.USER_ID)!!

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
                    type = "notifications"
                )
            )
        )
        notificationViewModel?.notificationApi(this,userId, notificationObject)


    }

    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initRepository() {
        //GetAllNotifications
        notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        notificationViewModel?.setNotificationRepository(notificationRepository)

        //Private Notification Review
        privateReviewViewModel = ViewModelProviders.of(this).get(PrivateReviewViewModel::class.java)
        privateReviewViewModel?.setPrivateReviewRepository(privateReviewRepository)
    }

    private fun initToolBar() {
        setSupportActionBar(custom_toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notifications"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private var notificationInterface: NotificationInterface = object : NotificationInterface {
        override fun onPrivateReview(status: String,springCode:String,osid:String,adminId:String,requesterId:String) {
            privateAccessReview(status,springCode,osid,adminId,requesterId)
        }
    }
}




