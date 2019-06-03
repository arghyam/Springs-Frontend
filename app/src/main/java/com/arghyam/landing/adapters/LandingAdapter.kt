package com.arghyam.landing.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.landing.model.LandingModel
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_spring.view.*

class LandingAdapter(val springList: ArrayList<LandingModel>, val context: Context) :
    RecyclerView.Adapter<LandingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_spring, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return springList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val springs: LandingModel = springList[position]
        holder.springNameText.text = springs.springName
        holder.villageNameText.text = springs.villageName
        Glide.with(context)
            .load(springs.springImage)
            .into(holder.springImage)
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, SpringDetailsActivity::class.java))
        }
        holder.favourite.setOnClickListener {
//                holder.favourite.setBackgroundResource(R.drawable.ic_fav_fill)
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val springNameText = view.spring_name
        val villageNameText = view.village_name
        val springImage = view.img_spring
        val favourite = view.fav_icon

    }
}