package com.arghyam.geographySearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.geographySearch.model.PanchayatModel
import kotlinx.android.synthetic.main.list_state.view.*

class PanchyatAdapter(val PanchayatList : ArrayList<PanchayatModel>, context: Context):RecyclerView.Adapter<PanchyatAdapter.PanchayatViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanchayatViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_state,parent,false)
        return PanchayatViewHolder(v)
    }

    override fun getItemCount(): Int {
        return PanchayatList.size
    }

    override fun onBindViewHolder(holder: PanchayatViewHolder, position: Int) {
        val panchayat: PanchayatModel = PanchayatList[position]
        holder.panchayatName.text = panchayat.panchayatName
    }

    class PanchayatViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val panchayatName = view.state_name
    }

}