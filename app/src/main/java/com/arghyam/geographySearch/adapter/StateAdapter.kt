package com.arghyam.geographySearch.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.geographySearch.interfaces.GeographyInterface
import com.arghyam.geographySearch.model.StateModel
import kotlinx.android.synthetic.main.list_state.view.*

class StateAdapter(val stateList : ArrayList<StateModel>, context: Context,val geographyInterface: GeographyInterface):RecyclerView.Adapter<StateAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_state,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return stateList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val states: StateModel = stateList[position]
        holder.stateName.text = states.stateName
        if (states.stateCount>0 && states.stateName == holder.stateName.text){
            Log.e("StateAdapter",states.stateName+states.stateCount+ holder.stateName.text)
            holder.stateCount.text = " ("+states.stateCount+")"
            holder.itemView.setOnClickListener {
                geographyInterface.onGeographyItemClickListener(position)
            }
        }
        else if (states.stateCount==0)
            holder.stateName.setTextColor(Color.parseColor("#e6e6e5"))
        holder.setIsRecyclable(false)

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val stateName = view.state_name!!
        val stateCount: TextView = view.state_count!!
    }


}