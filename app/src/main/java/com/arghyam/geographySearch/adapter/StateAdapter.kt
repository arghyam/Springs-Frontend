package com.arghyam.geographySearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.geographySearch.model.StateModel
import kotlinx.android.synthetic.main.list_state.view.*

class StateAdapter(val stateList : ArrayList<StateModel>, context: Context):RecyclerView.Adapter<StateAdapter.ViewHolder>(){
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
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val stateName = view.state_name
    }


}