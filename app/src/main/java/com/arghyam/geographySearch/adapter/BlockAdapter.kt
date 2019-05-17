package com.arghyam.geographySearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.geographySearch.model.BlockModel
import kotlinx.android.synthetic.main.list_state.view.*

class BlockAdapter(val BlockList : ArrayList<BlockModel>, context: Context):RecyclerView.Adapter<BlockAdapter.BlockViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_state,parent,false)
        return BlockViewHolder(v)
    }

    override fun getItemCount(): Int {
        return BlockList.size
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val blocks: BlockModel = BlockList[position]
        holder.blockName.text = blocks.blockName
    }


    class BlockViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val blockName = view.state_name
    }


}