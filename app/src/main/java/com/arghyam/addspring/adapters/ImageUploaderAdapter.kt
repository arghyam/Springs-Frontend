package com.arghyam.addspring.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.addspring.entities.ImageEntity
import com.arghyam.addspring.interfaces.ImageUploadInterface
import com.arghyam.addspring.ui.NewSpringActivity
import kotlinx.android.synthetic.main.list_image_uploader.view.*



class ImageUploaderAdapter(
    private var context: Context,
    private var imageList: ArrayList<ImageEntity>,
    private var imageInterface: ImageUploadInterface
) :
    RecyclerView.Adapter<ImageUploaderAdapter.ViewHolder>() {

    interface MyListClickListener {
        fun onItemRemoveClick(name: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_image_uploader, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image: ImageEntity = imageList[position]
        holder.springImageName?.text = image.name
        holder.progressBar?.progress = image.uploadPercentage
        holder.springImage?.setImageBitmap(image.bitmap)
        holder.springRemove.setOnClickListener {
            imageInterface.onRemove(position)
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val springImageName = view.image_name
        val springRemove = view.close
        val springImage = view.image
        val progressBar = view.progress
    }
}