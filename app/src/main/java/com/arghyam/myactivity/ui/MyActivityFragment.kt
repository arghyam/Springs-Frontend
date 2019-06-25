package com.arghyam.myactivity.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.arghyam.R
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.myactivity.adapter.MyActivityAdapter
import com.arghyam.myactivity.model.MyActivityModel
import com.arghyam.notification.ui.activity.NotificationActivity
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_my_activity.*
import kotlinx.android.synthetic.main.fragment_my_activity.notauser
import kotlinx.android.synthetic.main.fragment_my_activity.toolbar
import kotlinx.android.synthetic.main.toolbar.*


class MyActivityFragment : Fragment() {
    private var notification: Boolean = true

    private var myActivityList = ArrayList<MyActivityModel>()

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

    private fun initbell(notificationCount:Int) {
        if(notificationCount>0){
            badge.visibility = View.VISIBLE
            notification_count.text = notificationCount.toString()
        }
        bell.setOnClickListener{
            Log.e("Anirudh", "bell clicked")
            this.startActivity(Intent(activity!!, NotificationActivity::class.java))
        }
    }

    private fun initNotifications() {
        if (context?.let { SharedPreferenceFactory(it).getString(Constants.ACCESS_TOKEN) } == ""){
            notauser.visibility = View.VISIBLE
            myActivityRecyclerView.visibility = GONE
            bell.visibility = GONE
            initsigninbutton()
        }
        else{
            notauser.visibility = GONE
            bell.visibility = View.VISIBLE
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
        initRecyclerView()
        val toolbar = toolbar as Toolbar
        toolbar.title = "My Activity"
        initNotifications()

    }

    private fun initRecyclerView() {
        myActivityRecyclerView.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager?
        val adapter = activity?.let { MyActivityAdapter(myActivityList, it) }
        myActivityRecyclerView.adapter = adapter

        myActivityList.add(MyActivityModel("Discharge data added","01:35 PM | Apr 21,2019","Gaurikund","Kedarnath, Uttarakhand"))
        myActivityList.add(MyActivityModel("Discharge data added","01:35 PM | Apr 21,2019","Gaurikund","Kedarnath, Uttarakhand"))
        myActivityList.add(MyActivityModel("Spring added","01:35 PM | Apr 21,2019","Gaurikund","Kedarnath, Uttarakhand"))
        myActivityList.add(MyActivityModel("Spring added","01:35 PM | Apr 21,2019","Kheer Ganga","Kedarnath, Uttarakhand"))
        myActivityList.add(MyActivityModel("Discharge data added","01:35 PM | Apr 21,2019","Gaurikund","Kedarnath, Uttarakhand"))
        myActivityList.add(MyActivityModel("Discharge data added","01:35 PM | Apr 21,2019","Gaurikund","Kedarnath, Uttarakhand"))
        myActivityList.add(MyActivityModel("Discharge data added","01:35 PM | Apr 21,2019","Gaurikund","Kedarnath, Uttarakhand"))
        myActivityList.add(MyActivityModel("Discharge data added","01:35 PM | Apr 21,2019","Gaurikund","Kedarnath, Uttarakhand"))

    }

}
