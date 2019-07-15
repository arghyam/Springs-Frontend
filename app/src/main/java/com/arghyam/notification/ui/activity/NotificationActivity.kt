package com.arghyam.notification.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.arghyam.commons.utils.Constants.GET_ALL_SPRINGS_ID
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.notification.adapter.NotificationAdapter
import com.arghyam.notification.model.NotificationDataModel
import com.arghyam.notification.model.NotificationModel
import com.arghyam.notification.model.NotificationResponseModel
import com.arghyam.notification.model.notificationSpringModel
import com.arghyam.notification.repository.NotificationRepository
import com.arghyam.notification.viewmodel.NotificationViewModel
import com.arghyam.springdetails.repository.SpringDetailsRepository
import com.arghyam.springdetails.viewmodel.SpringDetailsViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_notification.*
import javax.inject.Inject
import kotlin.collections.ArrayList


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
        initRepository()

        initNotificationApi()
        initNotificationResponse()

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
            for (i in 0 until notificationResponseModel.notifications.size) {
                springCode = notificationResponseModel.notifications[i].springCode
            }
            loadNotification(notificationResponseModel)

        }

    }

    private fun loadNotification(notificationResponseModel: NotificationResponseModel) {
        listView = notification_list
        Log.e("Anirudh", "activityloaded")
        if (notificationResponseModel.notifications.isNotEmpty()) {
//
//            Collections.sort(notificationResponseModel.notifications) { o1, o2 ->
//                o1.createdAt.compareTo(o2.createdAt)
//            }
//            Collections.reverse(notificationResponseModel.notifications)


            Log.e("Anirudh", notificationResponseModel.notifications.size.toString())
            for (i in 0 until notificationResponseModel.notifications.size) {
                Log.e("Anirudh spring", notificationResponseModel.notifications[i].springCode)
                springCode = notificationResponseModel.notifications[i].springCode
                dataModels?.add(
                    NotificationDataModel(
                        "Spring discharge data was submitted by " + notificationResponseModel.notifications[i].firstName,
//                        ArghyamUtils().getTime(notificationResponseModel.notifications[i].createdAt),
//                        ArghyamUtils().getDate(notificationResponseModel.notifications[i].createdAt)
                        ArghyamUtils().epochToTime(notificationResponseModel.notifications[i].createdAt),
                        ArghyamUtils().epochToDateFormat(notificationResponseModel.notifications[i].createdAt)
                    )
                )
                adapter = this.dataModels?.let { NotificationAdapter(applicationContext, it) }
                listView.adapter = adapter
                Log.e("Anirudh", listView.adapter.toString())
            }
            listView.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView: AdapterView<*>, view: View, position: Int, l: Long ->
                    val (notification, time, date) = dataModels!![position]
                    Log.e(
                        "Anirudh",
                        notificationResponseModel.notifications[position].dischargeDataOsid + "  " + notificationResponseModel.notifications[position].springCode
                    )
                    val intent = Intent(this, DisplayDischargeDataActivity::class.java)
                    intent.putExtra(
                        "DischargeOSID",
                        notificationResponseModel.notifications[position].dischargeDataOsid
                    )
                    intent.putExtra(
                        "osid",
                        notificationResponseModel.notifications[position].osid
                    )
                    intent.putExtra(
                        "submittedBy",
                        notificationResponseModel.notifications[position].firstName
                    )
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
                    type = "notifications"
                )
            )
        )


        notificationViewModel?.notificationApi(this, notificationObject)


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




