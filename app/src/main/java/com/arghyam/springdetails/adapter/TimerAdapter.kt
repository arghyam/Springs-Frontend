package com.arghyam.springdetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.springdetails.interfaces.TimerInterface
import com.arghyam.springdetails.models.TimerModel
import kotlinx.android.synthetic.main.timer_item.view.*

class TimerAdapter(private var items: ArrayList<TimerModel>, val context: Context, var timerInterface: TimerInterface) :
    RecyclerView.Adapter<TimerViewHolder>() {

    var selectedItem: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        return TimerViewHolder(LayoutInflater.from(context).inflate(R.layout.timer_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateSelectedItem(position: Int) {
        selectedItem = position
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        if (selectedItem == position) {
            initSelectedItem(holder)
        } else {
            initDefaultItem(holder)
        }
        holder.itemView.setOnClickListener {
            timerInterface.onItemSelected(position)
            selectedItem = position
            notifyDataSetChanged()
        }
        holder.counter.text = "${position + 1}"
        holder.attemptCounter.text = "Attempt ${position + 1}"
        holder.itemTimer.text = ArghyamUtils().secondsToMinutes(items[position].seconds)
    }

    private fun initSelectedItem(holder: TimerViewHolder) {
        holder.timerDelete.visibility = View.VISIBLE
        holder.timerDelete.background = context.resources.getDrawable(R.drawable.ic_close)
        holder.timerDelete.setOnClickListener {

        }
        holder.counter.background = context.resources.getDrawable(R.drawable.timer_active)
        holder.counter.setTextColor(context.resources.getColor(R.color.colorPrimary))
        holder.attemptCounter.setTextColor(context.resources.getColor(R.color.bokara_grey))
        holder.itemTimer.setTextColor(context.resources.getColor(R.color.bokara_grey))
    }

    private fun initDefaultItem(holder: TimerViewHolder) {
        holder.timerDelete.visibility = View.INVISIBLE
        holder.counter.background = context.resources.getDrawable(R.drawable.timer_inactive)
        holder.counter.setTextColor(context.resources.getColor(R.color.jumbo))
        holder.attemptCounter.setTextColor(context.resources.getColor(R.color.jumbo))
        holder.itemTimer.setTextColor(context.resources.getColor(R.color.jumbo))
    }

}

class TimerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val counter: TextView = view.timer_counter
    val attemptCounter: TextView = view.attempt_counter
    val itemTimer: TextView = view.item_timer
    val timerDelete: ImageView = view.timer_delete
}