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
import com.arghyam.notification.adapter.NotificationAdapter
import com.arghyam.notification.repository.NotificationRepository
import com.arghyam.notification.viewmodel.NotificationViewModel
import com.arghyam.springdetails.repository.SpringDetailsRepository
import com.arghyam.springdetails.viewmodel.SpringDetailsViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.activity_notification.custom_toolbar
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject
import com.arghyam.notification.model.*


class NotificationActivity : AppCompatActivity() {

    var springCode: String? = ""
    private var springName: String? = ""


    private var adapter: NotificationAdapter? = null
    var dataModels: ArrayList<NotificationDataModel>? = ArrayList()
    private lateinit var listView: ListView

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
        notification_progressBar.visibility = VISIBLE

        initRepository()

        initNotificationApi()
        initNotificationResponse()

    }

    private fun initNotificationResponse() {
        notificationViewModel?.getNotificationResponse()?.observe(this, Observer {
            notification_progressBar.visibility = GONE
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
            val deletedNotificationList= ArrayList<AllNotificationModel>()

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
                            ArghyamUtils().epochToDateFormat(deletedNotificationList[i].createdAt)
                        )
                    )
                    adapter = this.dataModels?.let { NotificationAdapter(applicationContext, it) }
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
                    if(deletedNotificationList[position].status == "Created")
                        startActivity(intent)
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

        notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        notificationViewModel?.setNotificationRepository(notificationRepository)


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


}




