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
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.notification.adapter.NotificationAdapter
import com.arghyam.notification.model.NotificationDataModel
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

    var springCode: String? = null
    private var springName: String? = null


    private var adapter: NotificationAdapter? = null
    var dataModels: ArrayList<NotificationDataModel>? = ArrayList()
    private lateinit var listView: ListView

    private var springDetailsViewModel: SpringDetailsViewModel? = null


    @Inject
    lateinit var springDetailsRepository: SpringDetailsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        initToolBar()

        init()

//        assert(supportActionBar != null)   //null check
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        title = "Notifications"


        listView = notification_list
        Log.e("Anirudh", "activityloaded")
        dataModels?.add(
            NotificationDataModel(
                "Spring discharge data was submitted by Ramesh Agarwal ",
                "10:45 AM",
                "Apr 21, 2019"
            )
        )
        dataModels?.add(
            NotificationDataModel(
                "Spring discharge data was approved by Ramesh Agarwal ",
                "11:45 AM",
                "Apr 21, 2019"
            )
        )
        dataModels?.add(
            NotificationDataModel(
                "Spring discharge data was rejected by Ramesh Agarwal ",
                "1:04 PM",
                "Apr 22, 2019"
            )
        )
        dataModels?.add(
            NotificationDataModel(
                "Anand gave you admin access",
                "1:44 PM",
                "Jun 17, 2019"
            )
        )
        dataModels?.add(
            NotificationDataModel(
                "Anand removed your reviewer access",
                "3:44 PM",
                "Jun 17, 2019"
            )
        )

        adapter = this.dataModels?.let { NotificationAdapter(applicationContext, it) }
        listView.adapter = adapter
        Log.e("Anirudh", listView.adapter.toString())

        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, view: View, position: Int, l: Long ->
                val (notification, time, date) = dataModels!![position]
                Log.e("Anirudh", "Clicked$notification $time $date")
                this.startActivity(Intent(this, DisplayDischargeDataActivity::class.java))
        }
    }

    private fun init() {
        initComponent()
        getSpringId()
        initRepository()
        initSpringDetails()
        initSpringDetailsResponse()

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

        Log.d("springResponse-4", ""+ springProfileResponse.springName)
//        initDischargeData(springProfileResponse)
//        dischargeSample(springProfileResponse)
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




