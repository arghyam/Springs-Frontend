package com.arghyam.addspring.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.addspring.entities.ImageEntity
import kotlinx.android.synthetic.main.list_image_uploader.view.*
import kotlinx.android.synthetic.main.list_spring.view.*

class ImageUploaderAdapter(val imageList: ArrayList<ImageEntity>): RecyclerView.Adapter<ImageUploaderAdapter.ViewHolder>() {

    interface MyListClickListener {
        fun onItemRemoveClick(name : String,position : Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.list_image_uploader,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image: ImageEntity = imageList[position]

        holder.springImageName?.text = image.name
        holder.springImage?.setImageBitmap(image.bitmap)
        holder.springRemove.setOnClickListener{
            imageList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,imageList.size)
        }
    }





    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val springImageName = view.image_name
        val springRemove = view.close
        val springImage = view.image
    }
}