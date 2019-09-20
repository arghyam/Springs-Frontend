package com.arghyam.myactivity.ui


import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.GET_ALL_SPRINGS_ID
import com.arghyam.commons.utils.Constants.USER_ID
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.model.NotificationCountResponseModel
import com.arghyam.landing.repository.NotificationCountRepository
import com.arghyam.landing.viewmodel.NotificationCountViewModel
import com.arghyam.myactivity.adapter.MyActivityAdapter
import com.arghyam.myactivity.model.AllActivitiesModel
import com.arghyam.myactivity.model.MyActivitiesRequest
import com.arghyam.myactivity.model.MyActivityModel
import com.arghyam.myactivity.model.RequestMyActivitiesModel
import com.arghyam.myactivity.repository.MyActivitiesRepository
import com.arghyam.myactivity.viewmodel.MyActivitiesViewModel
import com.arghyam.notification.model.NotificationModel
import com.arghyam.notification.model.notificationSpringModel
import com.arghyam.notification.ui.activity.NotificationActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_my_activity.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class MyActivityFragment : Fragment() {

    private lateinit var mMyActivitiesViewModel: MyActivitiesViewModel

    @Inject
    lateinit var mMyActivitiesRepository: MyActivitiesRepository


    @Inject
    lateinit var notificationCountRepository: NotificationCountRepository

    private var notificationCountViewModel: NotificationCountViewModel? = null


    companion object {
        fun newInstance(): MyActivityFragment {
            var fragmentMyActivity = MyActivityFragment()
            var args = Bundle()
            fragmentMyActivity.arguments = args
            return fragmentMyActivity
        }

    }

    private fun initbell(notificationCount: Int) {
        if (notificationCount > 0) {
            badge.visibility = View.VISIBLE
            notification_count.visibility = View.VISIBLE
            notification_count.text = notificationCount.toString()
        }

    }

    private fun initNotifications() {
        if (context?.let { SharedPreferenceFactory(it).getString(Constants.ACCESS_TOKEN) } == "") {
            notauser.visibility = View.VISIBLE
            myActivityRecyclerView.visibility = GONE
            bell.visibility = GONE
            notification_bell.visibility = GONE
            initsigninbutton()
        } else {
            notauser.visibility = GONE
            bell.visibility = View.VISIBLE
        }
        bell.setOnClickListener {
            Log.e("Anirudh", "bell clicked")
            this.startActivity(Intent(activity!!, NotificationActivity::class.java))
        }
    }

    private fun initsigninbutton() {
        sign_in_button_myActivity.setOnClickListener {
            startActivity(Intent(activity!!, LoginActivity::class.java))
            activity!!.finish()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_my_activity, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initComponent()
        initRepository()
        observeData()
        initNotificationCountApi()
        initNotificationCountResponse()
        sendRequest()
        val toolbar = toolbar as Toolbar
        toolbar.title = "My Activity"
        initNotifications()
    }

    private fun initNotificationCountResponse() {
        Log.d("success", "inside")
        notificationCountViewModel?.getNotificationCountResponse()?.observe(this, Observer {

            saveNotificationCountData(it)
        })

        notificationCountViewModel?.notificationCountError()?.observe(this, Observer {
            Log.e("error---", it)
            if (notificationCountViewModel?.notificationCountError()?.hasObservers()!!) {
                notificationCountViewModel?.notificationCountError()?.removeObservers(this)
            }
        })
    }


    private fun saveNotificationCountData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            Log.d("success_notication", "yes")

            var notificationCountResponseModel: NotificationCountResponseModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<NotificationCountResponseModel>() {}.type
            )

            Log.d("notificationCount--act", notificationCountResponseModel.notificationCount.toString())
            var notificationCountBell = notificationCountResponseModel.notificationCount

            initbell(notificationCountBell)
        }
    }

    private fun initNotificationCountApi() {

        var userId = SharedPreferenceFactory(activity!!.applicationContext).getString(Constants.USER_ID)!!

        var notificationCountObject = RequestModel(
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
        notificationCountViewModel?.notificationCountApi(context!!, userId, notificationCountObject)

    }


    private fun initRecyclerView(responseData: AllActivitiesModel) {

        if (responseData.activities.size == 0) {
            no_activities.visibility = View.VISIBLE
        } else {
            responseData.activities.sortWith(Comparator { o1, o2 ->
                o1.createdAt.compareTo(o2.createdAt)
            })
            responseData.activities.reverse()

            myActivityRecyclerView.layoutManager = LinearLayoutManager(activity)
            val adapter = activity?.let { MyActivityAdapter(responseData.activities, it) }
            myActivityRecyclerView.adapter = adapter

        }
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initRepository() {
        mMyActivitiesViewModel = ViewModelProviders.of(this).get(MyActivitiesViewModel::class.java)
        mMyActivitiesViewModel.setMyActivitiesRepository(mMyActivitiesRepository)

        notificationCountViewModel = ViewModelProviders.of(this).get(NotificationCountViewModel::class.java)
        notificationCountViewModel?.setNotificationCountRepository(notificationCountRepository)

    }

    private fun observeData() {
        mMyActivitiesViewModel.getMyActivitiesSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("My Activities", "Success")
            initData(it)
        })

        mMyActivitiesViewModel.getMyActivitiesError().observe(this, androidx.lifecycle.Observer {
            Log.d("My Activities", "Api Error")
        })
    }

    private fun initData(responseModel: ResponseModel?) {
        saveMyActivities(responseModel)
    }

    private fun saveMyActivities(responseModel: ResponseModel?) {
        Log.e("responseMyActivities", ArghyamUtils().convertToString(responseModel!!.response.responseObject))

        var responseData: AllActivitiesModel = Gson().fromJson(
            ArghyamUtils().convertToString(responseModel.response.responseObject),
            object : TypeToken<AllActivitiesModel>() {}.type
        )
        initRecyclerView(responseData)

    }

    private fun sendRequest() {
        var mRequestData = RequestModel(
            id = GET_ALL_SPRINGS_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = RequestMyActivitiesModel(
                activities = MyActivitiesRequest(
                    userId = SharedPreferenceFactory(activity!!.applicationContext).getString(USER_ID)!!
                )
            )
        )
        makeApiCall(mRequestData )
    }

    private fun makeApiCall(mRequestData: RequestModel) {
        SharedPreferenceFactory(activity!!.applicationContext).getString(USER_ID)?.let {
            mMyActivitiesViewModel.myActivitiesApi(activity!!.applicationContext,
                it,mRequestData)
        }

    }

}
