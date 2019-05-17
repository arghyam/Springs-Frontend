package com.arghyam.geographySearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.geographySearch.model.DistrictModel
import kotlinx.android.synthetic.main.list_state.view.*

class DistrictAdapter(val DistrictList : ArrayList<DistrictModel>, context: Context):RecyclerView.Adapter<DistrictAdapter.DistrictViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_state,parent,false)
        return DistrictViewHolder(v)
    }

    override fun getItemCount(): Int {
        return DistrictList.size
    }

    override fun onBindViewHolder(holder: DistrictViewHolder, position: Int) {
        val districts: DistrictModel = DistrictList[position]
        holder.districtName.text = districts.districtName
    }


    class DistrictViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val districtName = view.state_name
    }


}