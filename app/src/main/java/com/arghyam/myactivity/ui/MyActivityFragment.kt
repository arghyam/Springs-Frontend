package com.arghyam.myactivity.ui


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig

import com.arghyam.R
import com.arghyam.additionalDetails.model.AdditionalDetailsModel
import com.arghyam.additionalDetails.model.RequestAdditionalDetailsDataModel
import com.arghyam.additionalDetails.repository.AdditionalDetailsRepository
import com.arghyam.additionalDetails.viewmodel.AddAdditionalDetailsViewModel
import com.arghyam.admin.model.AllUsersDataModel
import com.arghyam.admin.ui.User
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.GET_ALL_SPRINGS_ID
import com.arghyam.commons.utils.Constants.USER_ID
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.myactivity.adapter.MyActivityAdapter
import com.arghyam.myactivity.model.AllActivitiesModel
import com.arghyam.myactivity.model.MyActivitiesRequest
import com.arghyam.myactivity.model.MyActivityModel
import com.arghyam.myactivity.model.RequestMyActivitiesModel
import com.arghyam.myactivity.repository.MyActivitiesRepository
import com.arghyam.myactivity.viewmodel.MyActivitiesViewModel
import com.arghyam.notification.ui.activity.NotificationActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_my_activity.*
import kotlinx.android.synthetic.main.fragment_my_activity.notauser
import kotlinx.android.synthetic.main.fragment_my_activity.toolbar
import kotlinx.android.synthetic.main.custom_toolbar.*
import javax.inject.Inject


class MyActivityFragment : Fragment() {
    private var notification: Boolean = true

    private var myActivityList = ArrayList<MyActivityModel>()
    private lateinit var mMyActivitiesViewModel: MyActivitiesViewModel

    @Inject
    lateinit var mMyActivitiesRepository: MyActivitiesRepository

    companion object {
        fun newInstance(): MyActivityFragment {
            var fragmentMyActivity = MyActivityFragment()
            var args = Bundle()
            fragmentMyActivity.arguments = args
            return fragmentMyActivity
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun initbell(notificationCount: Int) {
        if (notificationCount > 0) {
//            badge.visibility = VISIBLE
//            notification_count.visibility = VISIBLE
//
//            notification_count.text = notificationCount.toString()
//        }
//        bell.setOnClickListener {
//            Log.e("Anirudh", "bell clicked")
//            this.startActivity(Intent(activity!!, NotificationActivity::class.java))
        }
    }

    private fun initNotifications() {
        if (context?.let { SharedPreferenceFactory(it).getString(Constants.ACCESS_TOKEN) } == "") {
            notauser.visibility = VISIBLE
            myActivityRecyclerView.visibility = GONE
//            bell.visibility = GONE
            initsigninbutton()
        } else {
            notauser.visibility = GONE
//            bell.visibility = VISIBLE
        }
    }

    private fun initsigninbutton() {
        sign_in_button_myActivity.setOnClickListener {
            startActivity(Intent(activity!!, LoginActivity::class.java))
            activity!!.finish()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_my_activity, container, false)
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
        sendRequest()
        val toolbar = toolbar as Toolbar
        toolbar.title = "My Activity"
        initNotifications()

    }

    private fun initRecyclerView(responseData: AllActivitiesModel) {
        if (responseData.activities.size == 0) {
            no_activities.visibility = VISIBLE
        } else {
            myActivityRecyclerView.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager?
            val adapter = activity?.let { MyActivityAdapter(myActivityList, it) }
            myActivityRecyclerView.adapter = adapter
            for (activity in responseData.activities) {
                myActivityList.add(
                    MyActivityModel(
                        activity.action,
                        activity.createdAt,
                        activity.springName,
                        activity.latitude.toString() + activity.longitude.toString(),
                        activity.springCode
                    )
                )
            }

        }
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
        if (SharedPreferenceFactory(activity!!.applicationContext).getInt(Constants.NOTIFICATION_COUNT)!! > 0){
            SharedPreferenceFactory(activity!!.applicationContext).getInt(Constants.NOTIFICATION_COUNT)?.let { initbell(it) }
        }
    }

    private fun initRepository() {
        mMyActivitiesViewModel = ViewModelProviders.of(this).get(MyActivitiesViewModel::class.java)
        mMyActivitiesViewModel.setMyActivitiesRepository(mMyActivitiesRepository)
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
        makeApiCall(mRequestData)
    }

    private fun makeApiCall(mRequestData: RequestModel) {
        mMyActivitiesViewModel.myActivitiesApi(activity!!.applicationContext, mRequestData)

    }

}
