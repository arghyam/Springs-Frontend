package com.arghyam.myactivity.adapter

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.myactivity.model.Activities
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import kotlinx.android.synthetic.main.list_my_activity.view.*

class MyActivityAdapter(val myActivityList: ArrayList<Activities>, val context: Context):RecyclerView.Adapter<MyActivityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_my_activity,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
       return myActivityList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myActivities: Activities = myActivityList[position]
        holder.dischargeData.text = myActivities.action
        holder.time.text = ArghyamUtils().epochToTime(myActivities.createdAt) + "  |  "+ArghyamUtils().epochToDateFormat(myActivities.createdAt)
        holder.springName.text = myActivities.springName
        val geocoder = Geocoder(context)
        val address = geocoder.getFromLocation(myActivities.latitude.toDouble(),
            myActivities.longitude.toDouble(),1)
        holder.villageName.text = address[0].locality+", "+address[0].adminArea

        holder.activity.setOnClickListener {
            var dataIntent = Intent(context, SpringDetailsActivity::class.java)
            if (myActivities.action.contains("discharge",true)){
                Log.e("MyActivitesAdapter","discharge")
                dataIntent.putExtra("flag",true)
            }
            else {
                Log.e("MyActivitesAdapter","normal")
            }
            dataIntent.putExtra("SpringCode", myActivities.springCode)
            dataIntent.putExtra("SpringName", myActivities.springName)
            context.startActivity(dataIntent)

            return@setOnClickListener
        }

    }

    class ViewHolder (view: View): RecyclerView.ViewHolder(view){

        val dischargeData = view.dicharge_data
        val time = view.time_date
        val springName = view.name
        val villageName = view.location
        val activity = view.my_activity_relative_layout

    }
}