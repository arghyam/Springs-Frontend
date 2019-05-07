package com.arghyam.example.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.example_item.view.*

class ExampleViewAdapter(val items : List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.example_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.itemIndex?.text = "" + (position + 1)
        Glide.with(context).load(items[position]).into(holder?.itemImage)
    }
    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val itemIndex = view.item_count
    val itemImage = view.item_image
}