package com.arghyam.notification.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import com.arghyam.notification.adapter.NotificationAdapter
import com.arghyam.notification.model.NotificationDataModel
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {

    private var adapter: NotificationAdapter? = null
    var dataModels: ArrayList<NotificationDataModel>? = ArrayList()
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        initToolBar()

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

        Log.e("Anirudh", dataModels.toString())
        adapter = this.dataModels?.let { NotificationAdapter(applicationContext, it) }
        listView.adapter = adapter

        listView.onItemClickListener =

            AdapterView.OnItemClickListener { adapterView: AdapterView<*>, view: View, position: Int, l: Long ->
                val (notification, time, date) = dataModels!![position]
                Log.e("Anirudh", "Clicked$notification $time $date")
                this.startActivity(Intent(this, DisplayDischargeDataActivity::class.java))
            }
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




