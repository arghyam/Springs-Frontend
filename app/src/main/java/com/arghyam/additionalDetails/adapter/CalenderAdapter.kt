package com.arghyam.additionalDetails.adapter


import android.content.Context
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import kotlinx.android.synthetic.main.list_calender.view.*

class CalenderAdapter(
    private val calender: ArrayList<String> = ArrayList(),
    var context: Context,
    internal var listener: OnRecyclerViewItemClickListener
) : RecyclerView.Adapter<CalenderAdapter.ViewHolder>() {


    internal var sparseBooleanArray: SparseBooleanArray // for identifying: in list which items are selected
    internal var selectedItemCount: Int = 0

    init {
        sparseBooleanArray = SparseBooleanArray()
        selectedItemCount = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_calender, parent, false))
    }

    override fun onBindViewHolder(holder: CalenderAdapter.ViewHolder, position: Int) {
        holder.months.text = calender[position]


        if (sparseBooleanArray.get(position)) {
            holder.months.setBackgroundColor(context.resources.getColor(R.color.colorAccent))
            holder.months.setTextColor(context.resources.getColor(R.color.white))
        } else {
            holder.months.setBackgroundColor(context.resources.getColor(R.color.gainsboro_50))
            holder.months.setTextColor(context.resources.getColor(R.color.ship_grey))
        }
    }

    override fun getItemCount(): Int {
        return calender.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var months: Button

        init {
            months = itemView.bt_jan
            months.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            listener.selectedMonth(position) // calling the method in main activity Because: in our case mainActivity our created interface for clicklisteners

            if (!sparseBooleanArray.get(adapterPosition)) {
                sparseBooleanArray.put(adapterPosition, true)



                notifyItemChanged(adapterPosition)
            } else
            // if clicked item is already selected
            {
                sparseBooleanArray.put(adapterPosition, false)

                notifyItemChanged(adapterPosition)
            }

        }
    }


    fun clear(){
        for (i in 0..12){
            sparseBooleanArray.put(i, false)
            notifyDataSetChanged()
        }
    }
    interface OnRecyclerViewItemClickListener {
        fun selectedMonth(position: Int)

    }
}
