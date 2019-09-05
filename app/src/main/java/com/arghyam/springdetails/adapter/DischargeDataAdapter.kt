package com.arghyam.springdetails.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.springdetails.models.DischargeDataModal
import kotlinx.android.synthetic.main.discharge_data_item.view.*

class DischargeDataAdapter(private val items: ArrayList<DischargeDataModal>, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.discharge_data_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: DischargeDataModal = items[position]
        holder.date.text = item.date
        holder.discharge.text = item.discharge
        holder.submittedBy.text = item.submitted

        Log.d("status--item", "" + item.status)
        if (item.status == "Accepted") {
            holder.tick.visibility = VISIBLE
        } else if (item.status == "Rejected") {
            holder.dischargeDataLayout.visibility = GONE
        } else {
            holder.tick.visibility = INVISIBLE
            holder.date.setTextColor(Color.parseColor("#8C8C8B"))
            holder.discharge.setTextColor(Color.parseColor("#8C8C8B"))
            holder.submittedBy.setTextColor(Color.parseColor("#8C8C8B"))
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tick: ImageView = view.dd_tick
    var date: TextView = view.dd_date
    var discharge: TextView = view.dd_discharge
    var submittedBy: TextView = view.dd_submitted
    val dischargeDataLayout: LinearLayout = view.discharge_data_layout
}