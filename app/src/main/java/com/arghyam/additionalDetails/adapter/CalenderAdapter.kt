package com.arghyam.additionalDetails.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.search.adapter.RecentSearchAdapter
import kotlinx.android.synthetic.main.list_calender.view.*

class CalenderAdapter(val calenderItems: ArrayList<String>, val context: Context) : RecyclerView.Adapter<CalenderAdapter.ViewHolder> (){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_calender,parent,false))
    }

    override fun getItemCount(): Int {
       return calenderItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.months.text=calenderItems.get(position)
        holder.months.setOnClickListener {
           var row_index= position;
            notifyDataSetChanged();

            if(row_index==position){
                holder?.months.setBackgroundColor((Color.parseColor("#2372D9")))
                holder.months.setTextColor(Color.parseColor("#ffffff"))
            } else{
                holder?.months.setBackgroundColor((Color.parseColor("#cccccc")))
                holder.months.setTextColor(Color.parseColor("#595958"))
            }

        }


    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val months = view.bt_jan
    }
}