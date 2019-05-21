package com.arghyam.myactivity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.myactivity.model.MyActivityModel
import kotlinx.android.synthetic.main.list_my_activity.view.*

class MyActivityAdapter(val myActivityList: ArrayList<MyActivityModel>, val context: Context):RecyclerView.Adapter<MyActivityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_my_activity,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
       return myActivityList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myActivities: MyActivityModel = myActivityList[position]
        holder.dischargeData.text = myActivities.dischargeData
        holder.time.text = myActivities.time
        holder.springName.text = myActivities.springName
        holder.villageName.text = myActivities.villageName

    }

    class ViewHolder (view: View): RecyclerView.ViewHolder(view){

        val dischargeData = view.dicharge_data
        val time = view.time_date
        val springName = view.name
        val villageName = view.village_name

    }
}