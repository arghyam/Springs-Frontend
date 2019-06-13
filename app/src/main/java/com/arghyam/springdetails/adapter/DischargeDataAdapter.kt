package com.arghyam.springdetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.springdetails.models.SpringProfileResponse
import kotlinx.android.synthetic.main.discharge_data_item.view.*

class DischargeDataAdapter(private val items : ArrayList<SpringProfileResponse>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.discharge_data_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item : SpringProfileResponse = items[position]
        holder.date.text = item.createdTimeStamp
//        holder.discharge.text = item.extraInformation.dischargeData
        holder.submittedBy.text = item.ownershipType
//        holder.tick.visibility = VISIBLE
//        if(!item.isVerified) {
//            holder.date.setTextColor(context.resources.getColor(R.color.jumbo))
//            holder.discharge.setTextColor(context.resources.getColor(R.color.jumbo))
//            holder.submittedBy.setTextColor(context.resources.getColor(R.color.jumbo))
//            holder.tick.visibility = INVISIBLE
//        }
    }
    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tick: ImageView = view.dd_tick
    val date : TextView = view.dd_date
    val discharge : TextView = view.dd_discharge
    val submittedBy : TextView = view.dd_submitted
}