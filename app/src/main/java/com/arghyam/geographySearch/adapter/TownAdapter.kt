package com.arghyam.geographySearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.geographySearch.model.TownModel
import kotlinx.android.synthetic.main.list_state.view.*

class TownAdapter(val TownList : ArrayList<TownModel>, context: Context):RecyclerView.Adapter<TownAdapter.TownViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TownViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_state,parent,false)
        return TownViewHolder(v)
    }

    override fun getItemCount(): Int {
        return TownList.size
    }

    override fun onBindViewHolder(holder: TownViewHolder, position: Int) {
        val town: TownModel = TownList[position]
        holder.townName.text = town.townName
    }


    class TownViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val townName = view.state_name
    }


}