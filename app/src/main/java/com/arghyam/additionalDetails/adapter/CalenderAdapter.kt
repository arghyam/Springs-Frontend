package com.arghyam.additionalDetails.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import kotlinx.android.synthetic.main.list_calender.view.*
class CalenderAdapter(val calenderItems: ArrayList<String>, val context: Context) : RecyclerView.Adapter<CalenderAdapter.ViewHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_calender,parent,false))
    }
    override fun getItemCount(): Int {
        return calenderItems.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.months.text=calenderItems.get(position)
    }
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val months = view.bt_jan
    }
}